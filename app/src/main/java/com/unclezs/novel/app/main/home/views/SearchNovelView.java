package com.unclezs.novel.app.main.home.views;

import cn.hutool.core.io.IoUtil;
import com.jfoenix.utils.JFXUtilities;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.SearchBar;
import com.unclezs.novel.app.framework.components.SearchBar.SearchEvent;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.FluentTask;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.home.views.widgets.BookListCell;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search-novel.fxml", bundle = "app")
@EqualsAndHashCode(callSuper = true)
public class SearchNovelView extends SidebarView<StackPane> {

  @FXML
  private ListView<Novel> listView;
  @FXML
  private SearchBar searchBar;
  private SearchSpider spider;

  @Override
  public void onShown(SidebarNavigateBundle bundle) {
    searchBar.focus();
  }

  @Override
  public void onCreated() {
    // 支持多选
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    listView.setCellFactory(BookListCell::new);
    Novel novelInfo = new Novel();
    novelInfo.setAuthor("辰东");
    novelInfo.setTitle("完美世界");
    novelInfo.setCategory("东方玄幻");
    novelInfo.setWordCount("6593730");
    novelInfo.setIntroduce("一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆。群雄并起，万族林立，诸圣争霸，乱天动地。问苍茫大地，谁主沉浮？！一个少年从大荒中走出，一切从这里开始………");
    novelInfo.setLatestChapterName("第两千零一十四章 独断万古（大结局）");
    novelInfo.setCoverUrl("https://img.zhaishuyuan.com/bookpic/s191.jpg");
    novelInfo.setState("已完结");
    novelInfo.setUpdateTime("2017-8-28 19:21:17");
//    listView.getItems().add(novelInfo);

    RuleHelper.loadRules(IoUtil.readUtf8(ResourceUtils.stream("rule.json")));
    spider = new SearchSpider(RuleHelper.rules().stream().filter(AnalyzerRule::isEnabled).collect(Collectors.toList()));
    spider.setOnNewItemAddHandler(novel -> JFXUtilities.runInFX(() -> {
      System.out.println(novel);
      listView.getItems().add(novel);
    }));
  }

  @FXML
  private void search(SearchEvent event) {
    String title = event.getInput();
    String type = event.getType();
    new FluentTask<List<Novel>>() {
      @Override
      protected List<Novel> call() throws IOException {
        spider.search(title);
        return null;
      }
    }.start();
  }
}
