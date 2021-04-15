package com.unclezs.novel.app.main.home;

import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.home.header.SettingPopupView;
import com.unclezs.novel.app.main.home.header.ThemePopupView;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/home/home.fxml", bundle = "app")
public class HomeView extends SceneView<StageDecorator> {

  public static final String DEFAULT_THEME = "default";
  public static final String HOME_STYLESHEET = "css/home/home.css";
  private ThemePopupView themePopupView;

  @Override
  public void onCreated() {
    System.out.println("HomeView created");
    // home 全局样式
    getRoot().getScene().getStylesheets().add(0, ResourceUtils.externalForm(HOME_STYLESHEET));
    // 初始化主题样式
    themePopupView = AppContext.getView(ThemePopupView.class);
    themePopupView.changeTheme(DEFAULT_THEME);
  }

  @Override
  public void onTheme(StageDecorator view, IconButton themeButton) {
    themePopupView.getRoot().show(themeButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    SettingPopupView settingPopupView = AppContext.getView(SettingPopupView.class);
    JFXPopup settingPopup = settingPopupView.getRoot();
    settingPopup.show(settingButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onShow(SceneNavigateBundle bundle) {
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
