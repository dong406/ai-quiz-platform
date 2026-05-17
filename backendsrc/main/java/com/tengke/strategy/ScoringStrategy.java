package com.tengke.strategy;

import com.tengke.model.domain.App;
import com.tengke.model.domain.UserAnswer;

import java.util.List;

/**
 * 评分策略接口
 */
public interface ScoringStrategy {
    /**
     * 执行评分
     * @param choices 用户答题选项
     * @param app 应用信息
     * @return 评分结果
     */
    UserAnswer doScore(List<String> choices, App app);
}
