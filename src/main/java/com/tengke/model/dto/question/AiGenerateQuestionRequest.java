package com.tengke.model.dto.question;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * AI生成题目请求
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {
    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;

    /**
     * 题目数量
     */
    @NotNull(message = "题目数量不能为空")
    private Integer questionNumber;

    /**
     * 选项数量
     */
    @NotNull(message = "选项数量不能为空")
    private Integer optionNumber;

    /**
     * 应用名称（可选，前端可直接传入，后端会优先使用；为空时从数据库中读取）
     */
    private String appName;

    /**
     * 应用描述（可选，前端可直接传入，后端会优先使用；为空时从数据库中读取）
     */
    private String appDesc;

    private static final long serialVersionUID = 1L;
}
