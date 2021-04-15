package com.unclezs.novel.app.framework.appication;

import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.core.View;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景View个标记 {@link BaseApplication}
 *
 * @author blog.unclezs.com
 * @since 2021/03/04 12:03
 */
public abstract class SceneView<V extends Parent> extends View<V> implements StageDecorator.ActionHandler {

  @Setter
  @Getter
  protected BaseApplication app;

  /**
   * 场景显示时候触发(场景view切换) 窗口隐藏不会被调用
   *
   * @param bundle 携带的数据
   */
  public void onShow(SceneNavigateBundle bundle) {
    // do something
  }

  /**
   * 被隐藏(场景view切换) 窗口隐藏不会被调用
   */
  public void onHidden() {
    // do something
  }

  /**
   * 当场景创建完毕
   */
  public void onCreated() {
    // do something
  }
}
