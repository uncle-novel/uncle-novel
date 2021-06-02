package com.unclezs.novel.app.main.core.spi;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.executor.Executor;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

/**
 * 采用openjfx的webEngine抓取动态网页，只支持单线程，多线程开销太大，性能太低
 *
 * @author blog.unclezs.com
 * @date 2021/4/26 11:58
 */
public class WebEngineHttpClient implements HttpProvider {

  public static final int MAX_WAIT_TIME = 30;
  public static final String GET_DOM_JS_SCRIPT = "document.documentElement.outerHTML";
  public static final String SCROLL_WINDOW_JS_SCRIPT = "window.scrollTo({ top:200})";

  /**
   * 设置请求头
   *
   * @param params 参数
   */
  public static void setCookies(RequestParams params) {
    // 设置cookie
    String cookies = params.getHeader(RequestParams.COOKIE);
    if (StringUtils.isNotBlank(cookies)) {
      try {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(cookies)) {
          headers.put("Set-Cookie", Arrays.stream(cookies.split(";")).collect(Collectors.toList()));
        } else {
          headers.put("Set-Cookie", Collections.emptyList());
        }
        CookieHandler.getDefault().put(URI.create(UrlUtils.getSite(params.getUrl())), headers);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 获取网页
   *
   * @param params 请求参数
   * @return 网页内容
   * @throws IOException 请求错误
   */
  @Override
  public String content(RequestParams params) throws IOException {
    if (!UrlUtils.isHttpUrl(params.getUrl())) {
      return StringUtils.EMPTY;
    }
    AtomicReference<String> content = new AtomicReference<>();
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 开始加载
    Executor.runFx(() -> {
      WebEngine engine = new WebEngine();
      setCookies(params);
      engine.getLoadWorker().stateProperty().addListener(new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends State> observable, State old, State newState) {
          if (newState == State.SUCCEEDED) {
            // 滚动窗口，防止有些网页需要滚动后才加载
            engine.executeScript(SCROLL_WINDOW_JS_SCRIPT);
            Executor.runFx(() -> {
              String result = engine.executeScript(GET_DOM_JS_SCRIPT).toString();
              content.set(result);
              countDownLatch.countDown();
            }, 500);
            engine.getLoadWorker().stateProperty().removeListener(this);
          } else if (newState == State.CANCELLED || newState == State.FAILED) {
            countDownLatch.countDown();
          }
        }
      });
      engine.load(params.getUrl());
    });
    // 等待完成
    boolean success;
    try {
      success = countDownLatch.await(MAX_WAIT_TIME, TimeUnit.SECONDS);
      if (success) {
        return content.get();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return StringUtils.EMPTY;
  }

  @Override
  public byte[] bytes(RequestParams requestParams) {
    return new byte[0];
  }

  @Override
  public boolean isDynamic() {
    return true;
  }
}
