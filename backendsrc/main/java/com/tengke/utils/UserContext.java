package com.tengke.utils;

import com.tengke.model.domain.User;

/**
 * 用户上下文
 */
public class UserContext {
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    public static User getLoginUser() {
        return USER_HOLDER.get();
    }

    public static void setLoginUser(User user) {
        USER_HOLDER.set(user);
    }

    public static void removeLoginUser() {
        USER_HOLDER.remove();
    }
}
