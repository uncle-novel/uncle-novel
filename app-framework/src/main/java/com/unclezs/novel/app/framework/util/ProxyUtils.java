package com.unclezs.novel.app.framework.util;

import com.unclezs.novel.analyzer.request.proxy.HttpProxy;
import com.unclezs.novel.analyzer.util.StringUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * 系统属性工具类
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 10:22
 */
@UtilityClass
public class ProxyUtils {

  public static final String SYSTEM_PROXIES = "java.net.useSystemProxies";
  public static final String HTTP_PROXY_HOST = "http.proxyHost";
  public static final String HTTP_PROXY_PORT = "http.proxyPort";
  public static final String HTTP_PROXY_USER = "http.proxyUser";
  public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
  public static final String GOOGLE = "https://www.google.com";
  private static final ProxySelector DEFAULT_SELECTOR;
  private static final HttpProxySelector SYSTEM_PROXY_SELECTOR;
  private static List<Proxy> proxies = List.of(Proxy.NO_PROXY);

  static {
    System.setProperty(SYSTEM_PROXIES, "true");
    DEFAULT_SELECTOR = ProxySelector.getDefault();
    SYSTEM_PROXY_SELECTOR = new HttpProxySelector();
    ProxySelector.setDefault(SYSTEM_PROXY_SELECTOR);
  }

  public static void setEnabledProxy(boolean enabled) {
    if (enabled) {
      proxies = List.of(getHttpProxy());
    } else {
      proxies = List.of(Proxy.NO_PROXY);
    }
    SYSTEM_PROXY_SELECTOR.setEnabledSystemProxy(enabled);
  }

  /**
   * 设置代理Host
   *
   * @param host 主机地址
   */
  public static void setHttpProxyHost(String host) {
    if (StringUtils.isBlank(host)) {
      System.clearProperty(HTTP_PROXY_HOST);
    } else {
      System.setProperty(HTTP_PROXY_HOST, host);
    }
  }

  /**
   * 设置代理端口
   *
   * @param port 端口
   */
  public static void setHttpProxyPort(String port) {
    if (StringUtils.isBlank(port)) {
      System.clearProperty(HTTP_PROXY_PORT);
    } else {
      System.setProperty(HTTP_PROXY_PORT, port);
    }
  }

  /**
   * 设置代理认证用户
   *
   * @param user 用户
   */
  public static void setHttpProxyUser(String user) {
    if (StringUtils.isBlank(user)) {
      System.clearProperty(HTTP_PROXY_USER);
    } else {
      System.setProperty(HTTP_PROXY_USER, user);
    }
  }

  /**
   * 设置代理认证密码
   *
   * @param password 密码
   */
  public static void setHttpProxyPassword(String password) {
    if (StringUtils.isBlank(password)) {
      System.clearProperty(HTTP_PROXY_PASSWORD);
    } else {
      System.setProperty(HTTP_PROXY_PASSWORD, password);
    }
  }

  /**
   * 获取HTTP代理
   *
   * @return http代理
   */
  private static Proxy getHttpProxy() {
    String host = System.getProperty(HTTP_PROXY_HOST);
    String port = System.getProperty(HTTP_PROXY_PORT);
    if (StringUtils.isBlank(host) || StringUtils.isBlank(port)) {
      return Proxy.NO_PROXY;
    }
    return new Proxy(Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));
  }

  /**
   * 获取系统HTTP代理
   *
   * @return 系统代理
   */
  public static HttpProxy getSystemProxy() {
    Proxy proxy = DEFAULT_SELECTOR.select(URI.create(GOOGLE)).get(0);
    if (proxy != Proxy.NO_PROXY) {
      String[] s = proxy.address().toString().split(":");
      return new HttpProxy(s[0], Integer.parseInt(s[1]));
    }
    return HttpProxy.NO_PROXY;
  }

  /**
   * 系统代理选择器
   */
  static class HttpProxySelector extends ProxySelector {

    private static final List<Proxy> NO_PROXY_LIST = List.of(Proxy.NO_PROXY);
    @Setter
    private boolean enabledSystemProxy = false;

    @Override
    public List<Proxy> select(URI uri) {
      if (enabledSystemProxy) {
        return proxies;
      }
      return NO_PROXY_LIST;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
      DEFAULT_SELECTOR.connectFailed(uri, sa, ioe);
    }
  }
}
