package com.tengke.model.dto.scoring;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 用户答题记录添加请求
 */
@Data
public class UserAnswerRecordAddRequest implements Serializable {
    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;

    /**
     * 答题选项列表
     */
    @NotNull(message = "答题选项不能为空")
    private List<String> choices;

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
