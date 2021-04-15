package com.unclezs.novel.app.main.home.views;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@Slf4j
@FxView(fxml = "/layout/home/views/setting/setting.fxml")
public class SettingView extends SidebarView<StackPane> {

  @Override
  public void onCreated() {
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    System.out.println(bundle.getData());
  }
}
