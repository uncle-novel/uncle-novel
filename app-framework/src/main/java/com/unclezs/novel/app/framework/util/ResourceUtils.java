package com.unclezs.novel.app.framework.util;


import com.unclezs.novel.app.framework.exception.IoRuntimeException;
import com.unclezs.novel.app.framework.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
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
  public static URL url(String location) {
    URL resource = Thread.currentThread().getContextClassLoader().getResource(location);
    if (resource == null) {
      log.error("资源未找到: {}", location);
      throw new ResourceNotFoundException("资源未找到: ".concat(location));
    }
    return resource;
  }

  /**
   * 加载classpath资源
   *
   * @param location 路径
   * @return true 存在
   */
  public static boolean exist(String location) {
    return null != Thread.currentThread().getContextClassLoader().getResource(location);
  }

  /**
   * 加载classpath资源
   *
   * @param location 路径
   * @return stream
   */
  public static InputStream stream(String location) {
    InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
    if (stream == null) {
      log.error("资源未找到: {}", location);
      throw new ResourceNotFoundException("资源未找到: ".concat(location));
    }
    return stream;
  }

  /**
   * 加载classpath的properties
   *
   * @param location 路径
   * @return properties
   */
  public static Properties properties(String location) {
    InputStream stream = stream(location);
    Properties properties = new Properties();
    try {
      properties.load(stream);
    } catch (IOException e) {
      throw new IoRuntimeException(e);
    }
    return properties;
  }

  /**
   * 加载classpath资源
   *
   * @param location css路径
   * @return css url字符串
   */
  public static String externalForm(String location) {
    return url(location).toExternalForm();
  }
}
