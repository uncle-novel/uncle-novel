package com.unclezs.novel.app.main.ui.home.header;

import com.jfoenix.controls.JFXPopup;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.core.View;
import com.unclezs.novel.app.main.ui.home.HomeView;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


/**
 * 主题
 *
 * @author blog.unclezs.com
 * @date 2021/03/06 18:16
 */
@FxView(fxml = "/layout/home/header/theme.fxml")
public class ThemePopupView extends View<JFXPopup> {

  public static final String THEME_FORMAT = "css/home/theme/%s.css";
  public static final String CURRENT_THEME_STYLE_CLASS = "current";
  public VBox box;
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
    if (themePalette.getStyleClass().contains(CURRENT_THEME_STYLE_CLASS)) {
      return;
    }
    changeTheme(themePalette.getUserData().toString());
    getRoot().hide();
  }


  /**
   * 切换主题
   *
   * @param themeName 主题样式表
   */
  public void changeTheme(String themeName) {
    String theme = String.format(THEME_FORMAT, themeName);
    // 同一主题不切换
    if (Objects.equals(currentTheme, theme)) {
      return;
    }
    // 设置当前主题
    Node current = box.lookup(".".concat(CURRENT_THEME_STYLE_CLASS));
    if (current != null) {
      current.getStyleClass().remove(CURRENT_THEME_STYLE_CLASS);
    }
    Node node = box.lookup(".".concat(themeName));
    node.getStyleClass().add(CURRENT_THEME_STYLE_CLASS);
    // 切换主题
    ObservableList<String> stylesheets = scene.getStylesheets();
    stylesheets.add(theme);
    stylesheets.remove(currentTheme);
    this.currentTheme = theme;
  }
}
