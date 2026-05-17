package com.tengke.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tengke.model.domain.App;
import com.tengke.model.dto.app.AppAddRequest;
import com.tengke.model.dto.app.AppReviewRequest;
import com.tengke.model.vo.AppVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 应用服务
 */
public interface AppService extends IService<App> {
    /**
     * 创建应用
     */
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    /**
     * 审核应用
     */
    Boolean reviewApp(AppReviewRequest appReviewRequest);

    /**
     * 获取应用VO
     */
    AppVO getAppVO(App app);

    /**
     * 删除应用
     */
    Boolean deleteApp(Long id, HttpServletRequest request);
}
