package com.unclezs.enmu;

import java.io.Serializable;

/**
 * 国际化
 *
 * @author uncle
 * @date 2020/4/27 11:34
 */
public enum LanguageLocale implements Serializable {
    /**
     * 中文
     */
    ZH_CN("zh_CN", "简体中文"),
    /**
     * 英文
     */
    CN_US("en_US", "English");

    private static final long serialVersionUID = 1L;
    private String locale;
    private String name;

    LanguageLocale(String locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return name;
    }
}
