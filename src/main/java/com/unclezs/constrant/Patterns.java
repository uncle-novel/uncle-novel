package com.unclezs.constrant;

import java.util.regex.Pattern;

/**
 * 正则常量类
 * @author uncle
 * @date 2019.10.23
 */
public final class Patterns {
    public static final Pattern LRTS_REG= Pattern.compile("章节：</span>(.+?)</li>");
    public static final Pattern TING56_REG= Pattern.compile("FonHen_JieMa[(]'([\\s\\S]+?)'");
    public static final Pattern YSTS8_REG= Pattern.compile("<iframe src=\"(.+?)\"");
    public static final Pattern YSTS8_REG1= Pattern.compile("[?']([0-9a-zA-Z]+?-[0-9a-zA-Z]+?)[?']");
    public static final Pattern TS8_REG= Pattern.compile("\"(/playdata/.+?js.*?)\"");
}
