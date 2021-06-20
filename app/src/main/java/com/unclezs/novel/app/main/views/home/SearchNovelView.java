package com.unclezs.novel.app.main.views.home;

import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.SearchBar;
import com.unclezs.novel.app.framework.components.SearchBar.SearchEvent;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.core.NovelSearcher;
import com.unclezs.novel.app.main.core.NovelSearcher.Callback;
import com.unclezs.novel.app.main.enums.SearchType;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import com.unclezs.novel.app.main.views.components.BookDetailModal;
import com.unclezs.novel.app.main.views.components.BookDetailModal.Action;
import com.unclezs.novel.app.main.views.components.cell.BookListCell;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 搜索小说页面
 *
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@Slf4j
@FxView(fxml = "/layout/home/search-novel.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNovelView extends SidebarView<StackPane> implements Callback {

  private static final String PAGE_NAME = "搜索小说";
  @FXML
  private JFXProgressBar loadingBar;
  @FXML
  private IconButton loading;
  @FXML
  private ListView<Novel> listView;
  @FXML
  private SearchBar searchBar;
  private ScrollBar scrollBar;
  private NovelSearcher novelSearcher;

  @Override
  public void onShown(SidebarNavigateBundle bundle) {
    searchBar.focus();
  }

  @Override
  public void onCreated() {
    searchBar.addTypes(SearchType.ALL.getDesc(), SearchType.NAME.getDesc(), SearchType.AUTHOR.getDesc());
    listView.setCellFactory(BookListCell::new);
    // 单机查看详情
    EventUtils.setOnMousePrimaryClick(listView, event -> {
      if (!listView.getSelectionModel().isEmpty()) {
        Novel novel = listView.getSelectionModel().getSelectedItem();
        new BookDetailModal(novel).withActions(Action.BOOKSHELF, Action.ANALYSIS, Action.DOWNLOAD).show();
      }
    });
    loading.setOnAction(e -> novelSearcher.cancel());
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    MixPanelHelper.event(PAGE_NAME);
  }

  /**
   * 点击搜索
   *
   * @param event 搜索事件
   */
  @FXML
  private void search(SearchEvent event) {
    listView.getItems().clear();
    if (novelSearcher == null) {
      novelSearcher = new NovelSearcher(false, this);
    }
    novelSearcher.doSearch(event.getInput(), event.getType());
  }

  /**
   * 加载更多数据
   */
  public void loadMore() {
    if (scrollBar.getValue() != 1 || !novelSearcher.isHasMore() || novelSearcher.isSearching()) {
      return;
    }
    // 加载更多
    scrollBar.setValue(1 - 0.00001);
    novelSearcher.loadMore();
  }

  @Override
  public void showLoading(boolean show) {
    if (show) {
      loading.setVisible(true);
      loading.setManaged(true);
      loadingBar.setVisible(true);
    } else {
      loading.setVisible(false);
      loading.setManaged(false);
      loadingBar.setVisible(false);
    }
  }

  @Override
  public void addItem(Novel novel) {
    listView.getItems().add(novel);
    // 获取滚动条，用于滚动到底部加载更多
    if (scrollBar == null) {
      scrollBar = NodeHelper.findVBar(listView);
      scrollBar.valueProperty().addListener(e -> loadMore());
    }
  }
}
