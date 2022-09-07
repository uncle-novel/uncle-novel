package com.unclezs.novel.app.framework.util;

/**
 * 操作系统工具
 *
 * @author blog.unclezs.com
 * @since 2021/04/03 17:30
 */
public class PlatformUtils {

  private static final String OS = System.getProperty("os.name");

  private static final boolean MAC = OS.startsWith("Mac");
  private static final boolean WINDOWS = OS.startsWith("Windows");
  private static final boolean LINUX = OS.startsWith("Linux");

  /**
   * 是否为windows
   *
   * @return true 是
   */
  public static boolean isWindows() {
    return WINDOWS;
  }

  /**
   * 是否为Mac
   *
   * @return true 是
   */
  public static boolean isMac() {
    return MAC;
  }

  /**
   * 是否为linux
   *
   * @return true 是
   */
  public static boolean isLinux() {
    return LINUX;
  }
}
