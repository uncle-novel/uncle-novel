package com.unclezs.novel.app.main.views.home;

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
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.main.db.beans.SearchEngine;
import com.unclezs.novel.app.main.db.dao.SearchEngineDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.RuleManager;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
@FxView(fxml = "/layout/home/search-network.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNetworkView extends SidebarView<StackPane> {


  public static final String KEYWORD = "{{keyword}}";
  @FXML
  private JFXProgressBar progress;
  @FXML
  private ListView<String> tocListView;
  @FXML
  private JFXDrawer tocDrawer;
  @FXML
  private JFXDrawersStack drawer;
  @FXML
  private VBox placeholder;
  @FXML
  private WebView webview;
  @FXML
  private SearchBar searchBar;
  private WebEngine engine;
  private ObservableList<SearchEngine> searchEngines;
  private LoadListener loadListener;

  @Override
  public void onCreate() {
    this.engine = webview.getEngine();
    this.loadListener = new LoadListener(webview);
    this.engine.getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
      progress.setProgress(newValue.doubleValue() == 0 ? 0.1 : newValue.doubleValue());
      progress.setVisible(progress.getProgress() != 1);
    });
    this.engine.getLoadWorker().stateProperty().addListener((ob, ov, nv) -> {
      if (nv == State.RUNNING) {
        initWebViewStylesheet();
      }
    });
    engine.setUserAgent(RequestParams.USER_AGENT_DEFAULT_VALUE);
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    String current = searchBar.getCurrentType();
    List<String> types = searchEngines.stream()
      .filter(searchEngine -> Boolean.TRUE.equals(searchEngine.getEnabled()))
      .map(SearchEngine::getName)
      .collect(Collectors.toList());
    // 是否有变化
    if (!types.equals(searchBar.getTypeItems())) {
      searchBar.clearType();
      searchBar.addTypes(types);
      if (types.contains(current)) {
        searchBar.setType(current);
      }
    }
  }

  @Override
  public void onCreated() {
    this.searchEngines = SearchEngineDao.me().all();
    // 监听变化
    searchEngines.addListener((ListChangeListener<SearchEngine>) c -> {
      while (c.next()) {
        for (SearchEngine searchEngine : c.getRemoved()) {
          searchBar.removeType(searchEngine.getName());
        }
        for (SearchEngine searchEngine : c.getAddedSubList()) {
          if (Boolean.TRUE.equals(searchEngine.getEnabled())) {
            searchBar.addType(searchEngine.getName());
          }
        }
      }
    });
    // 初始化
    searchEngines.stream()
      .filter(searchEngine -> Boolean.TRUE.equals(searchEngine.getEnabled()))
      .forEach(searchEngine -> searchBar.addType(searchEngine.getName()));
  }

  /**
   * 提交搜索
   *
   * @param event 搜索事件
   */
  public void search(SearchEvent event) {
    if (placeholder != null) {
      StackPane parent = (StackPane) placeholder.getParent();
      parent.getChildren().remove(placeholder);
      placeholder = null;
    }

    String type = event.getType();
    String keyword = event.getInput();
    for (SearchEngine searchEngine : searchEngines) {
      if (searchEngine.getName().equals(type)) {
        engine.load(searchEngine.getUrl().replace(KEYWORD, keyword));
        webview.setVisible(false);
        engine.getLoadWorker().progressProperty().addListener(loadListener);
        return;
      }
    }
    Toast.error("没有搜索引擎可用于搜索");
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

  /**
   * 设置webview自定义样式，没有则设置为null
   */
  private void initWebViewStylesheet() {
    SearchEngine searchEngine = findSearchEngineByCurrentLocation();
    if (searchEngine == null) {
      this.engine.setUserStyleSheetLocation(null);
    } else {
      this.engine.setUserStyleSheetLocation(ResourceManager.findResource(searchEngine.getStylesheet()));
    }
  }

  /**
   * 查找当前页面的搜索引擎
   *
   * @return 搜索引擎
   */
  private SearchEngine findSearchEngineByCurrentLocation() {
    String location = UrlUtils.getHost(engine.getLocation());
    for (SearchEngine searchEngine : searchEngines) {
      if (searchEngine.getDomain().contains(location)) {
        return searchEngine;
      }
    }
    return null;
  }

  static class LoadListener implements InvalidationListener {

    private final WebView webView;

    public LoadListener(WebView webView) {
      this.webView = webView;
    }

    @Override
    public void invalidated(Observable observable) {
      if (webView.getEngine().getLoadWorker().getProgress() > 0.6D) {
        webView.setVisible(true);
        webView.getEngine().getLoadWorker().progressProperty().removeListener(this);
      }
    }
  }
}
