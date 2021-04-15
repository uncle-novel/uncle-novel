package com.unclezs.novel.app.main.home.header;

import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.core.View;
import com.unclezs.novel.app.main.home.HomeView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;


/**
 * 主题
 *
 * @author blog.unclezs.com
 * @date 2021/03/06 18:16
 */
@FxView(fxml = "/layout/home/header/theme.fxml")
public class ThemePopupView extends View<JFXPopup> {

  public static final String THEME_FORMAT = "css/home/theme/%s.css";
  @FXML
  private JFXPopup popup;
  private String currentTheme;
  private Scene scene;

  @Override
  public void onCreate() {
    HomeView homeView = AppContext.getView(HomeView.class);
    this.scene = homeView.getRoot().getScene();
  }

  /**
   * 切换主题
   *
   * @param event 点击切换
   */
  public void changeTheme(MouseEvent event) {
    Node themePalette = (Node) event.getSource();
    changeTheme(themePalette.getUserData().toString());
    popup.hide();
  }


  /**
   * 切换主题
   *
   * @param themeName 主题样式表
   */
  public void changeTheme(String themeName) {
    String theme = String.format(THEME_FORMAT, themeName);
    ObservableList<String> stylesheets = scene.getStylesheets();
    stylesheets.remove(currentTheme);
    stylesheets.add(theme);
    this.currentTheme = theme;
  }
}
