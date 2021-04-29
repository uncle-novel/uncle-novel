package com.unclezs.novel.app.main.ui.home.views.widgets.setting;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ProxyUtils;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.Proxy;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 22:21
 */
@Slf4j
public class ProxySetting extends SettingItems {

  public static final String PROXY_TEST_URL = "http://www.cip.cc/";
  public static final String PROXY_INFO_SELECTOR = ".kq-well pre";

  public ProxySetting() {
    super("网络设置", List.of(createHttpProxy()));
  }

  /**
   * 创建系统代理Item
   *
   * @return 系统代理Item
   */
  private static SettingItem createHttpProxy() {
    Proxy proxy = SettingManager.manager().getProxy();
    JFXCheckBox httpProxyCheckBox = new JFXCheckBox("是否启用");
    BooleanProperty httpProxy = proxy.getHttpProxy();
    httpProxyCheckBox.selectedProperty().bindBidirectional(httpProxy);
    httpProxy.addListener(e -> ProxyUtils.setEnabledProxy(httpProxy.get()));
    // 主机
    TextField host = new TextField();
    host.setPromptText("主机");
    host.textProperty().bindBidirectional(proxy.getHost());
    proxy.getHost().addListener(e -> ProxyUtils.setHttpProxyHost(proxy.getHost().get()));
    // 端口
    TextField port = new TextField();
    port.textProperty().bindBidirectional(proxy.getPort());
    port.setPromptText("端口");
    proxy.getPort().addListener(e -> ProxyUtils.setHttpProxyPort(proxy.getPort().get()));
    // 用户
    TextField user = new TextField();
    user.setPromptText("用户");
    user.textProperty().bindBidirectional(proxy.getUser());
    proxy.getUser().addListener(e -> ProxyUtils.setHttpProxyHost(proxy.getUser().get()));
    // 密码
    TextField password = new TextField();
    password.setPromptText("用户");
    password.textProperty().bindBidirectional(proxy.getPassword());
    proxy.getPassword().addListener(e -> ProxyUtils.setHttpProxyHost(proxy.getPassword().get()));
    // 代理测试
    IconButton debug = NodeHelper.addClass(new IconButton("测试代理生效", IconFont.DEBUG), "btn");
    debug.setOnMouseClicked(e -> testHttpProxy());
    IconButton getProxy = NodeHelper.addClass(new IconButton("获取系统代理", IconFont.AIRPORT), "btn");
    getProxy.setOnMouseClicked(e -> {
      HttpProxy systemProxy = ProxyUtils.getSystemProxy();
      if (systemProxy != HttpProxy.NO_PROXY) {
        host.setText(systemProxy.getHost());
        port.setText(String.valueOf(systemProxy.getPort()));
        Toast.success("获取系统代理成功");
      } else {
        Toast.info("未发现系统代理");
      }
    });
    HBox hostPortBox = new HBox(host, port, getProxy);
    hostPortBox.setSpacing(20);
    HBox userPasswordBox = new HBox(user, password, debug);
    userPasswordBox.setSpacing(20);
    VBox container = new VBox(httpProxyCheckBox, hostPortBox, userPasswordBox);
    container.setSpacing(5);
    return new SettingItem("HTTP代理", container);
  }

  /**
   * 测试代理
   */
  private static void testHttpProxy() {
    TaskFactory.create(() -> Jsoup.connect(PROXY_TEST_URL).get().select(PROXY_INFO_SELECTOR).text())
      .onSuccess(content -> ModalBox.none().message(content).show())
      .onFailed(e -> {
        log.error("请求代理失败", e);
        Toast.error("代理无效");
      })
      .start();
  }
}
