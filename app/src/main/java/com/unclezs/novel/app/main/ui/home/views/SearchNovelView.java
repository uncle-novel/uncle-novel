package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.text.CharSequenceUtil;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
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
import com.unclezs.novel.app.main.ui.home.views.widgets.BookListCell;
import java.util.Collections;
import java.util.List;
import javafx.event.ActionEvent;
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
    listView.setCellFactory(BookListCell::new);
    EventUtils.setOnMousePrimaryClick(listView, event -> {
      if (!listView.getSelectionModel().isEmpty()) {
        ModalBox.none().body(new BookDetailNode(listView.getSelectionModel().getSelectedItem())).title("小说详情").cancel("关闭").show();
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
      Toast.error("未找到可用于搜索的书源");
      return;
    }
    keyword = event.getInput();
    listView.getItems().clear();
    SearchType searchType = SearchType.fromValue(event.getType());
    searcher = new SearchSpider(RuleManager.textRules());
    // 搜索结果处理回调
    searcher.setOnNewItemAddHandler(novel -> {
      if (searchType == SearchType.NAME && !CharSequenceUtil.containsIgnoreCase(novel.getTitle(), keyword)) {
        return;
      }
      if (searchType == SearchType.AUTHOR
        && CharSequenceUtil.isNotBlank(novel.getAuthor()) && !novel.getAuthor().contains(keyword)
        && !CharSequenceUtil.containsIgnoreCase(novel.getTitle(), keyword)) {
        return;
      }
      Executor.runFx(() -> listView.getItems().add(novel));
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
    })
      .start();
  }

  public void add(ActionEvent actionEvent) {
    Novel novelInfo = new Novel();
    novelInfo.setAuthor("辰东");
    novelInfo.setTitle("完美世界");
    novelInfo.setUrl("https://www.zhaishuyuan.com/read/33959");
    novelInfo.setCategory("东方玄幻");
    novelInfo.setWordCount("6593730");
    novelInfo.setIntroduce("一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆。群雄并起，万族林立，诸圣争霸，乱天动地。问苍茫大地，谁主沉浮？！一个少年从大荒中走出，一切从这里开始………");
    novelInfo.setLatestChapterName("第两千零一十四章 独断万古（大结局）");
    novelInfo.setCoverUrl("https://img.zhaishuyuan.com/bookpic/s191.jpg");
    novelInfo.setState("已完结");
    novelInfo.setUpdateTime("2017-8-28 19:21:17");
    System.out.println(novelInfo.getTitle());
    listView.getItems().add(novelInfo);
  }
}
