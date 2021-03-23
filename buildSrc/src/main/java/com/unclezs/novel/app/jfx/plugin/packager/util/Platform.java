package com.unclezs.novel.app.jfx.plugin.packager.util;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 18:46
 */
public enum Platform {
    /**
     * 自动识别
     */
    auto,
    /**
     * linux
     */
    linux,
    /**
     * mac
     */
    mac,
    /**
     * windows
     */
    windows;

    public boolean isCurrentPlatform() {
        if (this == auto) {
            return true;
        }
        return this == getCurrentPlatform();
    }

    public static Platform getCurrentPlatform() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return windows;
        }
        if (SystemUtils.IS_OS_LINUX) {
            return linux;
        }
        if (SystemUtils.IS_OS_MAC) {
            return mac;
        }
        return auto;
    }

}
