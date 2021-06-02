package com.unclezs.novel.app.main.model.config;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.util.ProxyUtils;
import com.unclezs.novel.app.main.manager.SettingManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 网络代理
 *
 * @author blog.unclezs.com
 * @date 2021/4/28 22:32
 */
@Setter
@Getter
@ToString
public class ProxyConfig {

  /**
   * http代理
   */
  private ObjectProperty<Boolean> httpProxy = new SimpleObjectProperty<>(false);
  /**
   * 主机
   */
  private ObjectProperty<String> host = new SimpleObjectProperty<>();
  /**
   * 端口
   */
  private ObjectProperty<String> port = new SimpleObjectProperty<>();
  /**
   * 用户
   */
  private ObjectProperty<String> user = new SimpleObjectProperty<>();
  /**
   * 密码
   */
  private ObjectProperty<String> password = new SimpleObjectProperty<>();

  /**
   * 初始化HTTP代理
   */
  public static void initHttpProxy() {
    ProxyConfig proxyConfig = SettingManager.manager().getProxy();
    if (proxyConfig.getHttpProxy().get()) {
      ProxyUtils.setHttpProxyHost(proxyConfig.getHost().get());
      ProxyUtils.setHttpProxyPort(proxyConfig.getPort().get());
      ProxyUtils.setHttpProxyUser(proxyConfig.getUser().get());
      ProxyUtils.setHttpProxyPassword(proxyConfig.getPassword().get());
    }
    ProxyUtils.setEnabledProxy(proxyConfig.getHttpProxy().get());
  }

  public static boolean isUseProxy() {
    ProxyConfig proxyConfig = SettingManager.manager().getProxy();
    return proxyConfig.getHttpProxy().get() && !StringUtils.isBlank(proxyConfig.getHost().get()) && !StringUtils.isBlank(proxyConfig.getPort().get());
  }
}
