package com.tengke.model.dto.user;

import com.tengke.model.vo.UserVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录响应
 */
@Data
public class UserLoginResponse implements Serializable {
    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 登录token（Session ID）
     */
    private String token;

    private static final long serialVersionUID = 1L;
}
