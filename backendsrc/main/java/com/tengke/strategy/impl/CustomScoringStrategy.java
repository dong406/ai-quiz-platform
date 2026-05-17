package com.tengke.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.tengke.annotation.ScoringStrategyConfig;
import com.tengke.model.domain.App;
import com.tengke.model.domain.UserAnswer;
import com.tengke.model.dto.question.QuestionContentDTO;
import com.tengke.service.QuestionService;
import com.tengke.strategy.ScoringStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义打分策略（打分类-自定义打分）
 */
@Service
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        // 获取题目列表
        List<QuestionContentDTO> questionList = questionService.getQuestionListByAppId(app.getId());
        // 计算总分
        int totalScore = 0;
        for (int i = 0; i < questionList.size() && i < choices.size(); i++) {
            QuestionContentDTO question = questionList.get(i);
            String choice = choices.get(i);
            // 根据选项key匹配分数（简化实现，实际需要根据选项配置计算）
            if (question.getScore() != null) {
                totalScore += question.getScore();
            }
        }
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setTotalScore(totalScore);
        return userAnswer;
    }
}
