package com.unclezs.novel.app.framework.exception;

/**
 * 资源未找到
 *
 * @author blog.unclezs.com
 * @since 2021/4/11 20:43
 */
public class ResourceNotFoundException extends BaseRuntimeException {

  public ResourceNotFoundException(String message, Object... params) {
    super(message, params);
  }

  public ResourceNotFoundException(String resource) {
    this("资源未找到:{}", resource);
  }
}
