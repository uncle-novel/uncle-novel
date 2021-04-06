package com.unclezs.novel.app.framework.util;

import com.unclezs.novel.app.framework.exception.ReflectionException;
import java.util.Optional;

/**
 * 反射工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 15:50
 */
public class ReflectionUtils {

  @SuppressWarnings("unchecked")
  public static <T> Class<T> forName(String className) {
    try {
      return (Class<T>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ReflectionException(e);
    }
  }

  /**
   * 获取调用的 Class
   *
   * @return 调用的class
   */
  public static Class<?> getCallerClass() {
    Optional<? extends Class<?>> walk = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
      .walk(s -> s.skip(2).findFirst().map(StackWalker.StackFrame::getDeclaringClass));
    return walk.orElse(null);
  }
}
