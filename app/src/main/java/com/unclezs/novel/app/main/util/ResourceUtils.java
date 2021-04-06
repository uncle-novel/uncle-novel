package com.unclezs.novel.app.main.util;


import com.unclezs.novel.app.framework.util.ReflectionUtils;
import java.net.URL;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源加载工具
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:47
 */
@Slf4j
@UtilityClass
public class ResourceUtils {

  /**
   * 加载classpath资源
   *
   * @param location 路径
   * @return URL
   */
  public static URL load(String location) {
    StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
    URL resource = ReflectionUtils.forName(stackTraceElement.getClassName()).getResource(location);
    if (resource == null) {
      log.error("资源未找到：{}", location);
      throw new RuntimeException("resource not found!");
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
