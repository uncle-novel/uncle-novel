package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import java.io.File;
import javafx.collections.ObservableList;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 15:23
 */
@FxView(fxml = "/layout/home/home.fxml")
public class HomeView extends SceneView<StageDecorator> {

  public static final File FONT_CSS_FILE = ResourceManager.confFile("font.css");
  public static final String FONT_CSS_FORMAT = ".root{-fx-font-family: '%s';}";
  public static final String DEFAULT_THEME = "default";
  private ThemeView themeView;

  @Override
  public void onCreated() {
    getRoot().getScene().getStylesheets().add("css/home/home.css");
    // 初始化主题样式
    themeView = AppContext.getView(ThemeView.class);
    themeView.changeTheme(SettingManager.manager().getBasic().getTheme());
    // 初始化默认字体
    changeFont(SettingManager.manager().getBasic().getFonts().get());
  }

  @Override
  public void onTheme(StageDecorator view, IconButton themeButton) {
    MixPanelHelper.event("主页头部主题");
    themeView.getRoot().show(themeButton, PopupVPosition.TOP, PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    MixPanelHelper.event("主页头部菜单");
    HeaderMenuView headerMenuView = AppContext.getView(HeaderMenuView.class);
    JFXPopup settingPopup = headerMenuView.getRoot();
    settingPopup.show(settingButton, PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, 40);
  }

  @Override
  public void onClose(StageDecorator view, IconButton closeButton) {
    if (Boolean.TRUE.equals(SettingManager.manager().getBasic().getTray().get())) {
      App.tray();
    } else {
      App.stopApp();
    }
  }

  /**
   * 更改系统字体
   *
   * @param font 字体
   */
  public void changeFont(String font) {
    String css = String.format(FONT_CSS_FORMAT, font);
    FileUtil.writeUtf8String(css, FONT_CSS_FILE);
    String fontCssUrl = URLUtil.getURL(FONT_CSS_FILE).toExternalForm();
    ObservableList<String> stylesheets = getRoot().getScene().getStylesheets();
    stylesheets.remove(fontCssUrl);
    stylesheets.add(fontCssUrl);
  }
}
