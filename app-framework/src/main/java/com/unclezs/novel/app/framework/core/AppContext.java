package com.unclezs.novel.app.framework.core;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * 应用上下文
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 17:09
 */
@UtilityClass
public class AppContext {

  private static final ViewFactory FACTORY = new ViewFactory();
  @Getter
  @Setter
  private static Stage stage;

  /**
   * 注册view，普通类型的视图，不是通过Fxml创建的通过此方式注册管理
   *
   * @param clazz 类型
   * @param view  视图
   */
  public static <T> T register(Class<?> clazz, T view) {
    return FACTORY.register(clazz, view);
  }

  /**
   * 获取View，自动创建管理
   *
   * @param clazz 控制器
   * @param <V>   视图类型
   * @return 视图
   */
  public static <V> V getRoot(Class<?> clazz) {
    return FACTORY.getRoot(clazz);
  }

  /**
   * 获取Controller，自动创建管理
   *
   * @param clazz 控制器
   * @return 控制器
   */
  public static <T> T getView(Class<?> clazz) {
    return FACTORY.getView(clazz);
  }

  /**
   * 停止
   */
  public void stop() {
    FACTORY.destroy();
  }
}
