package com.tengke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 评分策略配置注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScoringStrategyConfig {
    /**
     * 应用类型（0-打分类，1-测评类）
     */
    int appType();

    /**
     * 评分策略（0-自定义打分，1-自定义测评，2-AI测评）
     */
    int scoringStrategy();
}
