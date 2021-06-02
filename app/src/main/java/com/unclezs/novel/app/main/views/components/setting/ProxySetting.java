package com.unclezs.novel.app.main.views.components.setting;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ProxyUtils;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.config.ProxyConfig;
import java.util.List;
import javafx.beans.property.ObjectProperty;
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
    super(LocalizedSupport.app("setting.proxy"), List.of(createHttpProxy()));
  }

  /**
   * 创建系统代理Item
   *
   * @return 系统代理Item
   */
  private static SettingItem createHttpProxy() {
    ProxyConfig proxyConfig = SettingManager.manager().getProxy();
    JFXCheckBox httpProxyCheckBox = new JFXCheckBox(LocalizedSupport.app("setting.proxy.enabled"));
    ObjectProperty<Boolean> httpProxy = proxyConfig.getHttpProxy();
    httpProxyCheckBox.selectedProperty().bindBidirectional(httpProxy);
    httpProxy.addListener(e -> ProxyUtils.setEnabledProxy(httpProxy.get()));
    // 主机
    TextField host = new TextField();
    host.setPromptText(LocalizedSupport.app("setting.proxy.host"));
    host.textProperty().bindBidirectional(proxyConfig.getHost());
    proxyConfig.getHost().addListener(e -> ProxyUtils.setHttpProxyHost(proxyConfig.getHost().get()));
    // 端口
    TextField port = new TextField();
    port.textProperty().bindBidirectional(proxyConfig.getPort());
    port.setPromptText(LocalizedSupport.app("setting.proxy.port"));
    proxyConfig.getPort().addListener(e -> ProxyUtils.setHttpProxyPort(proxyConfig.getPort().get()));
    // 用户
    TextField user = new TextField();
    user.setPromptText(LocalizedSupport.app("setting.proxy.user"));
    user.textProperty().bindBidirectional(proxyConfig.getUser());
    proxyConfig.getUser().addListener(e -> ProxyUtils.setHttpProxyHost(proxyConfig.getUser().get()));
    // 密码
    TextField password = new TextField();
    password.setPromptText(LocalizedSupport.app("setting.proxy.password"));
    password.textProperty().bindBidirectional(proxyConfig.getPassword());
    proxyConfig.getPassword().addListener(e -> ProxyUtils.setHttpProxyHost(proxyConfig.getPassword().get()));
    // 代理测试
    IconButton debug = NodeHelper.addClass(new IconButton(LocalizedSupport.app("setting.proxy.test"), IconFont.DEBUG), "btn");
    debug.setOnMouseClicked(e -> testHttpProxy());
    IconButton getProxy = NodeHelper.addClass(new IconButton(LocalizedSupport.app("setting.proxy.system"), IconFont.AIRPORT), "btn");
    getProxy.setOnMouseClicked(e -> {
      HttpProxy systemProxy = ProxyUtils.getSystemProxy();
      if (systemProxy != HttpProxy.NO_PROXY) {
        host.setText(systemProxy.getHost());
        port.setText(String.valueOf(systemProxy.getPort()));
        Toast.success(LocalizedSupport.app("setting.proxy.system.success"));
      } else {
        Toast.info(LocalizedSupport.app("setting.proxy.system.error"));
      }
    });
    HBox hostPortBox = new HBox(host, port, getProxy);
    hostPortBox.setSpacing(20);
    HBox userPasswordBox = new HBox(user, password, debug);
    userPasswordBox.setSpacing(20);
    VBox container = new VBox(httpProxyCheckBox, hostPortBox, userPasswordBox);
    container.setSpacing(5);
    return new SettingItem(LocalizedSupport.app("setting.proxy.http"), container);
  }

  /**
   * 测试代理
   */
  private static void testHttpProxy() {
    TaskFactory.create(() -> Jsoup.connect(PROXY_TEST_URL).get().select(PROXY_INFO_SELECTOR).text())
      .onSuccess(content -> ModalBox.none().message(content).show())
      .onFailed(e -> {
        log.error("请求代理失败", e);
        Toast.error(LocalizedSupport.app("setting.proxy.invalid"));
      })
      .start();
  }
}
