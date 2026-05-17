package com.tengke.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengke.common.ErrorCode;
import com.tengke.constant.UserConstant;
import com.tengke.mapper.AppMapper;
import com.tengke.model.domain.App;
import com.tengke.model.domain.User;
import com.tengke.model.dto.app.AppAddRequest;
import com.tengke.model.dto.app.AppReviewRequest;
import com.tengke.model.vo.AppVO;
import com.tengke.service.AppService;
import com.tengke.service.UserService;
import com.tengke.utils.ThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 应用服务实现类
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Override
    public Long addApp(AppAddRequest appAddRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 创建应用
        App app = new App();
        BeanUtils.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        app.setReviewStatus(0); // 待审核
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return app.getId();
    }

    @Override
    public Boolean reviewApp(AppReviewRequest appReviewRequest) {
        ThrowUtils.throwIf(appReviewRequest == null || appReviewRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(appReviewRequest.getReviewStatus() == null, ErrorCode.PARAMS_ERROR);
        // 查询应用
        App app = this.getById(appReviewRequest.getId());
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新审核状态
        app.setReviewStatus(appReviewRequest.getReviewStatus());
        app.setReviewMessage(appReviewRequest.getReviewMessage());
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtils.copyProperties(app, appVO);
        return appVO;
    }

    @Override
    public Boolean deleteApp(Long id, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询应用是否存在
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在或已删除");
        // 权限校验：只有创建者或管理员可以删除
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isOwner = app.getUserId() != null && app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isOwner, ErrorCode.NO_AUTH_ERROR, "无权限删除该应用");
        // 逻辑删除（使用MyBatis Plus的@TableLogic）
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除应用失败");
        return true;
    }
}
