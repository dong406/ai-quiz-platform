package com.tengke.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengke.common.BaseResponse;
import com.tengke.common.ErrorCode;
import com.tengke.common.ResultUtils;
import com.tengke.exception.BusinessException;
import com.tengke.model.dto.scoring.UserAnswerRecordAddRequest;
import com.tengke.model.vo.UserAnswerRecordVO;
import com.tengke.service.UserAnswerRecordService;
import com.tengke.utils.ThrowUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户答题记录接口
 */
@RestController
@RequestMapping("/userAnswerRecord")
public class UserAnswerRecordController {

    @Resource
    private UserAnswerRecordService userAnswerRecordService;

    @PostMapping("/add")
    public BaseResponse<Long> addUserAnswerRecord(@RequestBody UserAnswerRecordAddRequest userAnswerRecordAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerRecordAddRequest == null, ErrorCode.PARAMS_ERROR);
        Long recordId = userAnswerRecordService.addUserAnswerRecord(userAnswerRecordAddRequest, request);
        return ResultUtils.success(recordId);
    }

    @GetMapping("/list")
    public BaseResponse<Page<UserAnswerRecordVO>> listUserAnswerRecord(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long appId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize) {
        Page<UserAnswerRecordVO> recordPage = userAnswerRecordService.listUserAnswerRecord(userId, appId, current, pageSize);
        return ResultUtils.success(recordPage);
    }

    @GetMapping("/get")
    public BaseResponse<UserAnswerRecordVO> getUserAnswerRecordById(@RequestParam Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        UserAnswerRecordVO recordVO = userAnswerRecordService.getUserAnswerRecordById(id);
        return ResultUtils.success(recordVO);
    }
}
