package com.unclezs.novel.app.jfx.plugin.packager.util;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Object utils
 *
 * @author blog.unclezs.com
 * @date 2021/3/28 23:17
 */
public class ObjectUtils {

  /**
   * Returns the first non-null object
   *
   * @param <T>    Type
   * @param values List of objects
   * @return First non-null object from values list
   */
  @SuppressWarnings("unchecked")
  public static <T> T defaultIfNull(final T... values) {
    Optional<T> value = Arrays.stream(values).filter(Objects::nonNull).findFirst();
    return value.orElse(null);
  }
}
