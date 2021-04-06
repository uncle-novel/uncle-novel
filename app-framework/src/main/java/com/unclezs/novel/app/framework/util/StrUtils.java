package com.unclezs.novel.app.framework.util;

/**
 * 字符串工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:58 下午
 */
public class StrUtils {

  /**
   * 字符常量：下划线 {@code '_'}
   */
  public static final char UNDERLINE = '_';
  /**
   * 字符串常量：空字符串 {@code StrUtils.EMPTY}
   */
  public static final String EMPTY = "";

  private StrUtils() {

  }


  /**
   * 驼峰转 下划线 userName  ---->  user_name user_name  ---->  user_name
   *
   * @param str 驼峰字符串
   * @return 带下滑线的String
   */
  public static String toUnderlineCase(String str) {
    if (str == null) {
      return null;
    }
    // 将驼峰字符串转换成数组
    char[] charArray = str.toCharArray();
    StringBuilder builder = new StringBuilder();
    //处理字符串
    for (int i = 0, l = charArray.length; i < l; i++) {
      if (charArray[i] >= 65 && charArray[i] <= 90) {
        if (i != 0) {
          builder.append("_");
        }
        builder.append(charArray[i] += 32);
      } else {
        builder.append(charArray[i]);
      }
    }
    return builder.toString();
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
