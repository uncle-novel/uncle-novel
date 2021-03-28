package com.unclezs.novel.app.jfx.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/03/27 0:24
 */
@UtilityClass
public class LoggerHelper {

  private static final Formatter FORMATTER = new LoggerFormatter("%s [%s] %-5s %s - %s");
  private static final Formatter HIGHLIGHT_FORMATTER = new LoggerFormatter(
    "\u001b[31m%s\u001b[0m \u001b[32m[%s]\u001b[0m \u001b[34m%-5s\u001b[0m \u001b[35m%s\u001b[0m \u001b[37m-\u001b[0m \u001b[36m%s\u001b[0m\n");
  private static FileHandler handler;
  private static ConsoleHandler consoleHandler;

  public static final String LAUNCHER_LOG_PATH = "./logs/launcher.log";
  public static final String LOGS_FILE_DIR = "logs";


  static {
    try {
      Path path = Paths.get(LOGS_FILE_DIR);
      if (Files.notExists(path)) {
        Files.createDirectory(path);
      }
      handler = new FileHandler(LAUNCHER_LOG_PATH);
      handler.setFormatter(FORMATTER);
      handler.setLevel(Level.WARNING);
      consoleHandler = new ConsoleHandler();
      consoleHandler.setFormatter(HIGHLIGHT_FORMATTER);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class LoggerFormatter extends Formatter {

    public String format;

    public LoggerFormatter(String format) {
      this.format = format;
    }

    @Override
    public String format(LogRecord record) {
      ZonedDateTime zdt = ZonedDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
      String date = zdt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      return String.format(this.format, date, Thread.currentThread().getName(), record.getLevel(), record.getLoggerName(), record.getMessage());
    }
  }

  public static Logger get(Class<?> clazz) {
    Logger logger = Logger.getLogger(clazz.getName());
    logger.setUseParentHandlers(false);
    logger.setParent(Logger.getGlobal());
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
    logger.addHandler(consoleHandler);
    return logger;
  }
}
