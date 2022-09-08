package com.unclezs.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * @author unclezs.com
 * @date 2019.07.08 19:46
 */
public class URLEncoder {
    private URLEncoder() {
    }

    //将url里的中文转Unicode
    public static String encode(String url, String charset) throws UnsupportedEncodingException {

        StringBuffer toUrl = new StringBuffer();
        for (char c : url.toCharArray()) {
            if (!isChinese(c)) {
                toUrl.append(c);
            } else {
                toUrl.append(java.net.URLEncoder.encode(c + "", charset));
            }
        }
        return toUrl.toString();
    }

    //判断是否为中文
    public static boolean isChinese(char c) {
        String reg = "[\\u4E00-\\u9FFF]";//中文
        return Pattern.matches(reg, c + "");
    }

    /**
     * &#x编码转换成汉字
     *
     * @param src 字符集
     * @return 解码后的字符集
     */
    public static String deCodeUnicode(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos;
        char ch;
        src = src.replace("&#x", "%u").replace(";", "");
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {

                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}
