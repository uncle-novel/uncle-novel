package com.unclezs.novel.app.main.ui.home.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.TocSpider;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.SearchBar;
import com.unclezs.novel.app.framework.components.SearchBar.SearchEvent;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.manager.RuleManager;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

  public static final String BAIDU_SEARCH = "https://www.baidu.com/s?wd=";
  public static final String BAIDU_SEARCH_KEYWORD = "title: (阅读 \"%s\" (最新章节) -(官方网站))";

  public JFXProgressBar progress;
  public IconButton script;
  @FXML
  public ListView<String> tocListView;
  @FXML
  public JFXDrawer tocDrawer;
  public JFXDrawersStack drawer;
  @FXML
  private WebView webview;
  @FXML
  private SearchBar searchBar;
  private WebEngine engine;

  @Override
  public void onCreate() {
    this.engine = webview.getEngine();
    this.engine.getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
      progress.setProgress(newValue.doubleValue() == 0 ? 0.1 : newValue.doubleValue());
      progress.setVisible(progress.getProgress() != 1);
    });
    this.engine.getLoadWorker().stateProperty().addListener((ob, ov, nv) -> {
      if (nv == State.SCHEDULED) {
        String domain = UrlUtils.getDomain(engine.getLocation());
        if ("baidu".equals(domain)) {
          engine.setUserStyleSheetLocation(ResourceUtils.externalForm("css/home/views/webview/baidu.css"));
        } else {
          engine.setUserStyleSheetLocation(null);
        }
      }
    });
    engine.setUserAgent(RequestParams.USER_AGENT_DEFAULT_VALUE);
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

  @Override
  public void onCreated() {
  }


  public void search(SearchEvent event) {
    String type = event.getType();
    String keyword = event.getInput();
    String url;
    switch (type) {
      case "百度":
        url = BAIDU_SEARCH + String.format(BAIDU_SEARCH_KEYWORD, keyword);
        break;
      case "谷歌":
        url = String.format("https://www.google.com/search?q=%s", keyword);
        break;
      case "必应":
      default:
        url = String.format("https://www.bing.com/search?q=%s", keyword);
    }
    System.out.println(url);
    engine.load(url);
    if (!webview.isVisible()) {
      webview.setVisible(true);
    }
  }

  /**
   * 直接显示目录
   */
  public void showToc() {
    if (engine.getLocation() == null) {
      Toast.error(getRoot(), "请先搜索吧~");
      return;
    }
    tocDrawer.close();
    tocListView.getItems().clear();
    TocSpider tocSpider = new TocSpider(RuleManager.getOrDefault(engine.getLocation()));
    tocSpider.setOnNewItemAddHandler(chapter -> Executor.runFx(() -> tocListView.getItems().add(chapter.getName())));
    TaskFactory.create(() -> {
      tocSpider.toc(engine.getLocation());
      tocSpider.loadAll();
      return null;
    }).onSuccess(v -> drawer.toggle(tocDrawer)).onFailed(e -> {
      Toast.error("目录解析失败");
      log.error("目录查看失败：链接：{}", engine.getLocation(), e);
    }).start();
  }

  /**
   * 跳转解析下载
   */
  public void analysisDownload() {
    if (engine.getLocation() == null) {
      Toast.error(getRoot(), "请先搜索吧~");
      return;
    }
    Novel novel = new Novel();
    novel.setUrl(engine.getLocation());
    navigation.navigate(AnalysisDownloadView.class, new SidebarNavigateBundle().put(AnalysisDownloadView.BUNDLE_KEY_NOVEL_INFO, novel));
  }
}
