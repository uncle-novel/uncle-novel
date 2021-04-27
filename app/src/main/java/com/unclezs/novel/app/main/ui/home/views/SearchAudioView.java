package com.unclezs.novel.app.main.ui.home.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
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
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailNode;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookDetailNode.Action;
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
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@Slf4j
@FxView(fxml = "/layout/home/views/search-audio.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchAudioView extends SidebarView<StackPane> {

  @FXML
  private ListView<String> tocListView;
  @FXML
  private JFXDrawer tocDrawer;
  @FXML
  private JFXDrawersStack drawers;
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
    listView.setCellFactory(BookListCell::new);
    // 单机查看详情
    EventUtils.setOnMousePrimaryClick(listView, event -> {
      if (!listView.getSelectionModel().isEmpty()) {
        Novel novel = listView.getSelectionModel().getSelectedItem();
        BookDetailNode bookDetailNode = new BookDetailNode(novel).withActions(Action.BOOKSHELF, Action.TOC);
        ModalBox detailModal = ModalBox.none().body(bookDetailNode).title("小说详情").cancel("关闭");
        bookDetailNode.getToc().setOnMouseClicked(e -> {
          detailModal.disabledAnimateClose().close();
          showToc();
        });
        detailModal.show();
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
    List<AnalyzerRule> searchRules = RuleManager.audioSearchRules();
    if (searchRules.isEmpty()) {
      Toast.error("未找到可用于搜索的书源");
      return;
    }
    keyword = event.getInput();
    listView.getItems().clear();
    searcher = new SearchSpider(searchRules);
    // 搜索结果处理回调
    searcher.setOnNewItemAddHandler(novel -> {
      if (SearchType.match(event.getType(), keyword, novel)) {
        Executor.runFx(() -> listView.getItems().add(novel));
      }
    });
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
    }).start();
  }

  /**
   * 查看有声小说目录
   */
  private void showToc() {
    Novel novel = listView.getSelectionModel().getSelectedItem();
    if (novel == null) {
      return;
    }
    String tocUrl = novel.getUrl();
    if (!UrlUtils.isHttpUrl(tocUrl)) {
      Toast.error("小说目录网址不正确~");
      return;
    }
    tocDrawer.close();
    tocListView.getItems().clear();
    TocSpider tocSpider = new TocSpider(RuleManager.getOrDefault(tocUrl));
    tocSpider.setOnNewItemAddHandler(chapter -> Executor.runFx(() -> tocListView.getItems().add(chapter.getName())));
    TaskFactory.create(() -> {
      tocSpider.toc(tocUrl);
      tocSpider.loadAll();
      return null;
    }).onSuccess(v -> drawers.toggle(tocDrawer))
      .onFailed(e -> {
        Toast.error("目录解析失败");
        log.error("目录查看失败：链接：{}", tocUrl, e);
      }).start();
  }
}

