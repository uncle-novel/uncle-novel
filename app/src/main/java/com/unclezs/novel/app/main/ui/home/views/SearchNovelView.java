package com.unclezs.novel.app.main.ui.home.views;

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
import com.unclezs.novel.app.main.enums.SearchType;
import com.unclezs.novel.app.main.service.SearchService;
import com.unclezs.novel.app.main.service.SearchService.Callback;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailModal;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailModal.Action;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookListCell;
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
@FxView(fxml = "/layout/home/views/search-novel.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNovelView extends SidebarView<StackPane> implements Callback {

  @FXML
  private JFXProgressBar loadingBar;
  @FXML
  private IconButton loading;
  @FXML
  private ListView<Novel> listView;
  @FXML
  private SearchBar searchBar;
  private ScrollBar scrollBar;
  private SearchService searchService;

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
    loading.setOnAction(e -> searchService.cancel());
  }

  /**
   * 点击搜索
   *
   * @param event 搜索事件
   */
  @FXML
  private void search(SearchEvent event) {
    listView.getItems().clear();
    if (searchService == null) {
      searchService = new SearchService(false, this);
    }
    searchService.doSearch(event.getInput(), event.getType());
  }

  /**
   * 加载更多数据
   */
  public void loadMore() {
    if (scrollBar.getValue() != 1 || !searchService.isHasMore() || searchService.isSearching()) {
      return;
    }
    // 加载更多
    scrollBar.setValue(1 - 0.00001);
    searchService.loadMore();
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
