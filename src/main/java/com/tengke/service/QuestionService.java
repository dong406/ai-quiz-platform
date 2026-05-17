package com.tengke.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tengke.model.domain.Question;
import com.tengke.model.dto.question.QuestionAddRequest;
import com.tengke.model.dto.question.QuestionContentDTO;

import java.util.List;

/**
 * 题目服务
 */
public interface QuestionService extends IService<Question> {
    /**
     * 添加题目
     */
    Long addQuestion(QuestionAddRequest questionAddRequest);

    /**
     * 根据应用ID查询题目列表
     */
    List<QuestionContentDTO> getQuestionListByAppId(Long appId);
}
