package com.unclezs.novel.app.main.ui.home.views;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.InputBox;
import com.unclezs.novel.app.framework.components.InputBox.ActionClickedEvent;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.main.spi.WebEngineHttpClient;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Worker.State;
import javafx.scene.control.TextField;
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

  public WebView webview;
  public InputBox inputBox;
  public TextField cookieField;
  public IconButton load;
  public TextField userAgent;
  private WebEngine engine;

  @Override
  public void onCreate() {
    this.engine = webview.getEngine();
    userAgent.setText(RequestParams.USER_AGENT_DEFAULT_VALUE);
    cookieField.setText(
      "_csrfToken=fZv0AQ23TMWdRtGTrwuwhvN1ZYlPWVshExFEkwgd; newstatisticUUID=1619406065_349900643; qdrs=0|3|0|0|1; showSectionCommentGuide=1; qdgd=1; rcr=1735921; e1={\"pid\":\"qd_P_my_bookshelf\",\"eid\":\"qd_M188\",\"l1\":2}; e2={\"pid\":\"qd_P_recentread\",\"eid\":\"\",\"l3\":3,\"l1\":3}; bc=1735921; ywguid=1585503310; ywkey=ywBcZuAE5xgd; ywopenid=7387FAAE42281F44434B7CF4977CFD3E; pageOps=1; lrbc=1735921|30194132|1");
    String url = "https://vipreader.qidian.com/chapter/1735921/30194132";
    inputBox.getInput().setText(url);
    engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == State.SUCCEEDED) {
        engine.executeScript("window.scrollTo({ top:200})");
      }
    });
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

  @Override
  public void onCreated() {
    load.setOnAction(e -> {
      try {
        Map<String, List<String>> map = CookieHandler.getDefault().get(URI.create(UrlUtils.getSite(inputBox.getInput().getText()).concat("/")), Collections.emptyMap());
        System.out.println(map);
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    });
  }

  public void load(ActionClickedEvent actionClickedEvent) {
    RequestParams params = RequestParams.create(inputBox.getInput().getText());
    params.addHeader(RequestParams.COOKIE, cookieField.getText());
    params.addHeader(RequestParams.USER_AGENT, userAgent.getText());
    WebEngineHttpClient.setCookies(params);
    engine.load(actionClickedEvent.getInput());
  }
}
