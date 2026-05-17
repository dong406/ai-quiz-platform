package com.tengke.service.impl;

import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;
import com.tengke.model.domain.App;
import com.tengke.model.domain.User;
import com.tengke.model.domain.UserAnswer;
import com.tengke.model.dto.scoring.ScoringRequest;
import com.tengke.service.AppService;
import com.tengke.service.ScoringService;
import com.tengke.service.UserService;
import com.tengke.strategy.ScoringStrategy;
import com.tengke.strategy.factory.ScoringStrategyFactory;
import com.tengke.utils.ThrowUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 评分服务实现类
 */
@Service
public class ScoringServiceImpl implements ScoringService {

    @Resource
    private ScoringStrategyFactory scoringStrategyFactory;

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    @Override
    public UserAnswer doScoring(ScoringRequest scoringRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(scoringRequest == null || scoringRequest.getAppId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(scoringRequest.getChoices() == null || scoringRequest.getChoices().isEmpty(), 
                ErrorCode.PARAMS_ERROR, "答题选项不能为空");
        // 查询应用信息
        App app = appService.getById(scoringRequest.getAppId());
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取评分策略
        ScoringStrategy strategy = scoringStrategyFactory.getScoringStrategy(app.getAppType(), app.getScoringStrategy());
        ThrowUtils.throwIf(strategy == null, ErrorCode.SYSTEM_ERROR, "未找到对应的评分策略");
        // 执行评分
        return strategy.doScore(scoringRequest.getChoices(), app);
    }
}
