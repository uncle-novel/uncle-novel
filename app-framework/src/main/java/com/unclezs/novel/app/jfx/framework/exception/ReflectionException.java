package com.unclezs.novel.app.jfx.framework.exception;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 15:52
 */
public class ReflectionException extends RuntimeException {

  public ReflectionException() {
  }

  public ReflectionException(String message) {
    super(message);
  }

  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionException(Throwable cause) {
    super(cause);
  }

  public ReflectionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
