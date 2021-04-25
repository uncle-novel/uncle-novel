package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 全网搜书
 *
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@Slf4j
@FxView(fxml = "/layout/home/views/search-network.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNetworkView extends SidebarView<StackPane> {

  @Override
  public void onCreate() {
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

  @Override
  public void onCreated() {

  }
}
