package com.uncles.novel.app.jfx.framework.view;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 基本视图 一切Fxml视图应该继承自此
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:07
 */
public class BaseView {
    /**
     * 国际化资源文件 自动注入
     */
    public ResourceBundle resources;
    /**
     * Fxml路径
     */
    public URL location;

    /**
     * 默认的初始化方法 FXMLLoader加载时自动反射调用
     */
    public void initialize() {
        onCreated();
    }

    /**
     * fxml被加载完成后调用，此时fxml field已经被注入完成
     */
    public void onCreated() {
        // overwrite me.
    }

    /**
     * 读取国际化字符串
     *
     * @param key 字符串key
     * @return 国际化字符串
     */
    protected final String localized(String key) {
        return resources.getString(key);
    }
}
