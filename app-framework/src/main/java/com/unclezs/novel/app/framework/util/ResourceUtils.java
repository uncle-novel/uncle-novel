package com.unclezs.novel.app.framework.util;


import java.net.URL;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源加载工具
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:47
 */
@Slf4j
public class ResourceUtils {

  private ResourceUtils() {

  }

  /**
   * 加载classpath资源
   *
   * @param location 路径
   * @return URL
   */
  public static URL load(String location) {
    URL resource = ResourceUtils.class.getResource(location);
    if (resource == null) {
      log.error("资源未找到: {}", location);
      throw new RuntimeException("资源未找到: ".concat(location));
    }
    return resource;
  }

  /**
   * 加载classpath css资源
   *
   * @param location css路径
   * @return css URL
   */
  public static String loadCss(String location) {
    return load(location).toExternalForm();
  }
}
