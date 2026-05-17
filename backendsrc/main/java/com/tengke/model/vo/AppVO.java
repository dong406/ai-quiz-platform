package com.tengke.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用视图对象
 */
@Data
public class AppVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
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
    private Integer appType;

    /**
     * 评分策略（0-自定义打分，1-自定义测评，2-AI测评）
     */
    private Integer scoringStrategy;

    /**
     * 审核状态（0-待审核，1-通过，2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
