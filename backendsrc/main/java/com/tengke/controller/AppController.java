package com.tengke.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengke.annotation.AuthCheck;
import com.tengke.common.BaseResponse;
import com.tengke.common.ErrorCode;
import com.tengke.common.ResultUtils;
import com.tengke.constant.UserConstant;
import com.tengke.model.domain.App;
import com.tengke.model.dto.app.AppAddRequest;
import com.tengke.model.dto.app.AppReviewRequest;
import com.tengke.model.vo.AppVO;
import com.tengke.service.AppService;
import com.tengke.utils.ThrowUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用接口
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;

    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        // 参数校验（参考用户模块）
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 业务逻辑
        Long appId = appService.addApp(appAddRequest, request);
        return ResultUtils.success(appId);
    }

    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewApp(@RequestBody AppReviewRequest appReviewRequest) {
        // 应用审核接口（仅管理员可调用）
        ThrowUtils.throwIf(appReviewRequest == null, ErrorCode.PARAMS_ERROR);
        Boolean result = appService.reviewApp(appReviewRequest);
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    public BaseResponse<Page<AppVO>> listApp(
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) Integer appType,
            @RequestParam(required = false) Integer reviewStatus,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            HttpServletRequest request) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(appName != null, App::getAppName, appName);
        queryWrapper.eq(appType != null, App::getAppType, appType);
        // 按传入的审核状态过滤；不传则不过滤（便于创建后立即可见）
        queryWrapper.eq(reviewStatus != null, App::getReviewStatus, reviewStatus);
        queryWrapper.orderByDesc(App::getCreateTime);
        Page<App> appPage = appService.page(new Page<>(current, pageSize), queryWrapper);
        Page<AppVO> appVOPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        List<AppVO> appVOList = appPage.getRecords().stream()
                .map(appService::getAppVO)
                .collect(Collectors.toList());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    @GetMapping("/get")
    public BaseResponse<AppVO> getAppById(@RequestParam Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        AppVO appVO = appService.getAppVO(app);
        return ResultUtils.success(appVO);
    }

    /**
     * 删除应用
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Boolean result = appService.deleteApp(id, request);
        return ResultUtils.success(result);
    }
}
