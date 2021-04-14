package com.unclezs.novel.app.framework.appication;

import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.view.View;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景View个标记 {@link SsaApplication}
 *
 * @author blog.unclezs.com
 * @since 2021/03/04 12:03
 */
public abstract class SceneView<V extends Parent> extends View<V> implements StageDecorator.ActionHandler {

  @Getter
  @Setter
  private Scene scene;

  /**
   * 场景显示时候触发(场景view切换) 窗口隐藏不会被调用
   *
   * @param bundle 携带的数据
   */
  public void onShow(SceneViewNavigateBundle bundle) {

  }

  /**
   * 被隐藏(场景view切换) 窗口隐藏不会被调用
   */
  public void onHidden() {

  }

  /**
   * 当场景创建完毕
   *
   * @param scene 场景
   */
  public void onSceneCreated(Scene scene) {

  }
}
