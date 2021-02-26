package com.uncles.novel.app.jfx.framework.util;

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
    /**
     * 国际化语言包
     */
    private static final WeakHashMap<String, ResourceBundle> BUNDLES = new WeakHashMap<>(16);
    private static final String BASE_DIR = "i18n.";

    /**
     * 获取国际化资源文件
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getResourceBundle(String bundleName) {
        ResourceBundle bundle = BUNDLES.get(bundleName);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
            BUNDLES.put(bundleName, bundle);
        }
        return bundle;
    }

    /**
     * 获取国际化资源文件，自动拼接前缀
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getBundle(String baseName) {
        baseName = BASE_DIR + baseName;
        return getResourceBundle(baseName);
    }

    /**
     * 拿到国际化后的字符串
     *
     * @param key        stringKey
     * @param bundleName 资源文件名
     * @return ResourceBundle
     */
    public static String getString(String bundleName, String key) {
        return getResourceBundle(bundleName).getString(key);
    }
}
