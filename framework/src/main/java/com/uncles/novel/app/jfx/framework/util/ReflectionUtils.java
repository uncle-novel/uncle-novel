package com.uncles.novel.app.jfx.framework.util;

import com.uncles.novel.app.jfx.framework.exception.ReflectionException;

/**
 * 反射工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 15:50
 */
public class ReflectionUtils {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e);
        }
    }
}
