package com.tengke.controller;

import com.tengke.common.BaseResponse;
import com.tengke.common.ErrorCode;
import com.tengke.common.ResultUtils;
import com.tengke.constant.UserConstant;
import com.tengke.exception.BusinessException;
import com.tengke.model.domain.User;
import com.tengke.model.dto.user.UserLoginRequest;
import com.tengke.model.dto.user.UserLoginResponse;
import com.tengke.model.dto.user.UserRegisterRequest;
import com.tengke.model.vo.UserVO;
import com.tengke.service.UserService;
import com.tengke.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 参数校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        // 业务逻辑
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<UserLoginResponse> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 参数校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        // 业务逻辑
        User user = userService.userLogin(userLoginRequest, request);
        UserVO userVO = userService.getSafetyUser(user);
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setUser(userVO);
        userLoginResponse.setToken(request.getSession().getId());
        return ResultUtils.success(userLoginResponse);
    }

    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = userService.getSafetyUser(user);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return ResultUtils.success(true);
    }
}
