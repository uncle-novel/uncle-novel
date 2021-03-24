package com.unclezs.novel.app.jfx.framework.ui.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import lombok.Setter;

/**
 * 基本视图 一切 Fxml视图应该继承自此
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
  @FXML
  @Setter
  private Object root;

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

  /**
   * 获取 fxml对应的根节点，也就是view
   *
   * @param <T> 类型
   * @return view
   */
  @SuppressWarnings("unchecked")
  public <T> T getView() {
    if (root == null) {
      return null;
    }
    return (T) root;
  }
}
