package com.tengke.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tengke.model.domain.User;
import com.tengke.model.dto.user.UserLoginRequest;
import com.tengke.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     */
    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户脱敏
     */
    UserVO getSafetyUser(User originUser);
}
