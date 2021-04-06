package com.unclezs.novel.app.framework.i18n;

import com.unclezs.novel.app.framework.i18n.bundle.XmlResourceBundle;
import com.unclezs.novel.app.framework.i18n.control.XmlControl;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

/**
 * I18n 多语言工具
 * <p>
 * i18n = internationalization ,因为18个字,所以叫i18n
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 10:55
 */
public class LanguageUtils {

  public static final String FRAMEWORK = "i18n.framework.framework";
  public static final ResourceBundle FRAMEWORK_BUNDLE;
  public static final String CHINESE = "zh";
  /**
   * 国际化语言包
   */
  private static final WeakHashMap<String, ResourceBundle> BUNDLES = new WeakHashMap<>(16);

  static {
    FRAMEWORK_BUNDLE = getResourceBundle(FRAMEWORK);
  }

  /**
   * 获取国际化资源文件并缓存
   *
   * @return ResourceBundle
   */
  public static ResourceBundle getBundle(String bundleName) {
    ResourceBundle bundle = BUNDLES.get(bundleName);
    if (bundle == null) {
      // 默认自动设置父bundle为FRAMEWORK
      if (!FRAMEWORK.equals(bundleName)) {
        XmlResourceBundle resourceBundle = getResourceBundle(bundleName);
        resourceBundle.setParent(FRAMEWORK_BUNDLE);
        bundle = resourceBundle;
        BUNDLES.put(bundleName, bundle);
      } else {
        return FRAMEWORK_BUNDLE;
      }
    }
    return bundle;
  }

  /**
   * 获取resource bundle
   *
   * @param bundleName 名字
   * @return bundle
   */
  public static XmlResourceBundle getResourceBundle(String bundleName) {
    return (XmlResourceBundle) ResourceBundle.getBundle(bundleName, Locale.getDefault(), XmlControl.ME);
  }

  /**
   * 拿到国际化后的字符串
   *
   * @param key        stringKey
   * @param bundleName 资源文件名
   * @return ResourceBundle
   */
  public static String getString(String bundleName, String key) {
    if (Locale.getDefault().equals(Locale.CHINESE)) {
      return key;
    }
    return getBundle(bundleName).getString(key);
  }

  /**
   * 判断当前是否为中文简体 Locale
   *
   * @return true 中文简体
   */
  public static boolean isChineseSimple() {
    return CHINESE.equals(Locale.getDefault().getLanguage()) && !Locale.TAIWAN
      .equals(Locale.getDefault());
  }
}
