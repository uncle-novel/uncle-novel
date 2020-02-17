package com.unclezs.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.gargoylesoftware.htmlunit.WebRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * 代理工具
 * Created by Uncle
 * 2019.08.09.
 */
public class ProxyUtil {
    public static void proxyHttpClient(HttpClientBuilder builder) {
        if (StrUtil.isNotEmpty(ConfUtil.get(ConfUtil.PROXY_PORT)) && StrUtil.isNotEmpty(ConfUtil.get(ConfUtil.PROXY_HOSTNAME))) {
            builder.setProxy(new HttpHost(ConfUtil.get(ConfUtil.PROXY_HOSTNAME), Integer.parseInt(ConfUtil.get(ConfUtil.PROXY_PORT))));
        }
    }
    public static void proxyHttp(HttpRequest request) {
        if (StrUtil.isNotEmpty(ConfUtil.get(ConfUtil.PROXY_PORT)) && StrUtil.isNotEmpty(ConfUtil.get(ConfUtil.PROXY_HOSTNAME))) {
            request.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ConfUtil.get(ConfUtil.PROXY_HOSTNAME), Integer.parseInt(ConfUtil.get(ConfUtil.PROXY_PORT)))));
        }
    }
    public static void proxyConnection() {
        if (ConfUtil.get(ConfUtil.PROXY_PORT) != null && !"".equals(ConfUtil.get(ConfUtil.PROXY_PORT))
                && ConfUtil.get(ConfUtil.PROXY_HOSTNAME) != null && !"".equals(ConfUtil.get(ConfUtil.PROXY_HOSTNAME))) {
            System.setProperty("proxyHost", ConfUtil.get(ConfUtil.PROXY_HOSTNAME));
            System.setProperty("proxyPort", ConfUtil.get(ConfUtil.PROXY_PORT));
        }
    }

    public static void proxyHtmlUnit(WebRequest request) {
        if (ConfUtil.get(ConfUtil.PROXY_PORT) != null && !"".equals(ConfUtil.get(ConfUtil.PROXY_PORT))
                && ConfUtil.get(ConfUtil.PROXY_HOSTNAME) != null && !"".equals(ConfUtil.get(ConfUtil.PROXY_HOSTNAME))) {
            request.setProxyHost(ConfUtil.get(ConfUtil.PROXY_HOSTNAME));
            request.setProxyPort(Integer.parseInt(ConfUtil.get(ConfUtil.PROXY_PORT)));
        }
    }

    //测试代理是否正常
    public static String testProxy(String host, String port) {
        HttpClientBuilder builder = HttpClients.custom();
        if (host != null && !"".equals(host) && port != null && !"".equals(port)) {
            builder.setProxy(new HttpHost(host, Integer.parseInt(port)));
        }
        try (CloseableHttpClient client = builder.build()) {
            HttpEntity entity = client.execute(new HttpGet("http://www.cip.cc/")).getEntity();
            String s = EntityUtils.toString(entity, "UTF-8");
            Document document = Jsoup.parse(s);
            return document.select(".kq-well").first().text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}