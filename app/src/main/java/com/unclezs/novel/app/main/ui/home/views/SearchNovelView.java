package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.SearchBar;
import com.unclezs.novel.app.framework.components.SearchBar.SearchEvent;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.enums.SearchType;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailModal;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailModal.Action;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookListCell;
import java.util.Collections;
import java.util.List;
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
public class SearchNovelView extends SidebarView<StackPane> {

  @FXML
  private ListView<Novel> listView;
  @FXML
  private SearchBar searchBar;
  private SearchSpider searcher;
  private ScrollBar scrollBar;
  private String keyword;

  @Override
  public void onShown(SidebarNavigateBundle bundle) {
    searchBar.focus();
  }

  @Override
  public void onCreated() {
    searchBar.addTypes(SearchType.NAME.getDesc(), SearchType.AUTHOR.getDesc());
    listView.setCellFactory(BookListCell::new);
    // 单机查看详情
    EventUtils.setOnMousePrimaryClick(listView, event -> {
      if (!listView.getSelectionModel().isEmpty()) {
        Novel novel = listView.getSelectionModel().getSelectedItem();
        new BookDetailModal(novel).withActions(Action.BOOKSHELF, Action.ANALYSIS, Action.DOWNLOAD).show();
      }
    });
  }

  /**
   * 点击搜索
   *
   * @param event 搜索事件
   */
  @FXML
  private void search(SearchEvent event) {
    List<AnalyzerRule> searchRules = RuleManager.textSearchRules();
    if (searchRules.isEmpty()) {
      Toast.error("未找到可用于搜索的有声书源");
      return;
    }
    searcher = new SearchSpider(searchRules);
    // 搜索结果处理回调
    searcher.setOnNewItemAddHandler(novel -> {
      if (SearchType.match(event.getType(), keyword, novel)) {
        Executor.runFx(() -> listView.getItems().add(novel));
      }
    });
    listView.getItems().clear();
    keyword = event.getInput();
    // 开始搜索
    TaskFactory.create(() -> {
      searcher.search(keyword);
      return Collections.emptyList();
    }).onSuccess(v -> {
      // 获取滚动条，用于滚动到底部加载更多
      if (scrollBar == null) {
        scrollBar = NodeHelper.findVBar(listView);
        scrollBar.valueProperty().addListener(e -> this.loadMore());
      }
    }).start();
  }

  /**
   * 加载更多数据
   */
  public void loadMore() {
    if (scrollBar.getValue() != 1 || !searcher.hasMore() || searcher.isCanceled()) {
      return;
    }
    // 加载更多
    scrollBar.setValue(1 - 0.00001);
    TaskFactory.create(() -> {
      searcher.loadMore();
      return searcher.hasMore();
    }).onSuccess(hasMore -> {
      if (Boolean.FALSE.equals(hasMore)) {
        Toast.info(getRoot(), "没有更多了");
      }
    }).onFailed(e -> {
      Toast.error("加载失败");
      log.error("小说搜索失败:{}", searcher.getKeyword(), e);
    })
      .start();
  }
}
