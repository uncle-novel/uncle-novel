package com.unclezs.novel.app.framework.core;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.exception.FxException;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.ReflectUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.fxml.FXMLLoader;

/**
 * View工厂，负责创建Fxml和普通视图的对象，创建时会自动注入视图与国际化资源
 *
 * @author blog.unclezs.com
 * @date 2021/4/4 22:39
 * @see View
 */
public class ViewFactory {

  /**
   * 视图根节点字段名称
   */
  public static final String ROOT_FIELD_NAME = "root";
  /**
   * 国际化资源包字段名称
   */
  public static final String BUNDLE_FIELD_NAME = "bundle";
  private final Map<String, Object> views;

  public ViewFactory() {
    this.views = new ConcurrentHashMap<>();
  }

  /**
   * 注册view，普通类型的视图，不是通过Fxml创建的通过此方式注册管理
   *
   * @param clazz 类型
   * @param view  视图
   */
  public <T> T register(Class<?> clazz, T view) {
    FxView fxView = clazz.getAnnotation(FxView.class);
    if (this.views.get(clazz.getName()) != null && fxView.singleton()) {
      throw new FxException("view 已经被注册，{}", clazz);
    }
    this.views.put(clazz.getName(), view);
    // 设置国际化
    if (CharSequenceUtil.isNotBlank(fxView.bundle())) {
      ReflectUtils.setFieldValue(clazz, BUNDLE_FIELD_NAME, view, LocalizedSupport.getBundle(fxView.bundle()));
    }
    return view;
  }

  /**
   * 获取View，自动创建管理
   *
   * @param clazz 控制器
   * @param <V>   视图类型
   * @return 视图
   */
  @SuppressWarnings("unchecked")
  public <V> V getRoot(Class<?> clazz) {
    Object cache = views.get(clazz.getName());
    if (cache == null) {
      return ((View<V>) createFxmlView(clazz)).getRoot();
    }
    return (V) cache;
  }

  /**
   * 获取Controller，自动创建管理
   *
   * @param clazz 控制器
   * @return 控制器
   */
  @SuppressWarnings("unchecked")
  public <T> T getView(Class<?> clazz) {
    Object cache = views.get(clazz.getName());
    if (cache == null) {
      return createFxmlView(clazz);
    }
    return (T) cache;
  }

  /**
   * 根据Fxml创建View，自动创建管理
   *
   * @param clazz 控制器
   * @return 控制器
   */
  @SuppressWarnings("unchecked")
  private <T> T createFxmlView(Class<?> clazz) {
    FxView fxView = clazz.getAnnotation(FxView.class);
    Assert.notNull(fxView, "{}必须要标记注解@FxView", clazz);
    Assert.notBlank(fxView.fxml(), "@FxView的fxml属性不可为空 {}", clazz);
    try {
      FXMLLoader loader = new FXMLLoader();
      // 设置国际化
      if (CharSequenceUtil.isNotBlank(fxView.bundle())) {
        loader.setResources(LocalizedSupport.getBundle(fxView.bundle()));
      }
      loader.setLocation(clazz.getResource(fxView.fxml()));
      Object root = loader.load();
      Object controller = loader.getController();
      // 属性注入
      ReflectUtils.setFieldValue(clazz, ROOT_FIELD_NAME, controller, root);
      views.put(clazz.getName(), controller);
      return (T) controller;
    } catch (Exception e) {
      e.printStackTrace();
      throw new FxException("创建FXML失败：{}", fxView.fxml(), e);
    }
  }

  /**
   * 销毁 Bean出发onDestroy
   */
  public void destroy() {
    this.views.values().forEach(view -> {
      if (view instanceof View) {
        ((View<?>) view).onDestroy();
      }
    });
  }
}
