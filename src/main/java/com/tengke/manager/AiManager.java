package com.tengke.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI管理器
 */
@Service
@Slf4j
public class AiManager {

    @Value("${ai.apiKey}")
    private String apiKey;

    /**
     * 智谱AI API地址
     */
    private static final String ZHIPU_AI_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    /**
     * 初始化AI客户端
     */
    @PostConstruct
    public void init() {
        log.info("AI Manager初始化完成，API Key: {}", apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "未配置");
    }

    /**
     * 同步稳定请求（temperature=0.05）
     */
    public String doSyncStableRequest(String prompt) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4");
            requestBody.put("temperature", 0.05);
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            // 发送HTTP请求
            String requestBodyJson = JSONUtil.toJsonStr(requestBody);
            log.debug("AI请求: {}", requestBodyJson);

            HttpResponse response = HttpRequest.post(ZHIPU_AI_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBodyJson)
                    .timeout(30000) // 30秒超时
                    .execute();

            // 解析响应
            String responseBody = response.body();
            log.debug("AI响应: {}", responseBody);

            if (response.getStatus() != 200) {
                log.error("AI调用失败，状态码: {}, 响应: {}", response.getStatus(), responseBody);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI调用失败，状态码: " + response.getStatus());
            }

            JSONObject responseObj = JSONUtil.parseObj(responseBody);
            
            // 检查错误
            if (responseObj.containsKey("error")) {
                JSONObject error = responseObj.getJSONObject("error");
                String errorMessage = error.getStr("message", "AI调用失败");
                log.error("AI调用返回错误: {}", errorMessage);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI调用失败: " + errorMessage);
            }

            // 解析choices
            JSONArray choices = responseObj.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                log.error("AI调用返回空，响应: {}", responseBody);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI调用返回空");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String content = message.getStr("content");

            if (content == null || content.trim().isEmpty()) {
                log.error("AI返回内容为空，响应: {}", responseBody);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI返回内容为空");
            }

            log.info("AI调用成功，返回内容长度: {}", content.length());
            return content.trim();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI调用失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI调用失败: " + e.getMessage());
        }
    }
}
