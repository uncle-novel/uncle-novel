package com.uncles.novel.app.jfx.framework.util;

import com.uncles.novel.app.jfx.framework.annotation.FxController;
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
    private static final String DEFAULT_FXML_LOCATION = "/layout/";
    private static final String FXML_SUFFIX = ".fxml";
    private static final String CONTROLLER_SUFFIX = "controller";

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
     * 初始化FXMLLoader
     *
     * @param controllerClazz controller
     * @return 加载结果
     */
    public static FXMLLoader getLoader(Class<?> controllerClazz) {
        FxController fxViewAnnotation = controllerClazz.getAnnotation(FxController.class);
        String controllerName = controllerClazz.getSimpleName().toLowerCase().replace(CONTROLLER_SUFFIX, "");
        // 得到fxml文件路径
        String fxml = DEFAULT_FXML_LOCATION + (fxViewAnnotation == null || StrUtils.isBlank(fxViewAnnotation.fxml()) ? controllerName : fxViewAnnotation.fxml());
        if (!fxml.endsWith(FXML_SUFFIX)) {
            fxml += FXML_SUFFIX;
        }
        // 国际化资源路径
        String bundle = fxViewAnnotation == null || StrUtils.isBlank(fxViewAnnotation.bundle()) ? controllerName : fxViewAnnotation.bundle();
        ResourceBundle resourceBundle = LanguageUtils.getBundle(bundle);
        FXMLLoader loader = new FXMLLoader(ResourceUtils.load(fxml), resourceBundle);
        loader.setCharset(StandardCharsets.UTF_8);
        log.info("fxml loader controller:{} fxml:{} bundle:{}", controllerClazz.getName(), fxml, resourceBundle.getBaseBundleName());
        return loader;
    }
}
