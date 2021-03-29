package com.unclezs.jfx.launcher;

/**
 * 操作系统枚举
 *
 * @author blog.unclezs.com
 * @since 2021/03/23 13:48
 */
public enum Os {
  /**
   * Mac
   */
  MAC,
  /**
   * Linux
   */
  LINUX,
  /**
   * Windows
   */
  WIN,
  /**
   * 通用
   */
  COMMON;
  /**
   * 当前操作系统
   */
  public static final Os CURRENT;

  public static final String MAC_STR = "mac";
  public static final String DARWIN = "darwin";
  public static final String NUX = "nux";
  public static final String WIN_STR = "win";

  static {
    String os = System.getProperty("os.name", "generic").toLowerCase();

    if ((os.contains(MAC_STR)) || (os.contains(DARWIN))) {
      CURRENT = MAC;
    } else if (os.contains(NUX)) {
      CURRENT = LINUX;
    } else if (os.contains(WIN_STR)) {
      CURRENT = WIN;
    } else {
      CURRENT = COMMON;
    }
  }
}
