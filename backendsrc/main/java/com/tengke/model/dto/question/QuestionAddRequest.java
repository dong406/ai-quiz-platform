package com.tengke.model.dto.question;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 题目添加请求
 */
@Data
public class QuestionAddRequest implements Serializable {
    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;

    /**
     * 题目内容列表
     */
    private List<QuestionContentDTO> questionContent;

    private static final long serialVersionUID = 1L;
}
