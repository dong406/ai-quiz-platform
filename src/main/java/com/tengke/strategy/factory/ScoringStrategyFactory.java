package com.tengke.strategy.factory;

import com.tengke.annotation.ScoringStrategyConfig;
import com.tengke.strategy.ScoringStrategy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评分策略工厂
 */
@Service
public class ScoringStrategyFactory {

    private final Map<String, ScoringStrategy> strategyMap = new HashMap<>();

    private final List<ScoringStrategy> scoringStrategyList;

    public ScoringStrategyFactory(List<ScoringStrategy> scoringStrategyList) {
        this.scoringStrategyList = scoringStrategyList;
    }

    @PostConstruct
    public void init() {
        // 自动注入所有评分策略实现类
        for (ScoringStrategy strategy : scoringStrategyList) {
            ScoringStrategyConfig annotation = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
            if (annotation != null) {
                String key = annotation.appType() + "_" + annotation.scoringStrategy();
                strategyMap.put(key, strategy);
            }
        }
    }

    /**
     * 获取评分策略
     */
    public ScoringStrategy getScoringStrategy(int appType, int scoringStrategy) {
        String key = appType + "_" + scoringStrategy;
        return strategyMap.get(key);
    }
}
