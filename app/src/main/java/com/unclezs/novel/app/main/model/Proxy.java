package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.util.ProxyUtils;
import com.unclezs.novel.app.main.manager.SettingManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
public class Proxy {

  /**
   * http代理
   */
  private BooleanProperty httpProxy = new SimpleBooleanProperty(false);
  /**
   * 主机
   */
  private StringProperty host = new SimpleStringProperty();
  /**
   * 端口
   */
  private StringProperty port = new SimpleStringProperty();
  /**
   * 用户
   */
  private StringProperty user = new SimpleStringProperty();
  /**
   * 密码
   */
  private StringProperty password = new SimpleStringProperty();

  /**
   * 初始化HTTP代理
   */
  public static void initHttpProxy() {
    Proxy proxy = SettingManager.manager().getProxy();
    if (proxy.getHttpProxy().get()) {
      ProxyUtils.setHttpProxyHost(proxy.getHost().get());
      ProxyUtils.setHttpProxyPort(proxy.getPort().get());
      ProxyUtils.setHttpProxyUser(proxy.getUser().get());
      ProxyUtils.setHttpProxyPassword(proxy.getPassword().get());
    }
    ProxyUtils.setEnabledProxy(proxy.getHttpProxy().get());
  }

  public static boolean isUseProxy() {
    Proxy proxy = SettingManager.manager().getProxy();
    return proxy.getHttpProxy().get() && !StringUtils.isBlank(proxy.getHost().get()) && !StringUtils.isBlank(proxy.getPort().get());
  }
}
