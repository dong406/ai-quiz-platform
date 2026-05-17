package com.tengke.controller;

import com.tengke.common.BaseResponse;
import com.tengke.common.ErrorCode;
import com.tengke.common.ResultUtils;
import com.tengke.model.domain.UserAnswer;
import com.tengke.model.dto.scoring.ScoringRequest;
import com.tengke.model.dto.scoring.UserAnswerRecordAddRequest;
import com.tengke.service.ScoringService;
import com.tengke.service.UserAnswerRecordService;
import com.tengke.utils.ThrowUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 评分接口
 */
@RestController
@RequestMapping("/scoring")
public class ScoringController {

    @Resource
    private ScoringService scoringService;

    @Resource
    private UserAnswerRecordService userAnswerRecordService;

    @PostMapping("/do")
    public BaseResponse<Map<String, Object>> doScoring(@RequestBody ScoringRequest scoringRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(scoringRequest == null, ErrorCode.PARAMS_ERROR);
        // 执行评分
        UserAnswer userAnswer = scoringService.doScoring(scoringRequest, request);
        // 保存答题记录
        UserAnswerRecordAddRequest recordAddRequest = new UserAnswerRecordAddRequest();
        if (scoringRequest != null) {
            recordAddRequest.setAppId(scoringRequest.getAppId());
            recordAddRequest.setChoices(scoringRequest.getChoices());
        }
        recordAddRequest.setTotalScore(userAnswer.getTotalScore());
        recordAddRequest.setResultName(userAnswer.getResultName());
        recordAddRequest.setResultDesc(userAnswer.getResultDesc());
        Long recordId = userAnswerRecordService.addUserAnswerRecord(recordAddRequest, request);
        // 返回评分结果和记录ID
        Map<String, Object> result = new HashMap<>();
        result.put("userAnswer", userAnswer);
        result.put("recordId", recordId);
        return ResultUtils.success(result);
    }
}
