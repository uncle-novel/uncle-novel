package com.unclezs.novel.app.jfx.packager.util;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.packager.Context;
import org.gradle.api.logging.LogLevel;

/**
 * 日志类
 *
 * @author blog.unclezs.com
 * @date 2021/4/2 9:30
 */
public class Logger {

  private static final String TAB = "    ";
  private static int tabs = 0;

  /**
   * 错误日志
   *
   * @param msg  消息
   * @param args 格式化参数
   */
  public static void error(String msg, Object... args) {
    log(LogLevel.ERROR, msg, args);
  }

  /**
   * 错误日志
   *
   * @param msg       消息
   * @param throwable 异常
   */
  public static void error(String msg, Throwable throwable) {
    log(LogLevel.ERROR, msg);
  }

  /**
   * 警告日志
   *
   * @param msg  消息
   * @param args 参数
   */
  public static void warn(String msg, Object... args) {
    log(LogLevel.WARN, yellow(msg), args);
  }

  /**
   * 警告日志
   *
   * @param msg  消息
   * @param args 参数
   */
  public static void info(String msg, Object... args) {
    log(LogLevel.QUIET, msg, args);
  }

  /**
   * 警告日志
   *
   * @param msg  消息
   * @param args 参数
   */
  public static void log(LogLevel level, String msg, Object... args) {
    Context.getLogger().log(level, StrUtil.repeat(TAB, tabs).concat(msg), args);
  }

  /**
   * 带缩进的日志
   *
   * @param msg  消息
   * @param args 参数
   */
  public static void infoIndent(String msg, Object... args) {
    info(msg, args);
    tabs++;
  }

  public static void infoUnIndent(String msg, Object... args) {
    tabs--;
    info(msg, args);
    info("");
  }

  public static void warnUnIndent(String msg, Object... args) {
    tabs--;
    warn(msg, args);
    info("");
  }

  public static void errorUnIndent(String msg, Object... args) {
    tabs--;
    error(msg, args);
    info("");
  }


  /**
   * 控制台 ANSI 黄色高亮
   *
   * @param msg 消息
   * @return 黄色高亮字符串
   */
  private static String yellow(String msg) {
    return String.format("\u001b[33m%s\u001b[0m", msg);
  }
}
