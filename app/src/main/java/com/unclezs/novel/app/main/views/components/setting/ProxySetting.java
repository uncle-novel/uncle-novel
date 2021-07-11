package com.unclezs.novel.app.main.views.components.setting;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.StringUtils;
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
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 22:21
 */
@Slf4j
public class ProxySetting extends SettingItems {

  public static final String PROXY_TEST_URL = "https://ip.900cha.com";
  public static final String PROXY_INFO_SELECTOR = ".list-unstyled";

  public ProxySetting() {
    super(LocalizedSupport.app("setting.proxy"), List.of(createSystemProxy(), createHttpProxy()));
  }

  /**
   * 创建系统代理Item
   *
   * @return 系统代理Item
   */
  private static SettingItem createSystemProxy() {
    ProxyConfig proxyConfig = SettingManager.manager().getProxy();
    // 系统代理
    JFXCheckBox useSystemCheckBox = new JFXCheckBox("启用系统代理");
    useSystemCheckBox.selectedProperty().bindBidirectional(proxyConfig.getUseSystem());
    useSystemCheckBox.selectedProperty().addListener(e -> ProxyUtils.setEnabledSystemProxy(useSystemCheckBox.isSelected()));
    ProxyUtils.setEnabledSystemProxy(useSystemCheckBox.isSelected());
    return new SettingItem("系统代理", useSystemCheckBox);
  }

  /**
   * 创建系统代理Item
   *
   * @return 系统代理Item
   */
  private static SettingItem createHttpProxy() {
    ProxyConfig proxyConfig = SettingManager.manager().getProxy();
    // 启用自定义代理
    JFXCheckBox httpProxyCheckBox = new JFXCheckBox(LocalizedSupport.app("setting.proxy.enabled"));
    ObjectProperty<Boolean> httpProxy = proxyConfig.getHttpProxy();
    httpProxyCheckBox.selectedProperty().bindBidirectional(httpProxy);
    httpProxy.addListener(e -> ProxyConfig.initHttpProxy());
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

    HBox optionsBox = new HBox(httpProxyCheckBox);
    optionsBox.setSpacing(10);
    HBox hostPortBox = new HBox(host, port, debug);
    hostPortBox.setSpacing(20);
    HBox userPasswordBox = new HBox(user, password);
    userPasswordBox.setSpacing(20);
    VBox container = new VBox(optionsBox, hostPortBox, userPasswordBox);
    container.setSpacing(5);
    return new SettingItem(LocalizedSupport.app("setting.proxy.http"), container);
  }

  /**
   * 测试代理
   */
  private static void testHttpProxy() {
    TaskFactory.create(() -> Jsoup.connect(PROXY_TEST_URL).followRedirects(true).get().select(PROXY_INFO_SELECTOR).first().select("li").eachText())
      .onSuccess(content -> {
        StringBuilder message = new StringBuilder();
        content.forEach(str -> message.append(str).append(StringUtils.LF));
        ModalBox.none().message(message.toString()).show();
      })
      .onFailed(e -> {
        log.error("请求代理失败", e);
        Toast.error(LocalizedSupport.app("setting.proxy.invalid"));
      })
      .start();
  }
}
