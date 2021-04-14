package com.unclezs.novel.app.framework.util;

import cn.hutool.core.util.ReflectUtil;
import com.unclezs.novel.app.framework.exception.ReflectionException;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 反射工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 15:50
 */
public class ReflectUtils {

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

  /**
   * 设置字段的值
   *
   * @param clazz     类型
   * @param fieldName 字段名
   * @param bean      实例
   * @param value     值
   */
  public static void setFieldValue(Class<?> clazz, String fieldName, Object bean, Object value) {
    Field field = ReflectUtil.getField(clazz, fieldName);
    if (field != null) {
      field.setAccessible(true);
      ReflectUtil.setFieldValue(bean, fieldName, value);
    }
  }
}
