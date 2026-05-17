package com.tengke.strategy.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tengke.annotation.ScoringStrategyConfig;
import com.tengke.common.ErrorCode;
import com.tengke.constant.AiConstant;
import com.tengke.exception.BusinessException;
import com.tengke.manager.AiManager;
import com.tengke.model.domain.App;
import com.tengke.model.domain.UserAnswer;
import com.tengke.model.dto.question.QuestionContentDTO;
import com.tengke.service.QuestionService;
import com.tengke.strategy.ScoringStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI打分类评分策略（打分类 - AI评分）
 * 不影响现有的测评类 / MBTI 策略
 */
@Service
@ScoringStrategyConfig(appType = 0, scoringStrategy = 2)
@Slf4j
public class AiScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        // 获取该应用的题目列表
        List<QuestionContentDTO> questionList = questionService.getQuestionListByAppId(app.getId());

        // 构建AI评分Prompt（题目列表 + 用户选择）
        String questionJson = JSONUtil.toJsonStr(questionList);
        String choiceJson = JSONUtil.toJsonStr(choices);
        String prompt = String.format(
                AiConstant.AI_SCORE_SCORING_SYSTEM_MESSAGE,
                app.getAppName(),
                app.getAppDesc(),
                questionJson,
                choiceJson
        );

        // 调用AI
        String aiResult = aiManager.doSyncStableRequest(prompt);
        log.info("AI打分类原始结果：{}", aiResult);

        // 提取每题解析说明（来自原始文本中的 // 注释部分），仅用于展示，不参与解析
        StringBuilder explanationBuilder = new StringBuilder();
        for (String line : aiResult.split("\n")) {
            int commentIdx = line.indexOf("//");
            if (commentIdx >= 0 && commentIdx + 2 < line.length()) {
                String comment = line.substring(commentIdx + 2).trim();
                if (!comment.isEmpty()) {
                    explanationBuilder.append(comment).append("\n");
                }
            }
        }
        String explanation = explanationBuilder.toString().trim();

        // 清洗AI返回，避免 markdown / 多余文本 导致解析失败
        String cleaned = aiResult.trim();
        if (cleaned.startsWith("```")) {
            int firstLineEnd = cleaned.indexOf('\n');
            if (firstLineEnd > 0) {
                cleaned = cleaned.substring(firstLineEnd + 1);
            } else {
                cleaned = cleaned.replaceFirst("```json", "")
                        .replaceFirst("```", "");
            }
            int lastFence = cleaned.lastIndexOf("```");
            if (lastFence >= 0) {
                cleaned = cleaned.substring(0, lastFence);
            }
            cleaned = cleaned.trim();
        }

        // 去掉每行中的 // 注释（AI 可能会在 JSON 后面加中文解释）
        StringBuilder noCommentBuilder = new StringBuilder();
        for (String line : cleaned.split("\n")) {
            int commentIdx = line.indexOf("//");
            if (commentIdx >= 0) {
                line = line.substring(0, commentIdx);
            }
            noCommentBuilder.append(line).append('\n');
        }
        cleaned = noCommentBuilder.toString().trim();

        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        log.info("AI打分类清洗后结果：{}", cleaned);

        // 解析总分
        JSONObject obj;
        try {
            obj = JSONUtil.parseObj(cleaned);
        } catch (Exception e) {
            log.error("AI打分类结果解析失败，原始内容：{}, 清洗后内容：{}", aiResult, cleaned, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI打分类结果格式不正确，请稍后重试");
        }

        Integer totalScore = obj.getInt("totalScore");
        if (totalScore == null) {
            log.error("AI打分类结果中缺少 totalScore 字段，内容：{}", cleaned);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI打分类结果缺少总分，请稍后重试");
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setTotalScore(totalScore);
        // 将AI返回的解析说明一起存入结果描述，方便前端展示
        if (!explanation.isEmpty()) {
            userAnswer.setResultDesc(explanation);
        }
        return userAnswer;
    }
}

