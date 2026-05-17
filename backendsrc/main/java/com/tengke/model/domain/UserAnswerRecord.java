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
 * 用户答题记录
 */
@TableName(value = "user_answer_record")
@Data
public class UserAnswerRecord implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 应用 id
     */
    @TableField("app_id")
    private Long appId;

    /**
     * 答题选项（JSON数组）
     */
    private String choices;

    /**
     * 总分（打分类）
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 结果名称（测评类）
     */
    @TableField("result_name")
    private String resultName;

    /**
     * 结果描述（测评类）
     */
    @TableField("result_desc")
    private String resultDesc;

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
