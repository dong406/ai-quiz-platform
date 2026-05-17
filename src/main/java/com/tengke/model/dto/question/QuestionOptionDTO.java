package com.tengke.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目选项DTO
 */
@Data
public class QuestionOptionDTO implements Serializable {
    /**
     * 选项key（A/B/C/D）
     */
    private String key;

    /**
     * 选项值
     */
    private String value;

    private static final long serialVersionUID = 1L;
}
