package com.unclezs.enmu;

/**
 * @author uncle
 * @date 2020/4/17 16:32
 */
public enum TextNovelType {
    /**
     * 格式
     */
    MOBI,
    TXT,
    EPUB;

    public static boolean contain(String type) {
        for (TextNovelType value : values()) {
            if (value.toString().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
