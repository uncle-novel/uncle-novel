package com.unclezs.utils;


import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.unclezs.config.SpiderConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;


/**
 * http请求工具
 *
 * @author uncle
 * @date 2020/2/25 16:36
 */
@Slf4j
public class RequestUtil {
    public static final String USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
    public static final String USER_AGENT_CLIENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36";

    static {
        initSSL();
    }

    /**
     * 忽略SSL验证错误
     */
    public static void initSSL() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new X509TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                    X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception ignore) {
        }
    }

    public static String get(String url) throws IOException {
        return get(url, null, USER_AGENT, null);
    }

    public static String get(String url, String referer) throws IOException {
        Map<String, String> header = new HashMap<>(1);
        header.put("Referer", referer);
        return get(url, null, USER_AGENT, header);
    }

    /**
     * 检查网址是否能够访问
     *
     * @param url 测试的网址
     * @return /
     */
    public static boolean check(String url) {
        return HttpUtil.createGet(url).setFollowRedirects(true).header("Referer", url).timeout(10000).execute().isOk();
    }

    public static String get(String url, String cookies, String ua, Map<String, String> header) throws IOException {
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        urlBuilder.getPath().setWithEndTag(url.endsWith(StrUtil.SLASH));
        return retryRun(link -> HttpUtil.createGet(link)
            .setUrl(urlBuilder)
            .header("Referer", link)
            .header("user-agent", StrUtil.emptyToDefault(ua, USER_AGENT))
            .addHeaders(header)
            .cookie(cookies)
            .setFollowRedirects(true)
            .timeout(3000)
            .execute().body(), url);
    }

    public static Document doc(String url) throws IOException {
        return Jsoup.parse(get(url), url);
    }

    public static String post(String url, Map<String, Object> form) {
        return HttpUtil.createPost(url).form(form).execute().body();
    }

    public static String post(String url, Map<String, Object> form, Map<String, String> headers) {
        return execute(url, form, "POST", false, headers).execute().body();
    }

    public static HttpRequest execute(String url, Map<String, Object> form, String method, boolean client,
        Map<String, String> headers) {
        return HttpUtil.createRequest(Method.valueOf(method), url)
            .header("Referer", url)
            .header("User-Agent", client ? USER_AGENT_CLIENT : USER_AGENT)
            .addHeaders(headers)
            .setFollowRedirects(true)
            .form(form);
    }

    public static HttpRequest execute(String url, Map<String, Object> form, String method, boolean client,
        Map<String, String> headers, String charset) {
        return execute(url, form, method, client, headers, charset, url);
    }

    public static HttpRequest execute(String url, Map<String, Object> form, String method, boolean client,
        Map<String, String> headers, String charset, String referer) {
        return HttpUtil.createRequest(Method.valueOf(method), url)
            .charset(charset)
            .setUrl(url)
            .header("Referer", referer)
            .header("User-Agent", client ? USER_AGENT_CLIENT : USER_AGENT)
            .addHeaders(headers)
            .setFollowRedirects(true)
            .form(form);
    }

    /**
     * 下载文件
     *
     * @param url /
     */
    public static void download(String url, String targetFile, boolean clientUa) throws IOException {
        if (StrUtil.isBlank(url)) {
            throw new NullPointerException("[url] is null!");
        }
        if (null == targetFile) {
            throw new NullPointerException("[targetFile] is null!");
        }
        retryRun(link -> {
            final HttpResponse response;
            response = HttpUtil.createGet(link).setFollowRedirects(true).header("User-Agent",
                clientUa ? USER_AGENT_CLIENT : USER_AGENT).timeout(-1).execute();
            if (response.isOk()) {
                response.writeBody(targetFile);
            }
            return "success";
        }, url);
    }

    /**
     * 重试
     *
     * @param function /
     * @param url      /
     * @return /
     * @throws IOException /
     */
    private static String retryRun(Function<String, String> function, String url) throws IOException {
        String res = null;
        for (int i = 0; i < SpiderConfig.RETRY_COUNT; i++) {
            try {
                res = function.apply(url);
                break;
            } catch (Exception e) {
                log.trace("[{}]--失败重试第{}/{}次：[{}]  原因:{}", Thread.currentThread().getName(), i + 1,
                    SpiderConfig.RETRY_COUNT, url, e.getMessage());
                ThreadUtil.safeSleep(SpiderConfig.RETRY_DELAY);
            }
        }
        if (res == null) {
            throw new IOException(String.format("连接超时:URl【%s】", url));
        }
        return res;
    }

    /**
     * 获取响应流
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    public static InputStream stream(String url) throws IOException {
        try {
            return HttpUtil.createGet(url).setFollowRedirects(true).header("Referer", url).timeout(5000).setReadTimeout(
                5000).execute().bodyStream();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
