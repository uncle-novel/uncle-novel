package com.unclezs.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

/**
 *@author unclezs.com
 *@date 2019.06.28 11:05
 */
public class HtmlUnitUtil {
    //获取动态网页
    public static HtmlPage doRequest(String url, String cookiesStr, Map<String, String> headers){
        //屏蔽日志
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
            webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
            setCookies(webClient, cookiesStr);
            setHeaders(webClient, headers);
            webClient.getOptions().setUseInsecureSSL(true);
            WebRequest request=new WebRequest(new URL(url));
            ProxyUtil.proxyHtmlUnit(request);
            final HtmlPage page = webClient.getPage(request);
            webClient.waitForBackgroundJavaScript(8000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
            return page;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("动态网页抓取失败"+url);
        }
        return null;
    }

    public static HtmlPage doRequest(String url) throws IOException {
        return doRequest(url, null, null);
    }

    //设置Cookies
    private static void setCookies(WebClient client, String cookiesStr) {
        if (cookiesStr == null || "".equals(cookiesStr)) {//有cookie则设置Cookies
            return;
        }
        String[] nameValuePair = cookiesStr.split("; ");
        for (String cookie : nameValuePair) {
            client.getCookieManager().addCookie(new Cookie("/", cookie.split("=")[0], cookie.split("=")[1]));
            System.out.println(cookie);
        }
    }

    //设置请求头
    private static void setHeaders(WebClient client, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {//有请求头则设置请求头否则默认请求头
            for (String name : headers.keySet()) {
                client.addRequestHeader(name, headers.get(name));
            }
        } else {
            client.addRequestHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        }
    }
}
