package com.unclezs.novel.app.jfx.framework.i18n;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public interface I18nSupport {
    /**
     * 读取国际化字符串
     *
     * @param key 字符串key
     * @return 国际化字符串
     */
    default String localized(String key) {
        return LanguageUtils.getString(getBundle(), key);
    }

    /**
     * 重写此方法获取设置bundle
     *
     * @return bundle
     */
    default String getBundle() {
        return LanguageUtils.FRAMEWORK;
    }
}
