package com.unclezs.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * URL工具类
 *
 * @author uncle
 * @date 2020/3/25 21:11
 */
public class UrlUtil {
    /**
     * 获取URL的域名
     *
     * @param url /
     * @return 域名
     */
    public static String getHost(String url) {
        return ReUtil.get("http[s]{0,1}://(.+?)/", url, 1);
    }

    /**
     * 获取host
     *
     * @param url /   https://www.unclezs.com  => unclezs
     * @return /
     */
    public static String getSite(String url) {
        String host = ReUtil.get("http[s]{0,1}://(.+?)/", url + "/", 1);
        String[] str = host.split("\\.");
        if (str.length == 3) {
            return str[1];
        } else {
            return str[0];
        }
    }

    /**
     * 取出URL中最后一段
     * https://unclezs.com/abc.html  则得到 abc
     *
     * @param url /
     * @return /
     */
    public static String getUrlLastPathNotSuffix(String url) {
        String str = url.replaceAll("\\.htm.*", "");
        int i = str.lastIndexOf("/");
        return str.substring(i + 1);
    }


    /**
     * 是否为http链接
     *
     * @param url /
     * @return /
     */
    public static boolean isHttpUrl(String url) {
        return StrUtil.isNotBlank(url) && url.toLowerCase().startsWith("http");
    }

    /**
     * 获取出现次数最多的基准URL
     *
     * @param urls url列表
     * @return url列表里面包含的次数最多的基准URL
     */
    public static String baseUrl(List<String> urls) {

        for (String url : urls) {

        }
        return "";
    }

    /**
     * url是否不包含锚点
     *
     * @param url /
     * @return /
     */
    public static boolean notAnchor(String url) {
        return isHttpUrl(url) && !url.contains("#");
    }
}
