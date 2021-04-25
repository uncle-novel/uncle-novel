package com.unclezs.novel.app.framework.core;

import com.unclezs.novel.app.framework.util.ReflectUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * 应用上下文
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 17:09
 */
public class AppContext {

  /**
   * 主要的舞台key
   */
  public static final String PRIMARY_STAGE = "primary-stage";
  private static final AppContext CONTEXT = new AppContext();
  private final ViewFactory viewFactory = new ViewFactory();
  private final Map<String, Object> contextBeans = new HashMap<>();
  private final List<ContextDestroyedListener> destroyedListeners = new ArrayList<>();
  /**
   * 主要的舞台
   */
  @Setter
  @Getter
  private Stage primaryStage;

  private AppContext() {
  }

  /**
   * 注册view，普通类型的视图，不是通过Fxml创建的通过此方式注册管理
   *
   * @param clazz 类型
   * @param view  视图
   */
  public static <T> T registerView(Class<?> clazz, T view) {
    return CONTEXT.viewFactory.register(clazz, view);
  }

  /**
   * 获取View，自动创建管理
   *
   * @param clazz 控制器
   * @param <V>   视图类型
   * @return 视图
   */
  public static <V> V getRoot(Class<?> clazz) {
    return CONTEXT.viewFactory.getRoot(clazz);
  }

  /**
   * 获取Controller，自动创建管理
   *
   * @param clazz 控制器
   * @return 控制器
   */
  public static <T> T getView(Class<T> clazz) {
    return CONTEXT.viewFactory.getView(clazz);
  }

  /**
   * 获取Controller，自动创建管理
   *
   * @param className 控制器类名
   * @return 控制器
   */
  public static <T> T getView(String className) {
    Class<Object> clazz = ReflectUtils.forName(className);
    return CONTEXT.viewFactory.getView(clazz);
  }

  /**
   * 获取上下文实例
   *
   * @return 实例
   */
  public static AppContext getInstance() {
    return CONTEXT;
  }

  /**
   * 注册Bean
   *
   * @param clazz 标识类，取全限定类名
   * @param bean  实例
   */
  public void register(Class<?> clazz, Object bean) {
    register(clazz.getName(), bean);
  }

  /**
   * 注册Bean
   *
   * @param id   标识
   * @param bean 实例
   */
  public void register(String id, Object bean) {
    contextBeans.put(id, bean);
  }

  /**
   * 获取Bean
   *
   * @param id 标识
   */
  @SuppressWarnings("unchecked")
  public <T> T getBean(String id) {
    return (T) contextBeans.get(id);
  }

  /**
   * 获取Bean
   *
   * @param beanClass bean的类型
   */
  public <T> T getBean(Class<T> beanClass) {
    return getBean(beanClass.getName());
  }

  /**
   * 销毁上下文
   */
  public void destroy() {
    for (ContextDestroyedListener listener : destroyedListeners) {
      listener.contextDestroyed(CONTEXT);
    }
    viewFactory.destroy();
  }

  /**
   * 注册上下文销毁时候的监听器
   *
   * @param listener 监听器
   */
  public void registerDestroyedListener(ContextDestroyedListener listener) {
    this.destroyedListeners.add(listener);
  }

}
