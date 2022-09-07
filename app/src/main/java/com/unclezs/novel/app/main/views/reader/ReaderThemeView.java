package com.unclezs.novel.app.main.views.reader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.ColorUtil;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 主题
 *
 * @author blog.unclezs.com
 * @since 2021/03/06 18:16
 */
public class ReaderThemeView extends FlowPane {

  public static final String THEME_FORMAT = "css/reader/theme/%s.css";
  public static final String CUSTOM_THEME_TEMPLATE = "css/reader/theme/custom.css";
  public static final String CURRENT_THEME_STYLE_CLASS = "current";
  public static final String CUSTOM_THEME = "custom";
  public static final File CUSTOM_THEME_FILE = ResourceManager.confFile("reader-theme.css");
  private static final List<String> THEMES =
    List.of("green", "dark", "darcula", "default", "yellow", "white", "pink", "grey", "teal");
  private final ColorPicker picker;
  private final List<IconButton> themeButtons = new ArrayList<>();
  private String currentTheme;


  public ReaderThemeView() {
    NodeHelper.addClass(this, "reader-theme-view");
    picker = NodeHelper.addClass(new ColorPicker(), "color-box-item-custom");
    picker.valueProperty().addListener(e -> {
      String theme = IoUtil.readUtf8(ResourceUtils.stream(CUSTOM_THEME_TEMPLATE)).replace("custom-theme-color",
        ColorUtil.colorToHex(picker.getValue()));
      FileUtil.writeUtf8String(theme, CUSTOM_THEME_FILE);
      changeTheme(CUSTOM_THEME);
    });
    for (String themeNames : THEMES) {
      getChildren().add(createThemeButton(themeNames));
    }
    getChildren().add(picker);
  }

  private IconButton createThemeButton(String name) {
    IconButton themeButton = new IconButton();
    themeButton.setOnMouseClicked(this::changeTheme);
    themeButton.setUserData(name);
    themeButton.getStyleClass().setAll("color-box-item", name);
    themeButtons.add(themeButton);
    return themeButton;
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
    String themeName = themePalette.getUserData().toString();
    changeTheme(themeName);
  }


  /**
   * 切换主题
   *
   * @param themeName 主题样式表
   */
  public void changeTheme(String themeName) {
    String theme;
    if (CUSTOM_THEME.equals(themeName)) {
      if (CUSTOM_THEME_FILE.exists()) {
        theme = URLUtil.getURL(CUSTOM_THEME_FILE).toExternalForm();
      } else {
        themeName = "green";
        theme = String.format(THEME_FORMAT, themeName);
      }
    } else {
      theme = String.format(THEME_FORMAT, themeName);
      // 同一主题不切换
      if (Objects.equals(currentTheme, theme)) {
        return;
      }
    }
    for (IconButton themeButton : themeButtons) {
      if (themeButton.getStyleClass().contains(themeName)) {
        themeButton.getStyleClass().add(CURRENT_THEME_STYLE_CLASS);
      } else {
        themeButton.getStyleClass().remove(CURRENT_THEME_STYLE_CLASS);
      }
    }
    // 切换主题
    ObservableList<String> stylesheets = AppContext.getView(ReaderView.class).getRoot().getScene().getStylesheets();
    stylesheets.add(theme);
    stylesheets.remove(currentTheme);
    this.currentTheme = theme;
    // 记录主题
    SettingManager.manager().getReader().getThemeName().set(themeName);
  }
}
