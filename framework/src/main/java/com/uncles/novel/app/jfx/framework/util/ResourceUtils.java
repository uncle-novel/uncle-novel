package com.uncles.novel.app.jfx.framework.util;


import java.net.URL;

/**
 * 资源加载工具
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:47
 */
public class ResourceUtils {
    private ResourceUtils() {

    }

    /**
     * 加载classpath资源
     *
     * @param location 路径
     * @return URL
     */
    public static URL load(String location) {
        return ResourceUtils.class.getResource(location);
    }

    /**
     * 加载classpath css资源
     *
     * @param location css路径
     * @return css URL
     */
    public static String loadCss(String location) {
        return ResourceUtils.class.getResource(location).toExternalForm();
    }
}
