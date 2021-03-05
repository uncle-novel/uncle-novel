package com.uncles.novel.app.jfx.framework.util;

import lombok.experimental.UtilityClass;

/**
 * 系统属性工具类
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 10:22
 */
@UtilityClass
public class SystemUtils {
    public static final String SYSTEM_PROXIES = "java.net.useSystemProxies";

    /**
     * 启用系统代理
     */
    public static void enabledSystemProxy() {
        System.setProperty(SYSTEM_PROXIES, "true");
    }

    /**
     * 打开关闭系统代理
     */
    public static void switchSystemProxy(boolean enable) {
        System.setProperty(SYSTEM_PROXIES, String.valueOf(enable));
    }
}
