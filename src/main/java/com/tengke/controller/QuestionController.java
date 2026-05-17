package com.tengke.controller;

import cn.hutool.json.JSONUtil;
import com.tengke.common.BaseResponse;
import com.tengke.common.ErrorCode;
import com.tengke.common.ResultUtils;
import com.tengke.constant.AiConstant;
import com.tengke.exception.BusinessException;
import com.tengke.manager.AiManager;
import com.tengke.model.domain.App;
import com.tengke.model.dto.question.AiGenerateQuestionRequest;
import com.tengke.model.dto.question.QuestionAddRequest;
import com.tengke.model.dto.question.QuestionContentDTO;
import com.tengke.service.AppService;
import com.tengke.service.QuestionService;
import com.tengke.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    /**
     * 健康检查接口，用于测试服务是否正常运行
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        log.info("健康检查接口被调用");
        return ResultUtils.success("服务正常运行");
    }

    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest) {
        log.info("=== 收到保存题目请求 ===");
        log.info("请求参数: appId={}, questionContent数量={}", 
                questionAddRequest != null ? questionAddRequest.getAppId() : null,
                questionAddRequest != null && questionAddRequest.getQuestionContent() != null 
                    ? questionAddRequest.getQuestionContent().size() : 0);
        
        try {
            // 参数校验
            ThrowUtils.throwIf(questionAddRequest == null || questionAddRequest.getAppId() == null, ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(questionAddRequest.getQuestionContent() == null || questionAddRequest.getQuestionContent().isEmpty(), 
                    ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            
            log.info("参数校验通过，开始保存题目...");
            // 保存题目（包含选项JSON序列化）
            Long questionId = questionService.addQuestion(questionAddRequest);
            log.info("题目保存成功，questionId={}", questionId);
            return ResultUtils.success(questionId);
        } catch (BusinessException e) {
            log.error("保存题目业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("保存题目系统异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存题目失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public BaseResponse<List<QuestionContentDTO>> listQuestion(@RequestParam Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        List<QuestionContentDTO> questionList = questionService.getQuestionListByAppId(appId);
        return ResultUtils.success(questionList);
    }

    @PostMapping("/ai_generate")
    public BaseResponse<List<QuestionContentDTO>> aiGenerateQuestion(@RequestBody AiGenerateQuestionRequest request) {
        try {
            // 参数校验
            ThrowUtils.throwIf(request.getAppId() == null || request.getAppId() <= 0, ErrorCode.PARAMS_ERROR);
            // 查询应用信息
            App app = appService.getById(request.getAppId());
            ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
            // 应用基础信息（优先使用前端传入，其次使用数据库值）
            String appName = request.getAppName();
            String appDesc = request.getAppDesc();
            if (appName == null || appName.trim().isEmpty()) {
                appName = app.getAppName();
            }
            if (appDesc == null || appDesc.trim().isEmpty()) {
                appDesc = app.getAppDesc();
            }
            // 构建Prompt
            String appTypeDesc = app.getAppType() == 0 ? "打分类" : "测评类";
            String prompt = String.format(AiConstant.GENERATE_QUESTION_SYSTEM_MESSAGE,
                    appName,
                    appDesc,
                    appTypeDesc,
                    request.getQuestionNumber(),
                    request.getOptionNumber());
            // 调用AI
            String aiResult = aiManager.doSyncStableRequest(prompt);
            // 打印AI原始返回内容（不做任何处理，便于排查）
            log.info("AI原始返回内容: {}", aiResult);
            
            // 清理AI返回的内容，移除可能的markdown代码块标记
            String cleanedResult = aiResult.trim();
            // 移除 ```json 和 ``` 标记
            if (cleanedResult.startsWith("```json")) {
                cleanedResult = cleanedResult.substring(7);
            } else if (cleanedResult.startsWith("```")) {
                cleanedResult = cleanedResult.substring(3);
            }
            if (cleanedResult.endsWith("```")) {
                cleanedResult = cleanedResult.substring(0, cleanedResult.length() - 3);
            }
            cleanedResult = cleanedResult.trim();
            
            // 解析JSON
            List<QuestionContentDTO> questionContentList;
            try {
                // 若不是以 '[' 开头，说明可能是单个对象或多个对象片段，尝试包装成数组
                String jsonToParse = cleanedResult;
                if (!jsonToParse.startsWith("[")) {
                    // 尝试提取第一个 '{' 之后到最后一个 '}' 之间的内容
                    int firstBrace = jsonToParse.indexOf('{');
                    int lastBrace = jsonToParse.lastIndexOf('}');
                    if (firstBrace >= 0 && lastBrace > firstBrace) {
                        jsonToParse = jsonToParse.substring(firstBrace, lastBrace + 1);
                    }
                    // 包装为数组，兼容“单个对象”或“多个对象以逗号分隔”的情况
                    jsonToParse = "[" + jsonToParse + "]";
                }

                log.info("准备解析的JSON内容: {}", jsonToParse);

                questionContentList = JSONUtil.toList(jsonToParse, QuestionContentDTO.class);
            } catch (Exception e) {
                // JSON解析失败，记录原始内容
                log.error("AI返回的原始内容: {}", aiResult);
                log.error("清理后的内容: {}", cleanedResult);
                log.error("JSON解析异常: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI返回的JSON格式不正确: " + e.getMessage());
            }
            
            if (questionContentList == null || questionContentList.isEmpty()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI未生成任何题目");
            }
            
            return ResultUtils.success(questionContentList);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成题目失败: " + e.getMessage());
        }
    }
}
