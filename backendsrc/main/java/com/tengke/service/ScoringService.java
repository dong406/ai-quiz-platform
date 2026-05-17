package com.tengke.service;

import com.tengke.model.domain.UserAnswer;
import com.tengke.model.dto.scoring.ScoringRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 评分服务
 */
public interface ScoringService {
    /**
     * 执行评分
     */
    UserAnswer doScoring(ScoringRequest scoringRequest, HttpServletRequest request);
}
