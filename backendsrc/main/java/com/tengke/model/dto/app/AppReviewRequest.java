package com.tengke.model.dto.app;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 应用审核请求
 */
@Data
public class AppReviewRequest implements Serializable {
    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空")
    private Long id;

    /**
     * 审核状态（1-通过，2-拒绝）
     */
    @NotNull(message = "审核状态不能为空")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}
