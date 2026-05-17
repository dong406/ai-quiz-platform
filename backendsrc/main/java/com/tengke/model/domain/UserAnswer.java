package com.tengke.model.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户答案（评分结果）
 */
@Data
public class UserAnswer implements Serializable {
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

    private static final long serialVersionUID = 1L;
}
