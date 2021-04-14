package com.unclezs.novel.app.framework.i18n;

import java.util.ResourceBundle;

/**
 * 国际化支持
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public interface LocalizedSupport {

  String BASE_NAME = "com.unclezs.novel.app.localized.";

  /**
   * 获取国际化资源文件
   *
   * @param bundleName 资源包名称
   * @return ResourceBundle
   */
  static ResourceBundle getBundle(String bundleName) {
    return ResourceBundle.getBundle(BASE_NAME.concat(bundleName));
  }

  /**
   * 读取国际化字符串
   *
   * @param key 字符串key
   * @return 国际化字符串
   */
  default String localized(String key) {
    return getBundle().getString(key);
  }

  /**
   * 获取 Bundle
   *
   * @return bundle
   */
  default ResourceBundle getBundle() {
    throw new UnsupportedOperationException("需要重写getBundle方法才能调用localized");
  }
}
