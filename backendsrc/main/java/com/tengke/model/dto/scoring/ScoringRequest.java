package com.tengke.model.dto.scoring;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 评分请求
 */
@Data
public class ScoringRequest implements Serializable {
    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;

    /**
     * 用户答题选项列表
     */
    @NotNull(message = "答题选项不能为空")
    private List<String> choices;

    private static final long serialVersionUID = 1L;
}
