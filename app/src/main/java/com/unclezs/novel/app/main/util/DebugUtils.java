package com.unclezs.novel.app.main.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 日志调试工具
 *
 * @author blog.unclezs.com
 * @date 2021/03/19 8:01
 */
@UtilityClass
public class DebugUtils {

  public static final String DEBUG_LOGGER_NAME = "com.unclezs";
  public static final Logger DEBUG_LOGGER;
  public static final LoggerContext CONTEXT;

  static {
    CONTEXT = (LoggerContext) LoggerFactory.getILoggerFactory();
    DEBUG_LOGGER = CONTEXT.getLogger(DEBUG_LOGGER_NAME);
  }

  /**
   * 开启全部级别的日志记录
   */
  public static void debug() {
    DEBUG_LOGGER.setLevel(Level.TRACE);
  }

  /**
   * 开启INFO级别以上的日志记录，关闭FX日志
   */
  public static void info() {
    DEBUG_LOGGER.setLevel(Level.INFO);
  }

  /**
   * 调式模式开关
   *
   * @param debug true开启调式模式
   */
  public static void debug(boolean debug) {
    if (debug) {
      debug();
    } else {
      info();
    }
  }

  /**
   * 停止日志记录
   */
  public static void stop() {
    CONTEXT.stop();
  }

  /**
   * 启动日志记录
   */
  public static void start() {
    CONTEXT.start();
  }

  /**
   * 自定义日志记录级别
   *
   * @param level 日志级别
   */
  public static void level(Level level) {
    DEBUG_LOGGER.setLevel(level);
  }

  /**
   * 重新加载配置，清除默认配置
   *
   * @param configInClasspath 在classpath下的配置,如果为空则清空当前配置
   * @return 配置上下文
   */
  public static LoggerContext reconfigure(String configInClasspath) {
    //这里不能直接new，因为还做了一些初始化的工作
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      context.reset();
      if (configInClasspath != null && !configInClasspath.isEmpty()) {
        configurator.doConfigure(Objects.requireNonNull(DebugUtils.class.getResource(configInClasspath)));
      }
    } catch (JoranException ignored) {
      StatusPrinter.print(context);
    }
    return context;
  }

  /**
   * 输出日志方便测试
   *
   * @param loggerName 日志记录器名字
   */
  public static void logMsg(String loggerName) {
    Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
    logger.trace("test message by different level");
    logger.debug("test message by different level");
    logger.info("test message by different level");
    logger.warn("test message by different level");
    logger.error("test message by different level");
  }
}
