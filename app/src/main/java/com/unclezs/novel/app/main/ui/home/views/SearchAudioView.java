package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import javafx.scene.layout.BorderPane;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search-audio.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchAudioView extends SidebarView<BorderPane> {


  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

  @Override
  public void onCreated() {
  }
}

