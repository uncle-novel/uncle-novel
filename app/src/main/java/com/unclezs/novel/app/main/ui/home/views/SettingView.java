package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.main.manager.SettingManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@Slf4j
@FxView(fxml = "/layout/home/views/setting/setting.fxml")
@EqualsAndHashCode(callSuper = false)
public class SettingView extends SidebarView<StackPane> {

  @FXML
  private ComboBox<String> language;
  /**
   * 设置管理器
   */
  private SettingManager manager;

  @Override
  public void onCreated() {
    manager = SettingManager.manager();
    language.valueProperty().bindBidirectional(manager.getLang());
  }


  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

}
