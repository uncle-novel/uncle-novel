package com.unclezs.novel.app.framework.support;

import java.util.ResourceBundle;

/**
 * 国际化支持
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public interface LocalizedSupport {

  String BASE_NAME = "com.unclezs.novel.app.localized.";
  String BASE_BUNDLE_NAME = "app";
  String COMMON_BUNDLE_NAME = "widgets.common";

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
   * 从app.properties中读取
   *
   * @param key key
   * @return 国际化后的
   */
  static String app(String key) {
    return LocalizedSupport.getBundle(LocalizedSupport.BASE_BUNDLE_NAME).getString(key);
  }

  /**
   * 读取国际化字符串
   *
   * @param key 字符串key
   * @return 国际化字符串
   */
  default String localized(String key) {
    ResourceBundle bundle = getBundle();
    if (bundle != null) {
      return bundle.getString(key);
    }
    if (getBundleName() == null) {
      throw new UnsupportedOperationException("未配置国际化资源包");
    }
    return getBundle(getBundleName()).getString(key);
  }

  /**
   * 获取 Bundle BaseName
   *
   * @return bundle BaseName
   */
  default String getBundleName() {
    return BASE_BUNDLE_NAME;
  }

  /**
   * 获取 Bundle
   *
   * @return bundle
   */
  default ResourceBundle getBundle() {
    return null;
  }
}
