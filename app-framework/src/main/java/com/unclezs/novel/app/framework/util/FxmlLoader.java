package com.unclezs.novel.app.framework.util;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.exception.FxException;
import com.unclezs.novel.app.framework.view.BaseView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Fxml加载器
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 14:41
 */
@Slf4j
@UtilityClass
public class FxmlLoader {

  private static final String FXML_SUFFIX = ".fxml";
  private static final String CONTROLLER_SUFFIX = "View";
  private static final String BASE = "com.unclezs.novel.app.localized.";

  /**
   * 加载FXML
   *
   * @param controllerClazz controller
   * @param <T>             类型
   * @return 加载结果
   */
  public static <T> T loadView(Class<T> controllerClazz) {
    try {
      return getLoader(controllerClazz).load();
    } catch (IOException e) {
      throw new FxException("fxml load failed.", e);
    }
  }

  /**
   * 加载FXML
   *
   * @param controllerClazz controller
   * @param <T>             类型
   * @return view controller
   */
  public static <T> T load(Class<T> controllerClazz) {
    FXMLLoader loader = loadedLoader(controllerClazz);
    T controller = loader.getController();
    if (controller instanceof BaseView) {
      BaseView view = (BaseView) controller;
      view.setRoot(loader.getRoot());
    }
    return controller;
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
      fxml = fxml.concat(FXML_SUFFIX);
    }
    // 国际化资源路径
    String bundle = fxViewAnnotation == null || StrUtils.isBlank(fxViewAnnotation.bundle()) ? null : fxViewAnnotation.bundle();
    ResourceBundle resourceBundle = null;
    if (bundle != null) {
      resourceBundle = ResourceBundle.getBundle(BASE.concat(bundle));
    }
    FXMLLoader loader = new FXMLLoader(controllerClazz.getResource(fxml), resourceBundle);
    loader.setCharset(StandardCharsets.UTF_8);
    log.info("fxml loader controller:{} fxml:{} bundle:{}", controllerClazz.getName(), fxml, resourceBundle);
    return loader;
  }
}
