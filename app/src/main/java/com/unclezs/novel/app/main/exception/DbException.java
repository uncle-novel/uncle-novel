package com.unclezs.novel.app.main.exception;

/**
 * DB相关异常
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 12:31
 */
public class DbException extends RuntimeException {

  public DbException() {
  }

  public DbException(String message) {
    super(message);
  }

  public DbException(String message, Throwable cause) {
    super(message, cause);
  }

  public DbException(Throwable cause) {
    super(cause);
  }

  public DbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
