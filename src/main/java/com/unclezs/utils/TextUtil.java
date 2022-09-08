package com.unclezs.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 文本工具
 *
 * @author uncle
 * @date 2020/2/27 11:20
 */
public class TextUtil {
    /**
     * 去除头尾
     *
     * @param header 头
     * @param tail   尾
     * @param src    源文本
     * @return 删减后的文本
     */
    public static String removeHeaderAndTail(String header, String tail, String src) {
        if (StrUtil.isNotEmpty(header) && src.contains(header)) {
            src = src.substring(header.length());
        }
        if (StrUtil.isNotEmpty(tail) && src.contains(tail)) {
            src = src.substring(0, src.indexOf(tail));
        }
        return src;
    }


    /**
     * 移除文本中的文本(去除空格)
     *
     * @param src    源文本
     * @param target 要移除的文本
     * @return /
     */
    public static String removeText(String src, String target) {
        return src.replace(TextUtil.remove(target, " "), "");
    }

    /**
     * 移除文本中的标题
     *
     * @param src    源文本
     * @param target 要移除的文本
     * @return /
     */
    public static String removeTitle(String src, String target) {
        if (!target.startsWith("第")) {
            return src;
        }
        String[] lines = src.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (StrUtil.isNotBlank(lines[i])) {
                if (i < 3) {
                    sb.append(lines[i].replace(TextUtil.remove(target, " "), "")).append("\r\n");
                } else {
                    sb.append(lines[i]).append("\r\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 清楚字符串里面的指定元素
     *
     * @param src    源字符串
     * @param target 目录字符串数组
     * @return 清除后的
     */
    public static String remove(String src, String... target) {
        if (StrUtil.isBlank(src)) {
            return src;
        }
        for (String s : target) {
            if (s != null) {
                src = src.replaceAll(s, "");
            }
        }
        return trim(src);
    }

    /**
     * 清楚字符串里面的指定元素
     *
     * @param src    源字符串
     * @param target 目录字符串数组
     * @return 清除后的
     */
    public static String removePlain(String src, String... target) {
        if (StrUtil.isBlank(src)) {
            return src;
        }
        for (String s : target) {
            if (s != null) {
                src = src.replace(s, "");
            }
        }
        return trim(src);
    }

    /**
     * 移除文件名称的非法字符
     *
     * @param str 文件名称
     * @return /
     */
    public static String removeInvalidSymbol(String str) {
        return ReUtil.replaceAll(str, "[\\\\/:*?\"<>|]", "");
    }

    /**
     * 获取添加范围后的html
     *
     * @param header 范围头
     * @param tail   范围尾部
     * @param src    源
     * @return 删减后的html
     */
    public static String getDelHtml(String header, String tail, String src) {
        int end = tail != null && tail.length() > 1 ? src.indexOf(tail) : src.length();
        int st = header != null && header.length() > 1 ? src.indexOf(header) : 0;
        if (st == -1) {
            st = 0;
        }
        if (end == -1) {
            end = src.length();
        }
        if (st != 0) {
            st -= 5;
        }
        if (end != src.length()) {
            end += 5;
        }
        return src.substring(st, end);
    }

    public static String trim(String text) {
        int len = text.length();
        int st = 0;
        char[] val = text.toCharArray();
        char p;
        while ((st < len) && ((p = val[st]) <= ' ' || p == 160 || p == 12288)) {
            st++;
        }
        while ((st < len) && ((p = val[len - 1]) <= ' ' || p == 160 || p == 12288)) {
            len--;
        }
        return ((st > 0) || (len < text.length())) ? text.substring(st, len) : text;
    }

}
