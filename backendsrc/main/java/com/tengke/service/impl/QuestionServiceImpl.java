package com.tengke.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;
import com.tengke.mapper.QuestionMapper;
import com.tengke.model.domain.Question;
import com.tengke.model.dto.question.QuestionAddRequest;
import com.tengke.model.dto.question.QuestionContentDTO;
import com.tengke.service.QuestionService;
import com.tengke.utils.ThrowUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public Long addQuestion(QuestionAddRequest questionAddRequest) {
        ThrowUtils.throwIf(questionAddRequest == null || questionAddRequest.getAppId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(questionAddRequest.getQuestionContent() == null || questionAddRequest.getQuestionContent().isEmpty(), 
                ErrorCode.PARAMS_ERROR, "题目内容不能为空");
        // 保存题目（包含选项JSON序列化）
        Question question = new Question();
        question.setAppId(questionAddRequest.getAppId());
        List<QuestionContentDTO> questionContent = questionAddRequest.getQuestionContent();
        question.setQuestionContent(JSONUtil.toJsonStr(questionContent));
        boolean result = this.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return question.getId();
    }

    @Override
    public List<QuestionContentDTO> getQuestionListByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getAppId, appId);
        List<Question> questionList = this.list(queryWrapper);
        // 解析JSON内容
        return questionList.stream()
                .map(question -> {
                    String questionContent = question.getQuestionContent();
                    return JSONUtil.toList(questionContent, QuestionContentDTO.class);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
