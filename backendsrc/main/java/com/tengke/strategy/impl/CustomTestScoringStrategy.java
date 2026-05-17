package com.tengke.strategy.impl;

import com.tengke.annotation.ScoringStrategyConfig;
import com.tengke.model.domain.App;
import com.tengke.model.domain.UserAnswer;
import com.tengke.strategy.ScoringStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义测评策略（测评类-自定义测评）
 */
@Service
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        // 根据用户选择的选项，匹配题目中的result字段
        // 简化实现，实际需要根据题目配置匹配结果
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setResultName("自定义测评结果");
        userAnswer.setResultDesc("根据您的答题情况，您的测评结果为：自定义测评结果");
        return userAnswer;
    }
}
