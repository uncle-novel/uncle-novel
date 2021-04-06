package com.unclezs.novel.app.main.ui.pages.reader;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.appication.SceneViewNavigateBundle;
import com.unclezs.novel.app.framework.components.IconButton;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.main.ui.app.App;
import com.unclezs.novel.app.main.ui.pages.home.HomeSceneView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@FxView(fxml = "/layout/reader/reader.fxml", bundle = "i18n.framework")
public class ReaderSceneView extends SceneView {

  public HBox box;
  public ToggleButton btn1;
  public ToggleButton btn2;
  public ToggleButton btn3;
  public ToggleButton btn4;

  @Override
  public void onCreated() {
    System.out.println("ReaderView created");
  }

  public void toHome() {
    App.redirect(HomeSceneView.class, new SceneViewNavigateBundle().put("data", "reader"));
  }

  @Override
  public void onTheme(StageDecorator view, IconButton themeButton) {
    System.out.println("主题被点击1");
  }

  @Override
  public void onShow(SceneViewNavigateBundle bundle) {
    System.out.println("ReaderView show");
    String data = bundle.get("data");
    if (data != null) {
      System.out.println("reader收到数据：" + data);
    }
  }

  @Override
  public void onHidden() {
    System.out.println("ReaderView hidden");
  }

  @Override
  public void onDestroy() {
    System.out.println("ReaderView destroy");
  }
}
