package com.uncles.novel.app.jfx.framework.i18n;

import com.uncles.novel.app.jfx.framework.util.LanguageUtils;

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
    default String str(String key) {
        return LanguageUtils.getString(key);
    }


    /**
     * 读取国际化字符串
     *
     * @param key 字符串key
     * @return 国际化字符串
     */
    default String localized(String key) {
        return LanguageUtils.getString("framework", key);
    }
}
