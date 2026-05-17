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
import com.tengke.strategy.ScoringStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI测评策略（测评类-AI测评）
 */
@Service
@ScoringStrategyConfig(appType = 1, scoringStrategy = 2)
@Slf4j
public class AiTestScoringStrategy implements ScoringStrategy {

    @Resource
    private AiManager aiManager;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        // 构建AI评分Prompt
        String prompt = String.format(AiConstant.AI_TEST_SCORING_SYSTEM_MESSAGE, 
                app.getAppName(), 
                app.getAppDesc(), 
                JSONUtil.toJsonStr(choices));
        // 调用AI
        String aiResult = aiManager.doSyncStableRequest(prompt);
        log.info("AI测评原始结果：{}", aiResult);

        // 清洗AI返回，去掉可能的markdown代码块和多余文字，只保留JSON对象
        String cleaned = aiResult.trim();
        // 去掉 ```json / ``` 包裹
        if (cleaned.startsWith("```")) {
            // 去掉开头 ```xxx\n
            int firstLineEnd = cleaned.indexOf('\n');
            if (firstLineEnd > 0) {
                cleaned = cleaned.substring(firstLineEnd + 1);
            } else {
                cleaned = cleaned.replaceFirst("```json", "")
                                 .replaceFirst("```", "");
            }
            // 去掉结尾 ```
            int lastFence = cleaned.lastIndexOf("```");
            if (lastFence >= 0) {
                cleaned = cleaned.substring(0, lastFence);
            }
            cleaned = cleaned.trim();
        }

        // 只截取第一个完整的大括号对象
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        log.info("AI测评清洗后结果：{}", cleaned);

        // 解析结果
        JSONObject resultObj;
        try {
            resultObj = JSONUtil.parseObj(cleaned);
        } catch (Exception e) {
            log.error("AI测评结果解析失败，原始内容：{}, 清洗后内容：{}", aiResult, cleaned, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI测评结果格式不正确，请稍后重试");
        }
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setResultName(resultObj.getStr("resultName"));
        userAnswer.setResultDesc(resultObj.getStr("resultDesc"));
        return userAnswer;
    }
}
