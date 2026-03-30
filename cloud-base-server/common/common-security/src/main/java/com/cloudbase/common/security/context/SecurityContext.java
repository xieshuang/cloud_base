package com.cloudbase.common.security.context;

import com.cloudbase.common.security.entity.LoginUser;

public class SecurityContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(LoginUser user) {
        USER_HOLDER.set(user);
    }

    public static LoginUser getUser() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        LoginUser user = getUser();
        return user != null ? user.getUsername() : null;
    }

    public static void removeUser() {
        USER_HOLDER.remove();
    }
}
