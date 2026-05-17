package com.tengke.utils;

import com.tengke.common.ErrorCode;
import com.tengke.exception.BusinessException;

/**
 * 抛异常工具类
 */
public class ThrowUtils {
    /**
     * 条件成立则抛异常
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new BusinessException(errorCode);
        }
    }

    /**
     * 条件成立则抛异常
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        if (condition) {
            throw new BusinessException(errorCode, message);
        }
    }
}
