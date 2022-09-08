package com.unclezs.gui.utils;

import com.unclezs.gui.extra.FXController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 加载FXML\CSS工具
 * 读取@FXController注解获取fxml路径
 *
 * @author uncle
 * @date 2020/4/18 10:15
 */
public class ResourceUtil {
    /**
     * 国际化语言包
     */
    private static ResourceBundle BUNDLE;

    /**
     * 根据@FXController注解加载FXML
     *
     * @param clazz fxml对应的controller，必须带有@FXController注解
     * @return /
     */
    public static <T> T loadFxml(Class clazz) throws IOException {
        return getFxmlLoader(clazz).load();
    }

    /**
     * 根据@FXController注解加载FXML
     *
     * @param clazz fxml对应的controller，必须带有@FXController注解
     * @return /
     */
    public static FXMLLoader getFxmlLoader(Class clazz) {
        FXController annotation = (FXController) clazz.getAnnotation(FXController.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到@FxController注解 " + clazz.getName());
        }
        String fxmlPath = annotation.prefix() + annotation.value() + annotation.suffix();
        return new FXMLLoader(ResourceUtil.class.getResource(fxmlPath), getResourceBundle());
    }

    public static <T> T loadFxml(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(ResourceUtil.class.getResource(fxmlPath), getResourceBundle());
        return loader.load();
    }

    /**
     * 加载css
     *
     * @param path css文件路径相对于resources文件夹  eg: /css/xx.css
     * @return /
     */
    public static String loadCss(String path) {
        return ResourceUtil.class.getResource(path).toExternalForm();
    }

    /**
     * 获取国际化资源文件
     *
     * @return /
     */
    private static ResourceBundle getResourceBundle() {
        if (BUNDLE == null) {
            String[] language = DataManager.application.getSetting().getLanguage().get().getLocale().split("_");
            Locale locale = new Locale(language[0], language[1].toUpperCase());
            BUNDLE = ResourceBundle.getBundle("locale.lang", locale);
        }
        return BUNDLE;
    }

    /**
     * 拿到国际化后的字符串
     *
     * @param key /
     * @return /
     */
    public static String getString(String key) {
        return getResourceBundle().getString(key);
    }
}
