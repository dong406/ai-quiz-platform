package com.tengke.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tengke.model.domain.UserAnswerRecord;
import com.tengke.model.dto.scoring.UserAnswerRecordAddRequest;
import com.tengke.model.vo.UserAnswerRecordVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户答题记录服务
 */
public interface UserAnswerRecordService extends IService<UserAnswerRecord> {
    /**
     * 保存答题记录
     */
    Long addUserAnswerRecord(UserAnswerRecordAddRequest userAnswerRecordAddRequest, HttpServletRequest request);

    /**
     * 分页查询答题记录
     */
    Page<UserAnswerRecordVO> listUserAnswerRecord(Long userId, Long appId, Long current, Long pageSize);

    /**
     * 根据ID查询答题记录
     */
    UserAnswerRecordVO getUserAnswerRecordById(Long id);
}
