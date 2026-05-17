package com.tengke.aop;

import com.tengke.annotation.AuthCheck;
import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;
import com.tengke.model.domain.User;
import com.tengke.utils.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 权限校验拦截器
 */
@Aspect
@Component
public class AuthInterceptor {

    @Before("@annotation(authCheck)")
    public void doBefore(JoinPoint joinPoint, AuthCheck authCheck) {
        String mustRole = authCheck.mustRole();
        User loginUser = UserContext.getLoginUser();
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            String userRole = loginUser.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }
}
