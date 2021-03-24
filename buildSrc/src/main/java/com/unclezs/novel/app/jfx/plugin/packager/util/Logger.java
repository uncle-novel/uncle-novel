package com.unclezs.novel.app.jfx.plugin.packager.util;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import org.apache.commons.lang3.StringUtils;

/**
 * Logging class
 */
public class Logger {

  private static final String TAB = "    "; // uses four blank spaces as tab

  private static int tabs = 0;

  public static String error(String error) {
    Context.getGradleContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error);
    return error;
  }

  public static String error(String error, Throwable t) {
    if (Context.isGradle()) {
      Context.getGradleContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error, t);
    }
    return error;
  }

  public static String warn(String warn) {
    if (Context.isGradle()) {
      Context.getGradleContext().getLogger().warn(StringUtils.repeat(TAB, tabs) + warn);
    }
    return warn;
  }

  public static String info(String info) {
    if (Context.isGradle()) {
      Context.getGradleContext().getLogger().quiet(StringUtils.repeat(TAB, tabs) + info);
    }
    return info;
  }

  public static void infoIndent(String msg) {
    info(msg);
    tabs++;
  }

  public static void infoUnindent(String msg) {
    tabs--;
    info(msg);
    info("");
  }

  public static void warnUnindent(String msg) {
    tabs--;
    warn(msg);
    info("");
  }

  public static void errorUnindent(String msg) {
    tabs--;
    error(msg);
    info("");
  }

  public static void errorUnindent(String msg, Throwable t) {
    tabs--;
    error(msg, t);
    info("");
  }

}
