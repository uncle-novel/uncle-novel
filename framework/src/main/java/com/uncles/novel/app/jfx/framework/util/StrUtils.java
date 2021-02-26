package com.uncles.novel.app.jfx.framework.util;

/**
 * 字符串工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:58 下午
 */
public class StrUtils {
    /**
     * 字符串常量：空字符串 {@code StrUtils.EMPTY}
     */
    public static final String EMPTY = "";

    private StrUtils() {

    }

    /**
     * 是否为空字符串
     *
     * @param str /
     * @return /
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 非空字符串
     *
     * @param str /
     * @return /
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 是否为空白字符串
     *
     * @param str /
     * @return /
     */
    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!isBlankChar(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否为空白字符
     *
     * @param c 字符
     * @return /
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234;
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 获取添加范围后的html
     *
     * @param header 范围头
     * @param tail   范围尾部
     * @param src    源
     * @return 删减后的html
     */
    public static String getRange(String header, String tail, String src) {
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

    /**
     * 以target结果
     *
     * @param src    /
     * @param suffix /
     * @return /
     */
    public static boolean endWith(String src, String... suffix) {
        for (String end : suffix) {
            if (src.endsWith(end)) {
                return true;
            }
        }
        return false;
    }
}
