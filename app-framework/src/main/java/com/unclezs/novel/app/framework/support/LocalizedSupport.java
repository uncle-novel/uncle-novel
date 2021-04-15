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
  String getBundleName();
}
