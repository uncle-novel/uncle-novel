package com.unclezs.novel.app.main.model.config;

import com.unclezs.novel.app.main.manager.LanguageManager;
import com.unclezs.novel.app.main.views.home.HomeView;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.Font;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/6/1 0:32
 */
@Data
public class BasicConfig {

  /**
   * 是否退出时最小化到托盘
   */
  private ObjectProperty<Boolean> tray = new SimpleObjectProperty<>(false);

  /**
   * 主题
   */
  private String theme = HomeView.DEFAULT_THEME;
  /**
   * 系统语言，读取默认
   */
  private ObjectProperty<String> lang = new SimpleObjectProperty<>(LanguageManager.name(Locale.getDefault()));
  /**
   * 系统默认字体
   */
  private ObjectProperty<String> fonts = new SimpleObjectProperty<>(Font.getDefault().getFamily());
}
