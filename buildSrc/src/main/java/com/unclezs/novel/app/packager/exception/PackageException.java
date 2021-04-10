package com.unclezs.novel.app.packager.exception;

/**
 * 打包异常
 *
 * @author blog.unclezs.com
 * @date 2021/4/10 9:51 上午
 */
public class PackageException extends RuntimeException {
  public PackageException() {
  }

  public PackageException(String message) {
    super(message);
  }

  public PackageException(String message, Throwable cause) {
    super(message, cause);
  }

  public PackageException(Throwable cause) {
    super(cause);
  }

  public PackageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
