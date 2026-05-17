package com.tengke.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;
import com.tengke.mapper.UserAnswerRecordMapper;
import com.tengke.model.domain.App;
import com.tengke.model.domain.User;
import com.tengke.model.domain.UserAnswerRecord;
import com.tengke.model.dto.scoring.UserAnswerRecordAddRequest;
import com.tengke.model.vo.UserAnswerRecordVO;
import com.tengke.service.AppService;
import com.tengke.service.UserAnswerRecordService;
import com.tengke.service.UserService;
import com.tengke.utils.ThrowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户答题记录服务实现类
 */
@Service
public class UserAnswerRecordServiceImpl extends ServiceImpl<UserAnswerRecordMapper, UserAnswerRecord> implements UserAnswerRecordService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Override
    public Long addUserAnswerRecord(UserAnswerRecordAddRequest userAnswerRecordAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userAnswerRecordAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 创建答题记录
        UserAnswerRecord userAnswerRecord = new UserAnswerRecord();
        userAnswerRecord.setUserId(loginUser.getId());
        userAnswerRecord.setAppId(userAnswerRecordAddRequest.getAppId());
        userAnswerRecord.setChoices(JSONUtil.toJsonStr(userAnswerRecordAddRequest.getChoices()));
        userAnswerRecord.setTotalScore(userAnswerRecordAddRequest.getTotalScore());
        userAnswerRecord.setResultName(userAnswerRecordAddRequest.getResultName());
        userAnswerRecord.setResultDesc(userAnswerRecordAddRequest.getResultDesc());
        boolean result = this.save(userAnswerRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return userAnswerRecord.getId();
    }

    @Override
    public Page<UserAnswerRecordVO> listUserAnswerRecord(Long userId, Long appId, Long current, Long pageSize) {
        LambdaQueryWrapper<UserAnswerRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, UserAnswerRecord::getUserId, userId);
        queryWrapper.eq(appId != null, UserAnswerRecord::getAppId, appId);
        queryWrapper.orderByDesc(UserAnswerRecord::getCreateTime);
        Page<UserAnswerRecord> recordPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserAnswerRecordVO> recordVOPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<UserAnswerRecordVO> recordVOList = recordPage.getRecords().stream()
                .map(record -> {
                    UserAnswerRecordVO vo = new UserAnswerRecordVO();
                    BeanUtils.copyProperties(record, vo);
                    // 查询应用名称
                    if (record.getAppId() != null) {
                        App app = appService.getById(record.getAppId());
                        if (app != null) {
                            vo.setAppName(app.getAppName());
                        }
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        recordVOPage.setRecords(recordVOList);
        return recordVOPage;
    }

    @Override
    public UserAnswerRecordVO getUserAnswerRecordById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        UserAnswerRecord record = this.getById(id);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR);
        UserAnswerRecordVO vo = new UserAnswerRecordVO();
        BeanUtils.copyProperties(record, vo);
        // 查询应用名称
        if (record.getAppId() != null) {
            App app = appService.getById(record.getAppId());
            if (app != null) {
                vo.setAppName(app.getAppName());
            }
        }
        return vo;
    }
}
