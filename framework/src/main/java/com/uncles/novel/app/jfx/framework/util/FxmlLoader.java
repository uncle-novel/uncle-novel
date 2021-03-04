package com.uncles.novel.app.jfx.framework.util;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.exception.FxException;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Fxml加载器
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
public class FxmlLoader {
    public static final Logger log = LoggerFactory.getLogger(FxmlLoader.class);
    private static final String FXML_SUFFIX = ".fxml";
    private static final String CONTROLLER_SUFFIX = "View";

    private FxmlLoader() {
    }

    /**
     * 加载FXML
     *
     * @param controllerClazz controller
     * @param <T>             类型
     * @return 加载结果
     */
    public static <T> T load(Class<?> controllerClazz) {
        try {
            return getLoader(controllerClazz).load();
        } catch (IOException e) {
            throw new FxException("fxml load failed.", e);
        }
    }

    /**
     * 加载FXML
     *
     * @param loader 加载器
     * @param <T>    类型
     * @return 加载结果
     */
    public static <T> T load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new FxException("fxml load failed.", e);
        }
    }

    /**
     * 加载FXML之后,返回FXMLLoader对象
     *
     * @param controllerClazz controller
     * @return 已经被load的FXMLLoader
     */
    public static FXMLLoader loadedLoader(Class<?> controllerClazz) {
        try {
            FXMLLoader fxmlLoader = getLoader(controllerClazz);
            fxmlLoader.load();
            return fxmlLoader;
        } catch (IOException e) {
            throw new FxException("fxml load failed.", e);
        }
    }

    /**
     * 初始化FXMLLoader
     *
     * @param controllerClazz controller
     * @return 加载结果
     */
    public static FXMLLoader getLoader(Class<?> controllerClazz) {
        FxView fxViewAnnotation = controllerClazz.getAnnotation(FxView.class);
        String replace = controllerClazz.getSimpleName().replace(CONTROLLER_SUFFIX, StrUtils.EMPTY);
        String controllerName = StrUtils.toUnderlineCase(replace);
        // 得到fxml文件路径
        String fxml = fxViewAnnotation == null || StrUtils.isBlank(fxViewAnnotation.fxml()) ? controllerName : fxViewAnnotation.fxml();
        if (!fxml.endsWith(FXML_SUFFIX)) {
            fxml += FXML_SUFFIX;
        }
        // 国际化资源路径
        String bundle = fxViewAnnotation == null || StrUtils.isBlank(fxViewAnnotation.bundle()) ? null : fxViewAnnotation.bundle();
        ResourceBundle resourceBundle = null;
        if (bundle != null) {
            resourceBundle = LanguageUtils.getBundle(bundle);
        }
        FXMLLoader loader = new FXMLLoader(ResourceUtils.load(fxml), resourceBundle);
        loader.setCharset(StandardCharsets.UTF_8);
        log.info("fxml loader controller:{} fxml:{} bundle:{}", controllerClazz.getName(), fxml, resourceBundle);
        return loader;
    }

    /**
     * 组件加载
     *
     * @param root 控制器也是rootView
     */
    public static void loadView(Object root) {
        FXMLLoader loader = getLoader(root.getClass());
        loader.setRoot(root);
        loader.setController(root);
        load(loader);
    }
}
