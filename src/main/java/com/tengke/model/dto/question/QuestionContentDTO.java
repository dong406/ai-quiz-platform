package com.tengke.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 题目内容DTO
 */
@Data
public class QuestionContentDTO implements Serializable {
    /**
     * 题目
     */
    private String title;

    /**
     * 选项（JSON数组）
     */
    private List<QuestionOptionDTO> options;

    /**
     * 分值（打分类）
     */
    private Integer score;

    /**
     * 结果（测评类）
     */
    private String result;

    private static final long serialVersionUID = 1L;
}
