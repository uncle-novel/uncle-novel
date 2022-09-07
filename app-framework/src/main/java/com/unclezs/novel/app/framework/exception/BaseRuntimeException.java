package com.unclezs.novel.app.framework.exception;

import lombok.NoArgsConstructor;
import org.slf4j.helpers.MessageFormatter;

/**
 * 带格式化的异常
 *
 * @author blog.unclezs.com
 * @since 2021/4/11 20:45
 */
@NoArgsConstructor
public class BaseRuntimeException extends RuntimeException {

  public BaseRuntimeException(String message, Object... params) {
    super(MessageFormatter.arrayFormat(message, params).getMessage(), MessageFormatter.getThrowableCandidate(params));
  }

  public BaseRuntimeException(Throwable cause) {
    super(cause);
  }
}
