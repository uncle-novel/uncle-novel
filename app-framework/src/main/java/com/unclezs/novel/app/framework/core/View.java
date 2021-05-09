package com.unclezs.novel.app.framework.core;

import com.unclezs.novel.app.framework.support.LocalizedSupport;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @param <V> 视图类型
 * @author blog.unclezs.com
 * @date 2021/4/12 9:52
 */
@Getter
@Setter
public class View<V> implements Initializable, LocalizedSupport {

  /**
   * 国际化资源包
   */
  protected String bundleName;
  /**
   * 控制器对应的视图
   */
  protected V root;

  /**
   * view创建之后，只做了fxml的属性注入，还没有做应用属性注入（如sidebar的navigation）
   */
  public void onCreate() {
    // Bean创建完成
  }

  /**
   * 被隐藏(场景view切换) 窗口隐藏不会被调用
   */
  public void onHidden() {
    // do something
  }

  /**
   * view被销毁时调用
   */
  public void onDestroy() {
    onHidden();
    // 一般为程序退出保持数据
  }

  @Override
  public final void initialize(URL location, ResourceBundle resources) {
    if (resources != null) {
      this.bundleName = resources.getBaseBundleName();
    }
    onCreate();
  }
}
