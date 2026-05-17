package com.tengke.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户答题记录视图对象
 */
@Data
public class UserAnswerRecordVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 答题选项（JSON数组）
     */
    private String choices;

    /**
     * 总分（打分类）
     */
    private Integer totalScore;

    /**
     * 结果名称（测评类）
     */
    private String resultName;

    /**
     * 结果描述（测评类）
     */
    private String resultDesc;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
