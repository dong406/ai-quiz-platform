package com.tengke.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用
 */
@TableName(value = "app")
@Data
public class App implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用描述
     */
    @TableField("app_desc")
    private String appDesc;

    /**
     * 应用封面
     */
    @TableField("app_cover")
    private String appCover;

    /**
     * 应用类型（0-打分类，1-测评类）
     */
    @TableField("app_type")
    private Integer appType;

    /**
     * 评分策略（0-自定义打分，1-自定义测评，2-AI测评）
     */
    @TableField("scoring_strategy")
    private Integer scoringStrategy;

    /**
     * 审核状态（0-待审核，1-通过，2-拒绝）
     */
    @TableField("review_status")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @TableField("review_message")
    private String reviewMessage;

    /**
     * 创建用户 id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
