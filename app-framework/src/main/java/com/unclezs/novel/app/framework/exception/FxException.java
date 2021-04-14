package com.unclezs.novel.app.framework.exception;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:15
 */
public class FxException extends BaseRuntimeException {

  public FxException() {
  }

  public FxException(String message, Object... params) {
    super(message, params);
  }

  public FxException(Throwable cause) {
    super(cause);
  }
}
