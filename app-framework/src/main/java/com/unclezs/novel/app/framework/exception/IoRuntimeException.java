package com.unclezs.novel.app.framework.exception;

/**
 * @author blog.unclezs.com
 * @date 2021/4/13 0:38
 */
public class IoRuntimeException extends BaseRuntimeException {

  public IoRuntimeException() {
  }

  public IoRuntimeException(String message, Object... params) {
    super(message, params);
  }

  public IoRuntimeException(Throwable cause) {
    super(cause);
  }
}
