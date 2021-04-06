package com.unclezs.novel.app.main.ui.pages.home;

import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.appication.SceneViewNavigateBundle;
import com.unclezs.novel.app.framework.components.IconButton;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigationPane;
import com.unclezs.novel.app.framework.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.util.FxmlLoader;
import com.unclezs.novel.app.main.ui.pages.home.header.SettingPopupView;
import com.unclezs.novel.app.main.ui.pages.home.header.ThemePopupView;
import javafx.scene.Scene;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/home/home.fxml", bundle = "app.home")
public class HomeSceneView extends SceneView {

  public StageDecorator root;
  public SidebarNavigationPane container;
  private ThemePopupView themePopupView;

  public void print() {
    System.out.println(localized("Uncle小说"));
  }

  @Override
  public void onSceneCreated(Scene scene) {
    System.out.println("MainView created");
    scene.getStylesheets().setAll(HomeSceneView.class.getResource(String.format(ThemePopupView.THEME_FORMAT, "default")).toExternalForm());
  }

  @Override
  public void onTheme(StageDecorator view, IconButton themeButton) {
    if (themePopupView == null) {
      themePopupView = FxmlLoader.load(ThemePopupView.class);
    }
    JFXPopup themePopup = themePopupView.getView();
    themePopup.show(themeButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    SettingPopupView settingPopupView = FxmlLoader.load(SettingPopupView.class);
    JFXPopup settingPopup = settingPopupView.getView();
    settingPopup
      .show(settingButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onShow(SceneViewNavigateBundle bundle) {
    System.out.println("MainView show");
    String data = bundle.get("data");
    if (data != null) {
      System.out.println("MainView收到数据：" + data);
    }
  }

  @Override
  public void onHidden() {
    System.out.println("MainView hidden");
  }

  @Override
  public void onDestroy() {
    new Thread(HotKeyManager::unbind).start();
    System.out.println("MainView destroy");
  }
}
