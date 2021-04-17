package com.unclezs.novel.app.main.home.views;

import com.jfoenix.controls.JFXButton;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import javafx.scene.control.TextField;
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
@FxView(fxml = "/layout/home/views/search_network.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNetworkView extends SidebarView<StackPane> {

  private static final String INDEX_URL = "https://www.baidu.com/s?wd=遮天 小说章节列表";
  public JFXButton search;
  public TextField input;
//  private WebEngine engine;

  @Override
  public void onCreate() {
    search.setOnMouseClicked(event -> {
    });
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    if (bundle.getData() != null) {
      log.trace("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
    }
  }

  @Override
  public void onCreated() {

  }
}
