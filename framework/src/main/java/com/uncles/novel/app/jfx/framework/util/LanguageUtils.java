package com.uncles.novel.app.jfx.framework.util;

import com.uncles.novel.app.jfx.framework.i18n.control.JsonControl;
import com.uncles.novel.app.jfx.framework.i18n.control.XmlControl;

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
    public static final String FRAMEWORK = "i18n.framework";
    /**
     * 国际化语言包
     */
    private static final WeakHashMap<String, ResourceBundle> BUNDLES = new WeakHashMap<>(16);
    /**
     * 获取国际化资源文件
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getBundle(String bundleName) {
        ResourceBundle bundle = BUNDLES.get(bundleName);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), XmlControl.ME);
            BUNDLES.put(bundleName, bundle);
        }
        return bundle;
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

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        System.out.println(ResourceBundle.getBundle("i18n.framework", JsonControl.ME).getString("Uncle小说"));
    }
}
