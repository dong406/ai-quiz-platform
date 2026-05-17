package com.tengke.model.dto.app;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 应用添加请求
 */
@Data
public class AppAddRequest implements Serializable {
    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空")
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用封面
     */
    private String appCover;

    /**
     * 应用类型（0-打分类，1-测评类）
     */
    @NotNull(message = "应用类型不能为空")
    private Integer appType;

    /**
     * 评分策略（0-自定义打分，1-自定义测评，2-AI测评）
     */
    @NotNull(message = "评分策略不能为空")
    private Integer scoringStrategy;

    private static final long serialVersionUID = 1L;
}
