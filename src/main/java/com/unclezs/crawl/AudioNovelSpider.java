package com.unclezs.crawl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unclezs.constrant.Charsets;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.model.rule.SearchAudioRule;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.UrlUtil;
import com.unclezs.utils.XpathUtil;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 有声小说爬虫
 *
 * @author unclezs.com
 * @date 2019.06.22 00:16
 */
@Getter
@Setter
public class AudioNovelSpider implements NovelSpider {


    /**
     * 搜索
     *
     * @param keyword 关键词
     * @param rule    规则
     * @param type    类型
     * @return /
     */
    public List<AudioBook> search(String keyword, SearchAudioRule rule, SearchKeyType type) {
        HttpRequest request = sendSearchRequest(keyword, rule);
        String html = request.execute().body();
        String preUrl = request.getUrl();
        Set<String> urls = new HashSet<>();
        List<AudioBook> list = new ArrayList<>(16);
        while (true) {
            Document doc = Jsoup.parse(html, rule.getSearchUrl());
            Elements elements = Xsoup.compile(rule.getSearchList()).evaluate(doc).getElements();
            for (Element element : elements) {
                String title = XpathUtil.xpath(element, rule.getTitle());
                if (rule.isStrict() && !keyword.equals(title)) {
                    continue;
                }
                //去重
                String url = XpathUtil.xpath(element, rule.getUrl());
                if (urls.contains(url)) {
                    break;
                } else {
                    urls.add(url);
                }
                String author = XpathUtil.xpath(element, rule.getAuthor());
                String cover = XpathUtil.xpath(element, rule.getCover());
                String speak = XpathUtil.xpath(element, rule.getSpeak());
                list.add(new AudioBook(author, speak, title, cover, url));
            }
            //翻页处理
            if (StrUtil.isNotEmpty(rule.getNextPage())) {
                String nextUrl = XpathUtil.xpath(doc, rule.getNextPage());
                if (!nextUrl.startsWith("http") || preUrl.equals(nextUrl)) {
                    break;
                }
                HttpRequest req =
                    RequestUtil.execute(nextUrl, null, "GET", rule.isClient(), null, rule.getCharset(), preUrl);
                html = req.execute().body();
                preUrl = nextUrl;
            } else {
                break;
            }
        }
        switch (type) {
            case TITLE:
                return list.stream().filter(b -> b.getTitle().contains(keyword)).collect(Collectors.toList());
            case AUTHOR:
                return list.stream().filter(b -> b.getAuthor().contains(keyword)).collect(Collectors.toList());
            case SPEAK:
                return list.stream().filter(b -> b.getSpeak().contains(keyword)).collect(Collectors.toList());
            default:
                return list;
        }
    }

    /**
     * 发起搜索请求
     * 处理rest方式请求的
     *
     * @param keyword 关键词
     * @param rule    规则
     * @return html request
     */
    private HttpRequest sendSearchRequest(String keyword, SearchAudioRule rule) {
        if (StrUtil.isNotEmpty(rule.getSearchKey())) {
            return RequestUtil.execute(rule.getSearchUrl(), Dict.create().set(rule.getSearchKey(), keyword),
                rule.getMethod(), rule.isClient(), null, rule.getCharset());
        } else {
            return RequestUtil.execute(
                rule.getSearchUrl() + URLEncoder.createDefault().encode(keyword, Charset.forName(rule.getCharset())),
                null, rule.getMethod(), rule.isClient(), null, rule.getCharset());
        }
    }


    /**
     * 获取章节列表
     *
     * @param url 链接
     * @return 列表
     */
    public List<AudioChapter> getChapters(String url) throws IOException {
        //懒人听书
        if (url.contains("lrts")) {
            return lrtsChapters(url);
        }
        SearchAudioRule rule = DataManager.application.getAudioRules().stream().filter(
            r -> r.getSite().contains(UrlUtil.getSite(url))).findFirst().orElse(null);
        if (rule == null) {
            return new ArrayList<>(0);
        }
        Document document = RequestUtil.doc(url);
        Elements elements = Xsoup.compile(rule.getCatalogList()).evaluate(document).getElements();
        List<AudioChapter> chapters = new ArrayList<>(elements.size());
        for (Element element : elements) {
            String title = XpathUtil.xpath(element, rule.getCatalogName());
            String link = XpathUtil.xpath(element, rule.getCatalogUrl());
            chapters.add(new AudioChapter(link, title));
        }
        return chapters;
    }

    /**
     * 获取懒人听书章节列表
     *
     * @param url ‘/
     * @return /
     * @throws IOException /
     */
    private List<AudioChapter> lrtsChapters(String url) throws IOException {
        List<AudioChapter> chapters = new ArrayList<>(100);
        String bookId = url.substring(url.lastIndexOf("/") + 1);
        Document html = RequestUtil.doc(url);
        Integer chapterNum = Integer.valueOf(
            Xsoup.compile("/html/body/div[1]/div[1]/section[1]/div[2]/ul[3]/li[1]/text()").evaluate(html).get());
        //章节总数
        for (int i = 1; i <= chapterNum; i++) {
            chapters.add(new AudioChapter("http://www.lrts.me/ajax/playlist/2/" + bookId + "/" + i, "" + i));
        }
        return chapters;
    }

    /**
     * 获取有声音频真实地址
     *
     * @param url /
     * @return /
     */
    public String getAudioLink(String url) {
        String methodName = UrlUtil.getSite(url).toLowerCase();
        if (CharUtil.isNumber(methodName.charAt(0))) {
            methodName = "_" + methodName;
        }
        return ReflectUtil.invoke(AudioNovelSpider.this, methodName, url).toString();
    }

    /**
     * http://m.ixinmo.com
     * 心魔听书网 目测和静听网同门
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    private String ixinmo(String url) throws IOException {
        return XpathUtil.xpath(RequestUtil.doc(url), "/html/body/div[2]/audio/source/@src");
    }


    /**
     * http://www.520tingshu.com
     * 520听书网和有声小说吧
     *
     * @param curl /
     * @return /
     * @throws IOException /
     */
    private String _520tingshu(String curl) throws IOException {
        String host = curl.substring(0, curl.indexOf("com") + 4);
        String link = ReUtil.get("\"(/playdata/.+?js.*?)\"", RequestUtil.get(curl), 1);
        if (StrUtil.isEmpty(link)) {
            return "";
        }
        String jsUrl = host + link;
        String jsRes = RequestUtil.get(jsUrl);
        String json = jsRes.substring(jsRes.indexOf(',') + 1, jsRes.lastIndexOf("]]"));
        JSONArray array = JSON.parseArray(json);
        String[] indexStr;
        if (curl.contains("520tingshu")) {
            indexStr = curl.replace(".html", "").split("-");
        } else {
            indexStr = curl.replace(".html", "").split("_");
        }
        int index = Integer.parseInt(indexStr[indexStr.length - 1]);
        return array.get(index).toString().split("[$]")[1];
    }

    /**
     * http://www.ting89.com/
     * 幻听网
     *
     * @param url /
     * @return /
     */
    private String ting89(String url) throws IOException {
        String html = Jsoup.connect(url).referrer(url).userAgent(RequestUtil.USER_AGENT).execute().charset(
            Charsets.GB2312).body();
        return ReUtil.get("data.+?\"(.+?)&", html, 1);
    }

    /**
     * https://www.ysts8.net/
     * 有声听书吧
     *
     * @param curl /
     * @return /
     * @throws IOException /
     */
    private String ysts8(String curl) throws IOException {
        //章节源码爬取
        String url = Xsoup.compile("//*[@id=\"play\"]/@abs:src").evaluate(RequestUtil.doc(curl)).get();
        String html = RequestUtil.execute(url, null, "GET", false, null, Charsets.GB2312).execute().body();
        String query = ReUtil.get("u[0-9]{3,} = '(.+?)'", html, 1);
        String path = ReUtil.get("uu[0-9]{3,} = '(.+?)'", html, 1);
        String host = ReUtil.get("mp3:'(.+?)'", html, 1);
        return host + path + query;
    }

    /**
     * http://www.ting56.com/
     * 56听书网
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    private String ting56(String url) throws IOException {
        String c = ReUtil.get("FonHen_JieMa[(]'([\\s\\S]+?)'", RequestUtil.get(url), 1);
        String res = getCodeString(c);
        String realUrl;
        //初步数据
        String[] datas = res.split("&");
        switch (datas[2]) {
            case "tudou":
                String rJson = RequestUtil.get("http://www.ting56.com//player/getcode.php?id=" + datas[0]);
                String code = JSON.parseObject(rJson).get("code").toString();
                if ("".equals(code)) {
                    realUrl = "http://www.tudou.com/programs/view/html5embed.action?code=" + code
                        + "&autoPlay=true&playType=AUTO";
                } else {
                    realUrl = "http://www.tudou.com/v/" + datas[0];
                }
                break;
            case "tc":
                String[] strings = datas[0].split("/");
                String jmUrl = strings[0] + '/' + strings[1] + "/play_" + strings[1] + "_" + strings[2] + ".htm";
                String resJson =
                    RequestUtil.post("http://www.ting56.com/player/tingchina.php", Dict.create().set("url", jmUrl));
                realUrl = JSON.parseObject(resJson).getString("url").replace("t44", "t33");
                break;
            default:
                realUrl = datas[0].replace(":82", "");
        }
        return realUrl;
    }

    /**
     * 恋听网
     *
     * @param url /
     * @return /
     */
    private String ting55(String url) throws IOException {
        String xt = ReUtil.get("<meta name=\"_c\" content=\"(.+?)\"", RequestUtil.get(url), 1);
        if (StrUtil.isNotEmpty(xt)) {
            String[] info = url.substring(url.lastIndexOf("/") + 1).split("-");
            Map<String, String> headers = new HashMap<>();
            headers.put("xt", xt);
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Dict form = Dict.create().set("bookId", info[0])
                .set("isPay", 0)
                .set("page", info[1]);
            String body = RequestUtil.execute("https://ting55.com/glink", form, "POST", false, headers, "utf-8",
                url).execute().body();
            JSONObject json = JSON.parseObject(body);
            String res = json.getString("url");
            if (StrUtil.isEmpty(res)) {
                res = json.getString("ourl");
            }
            return res;
        }
        return "";
    }

    /**
     * 静听网
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    private String audio699(String url) throws IOException {
        Document doc = RequestUtil.doc(url);
        return doc.select("source").attr("abs:src");
    }

    /**
     * 听中国的声音
     *
     * @param url /
     * @return /
     */
    private String tingchina(String url) throws IOException {
        String host = "http://www.tingchina.com";
        String flashUrl = ReUtil.get("playurl_flash=\"(.+?)\"", RequestUtil.get(url), 1);
        if (StrUtil.isNotEmpty(flashUrl)) {
            String html =
                RequestUtil.execute(host + flashUrl, null, "GET", false, null, Charsets.GB2312, url).execute().body();
            String query = ReUtil.get("url.3.= \"(.+?)\"", html, 1);
            if (StrUtil.isNotEmpty(query)) {
                return "http://t44.tingchina.com" + query;
            }
        }
        return "";
    }

    /**
     * https://www.ysts8.net
     * <p>
     * 懒人听书
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    public String lrts(String url) throws IOException {
        return Xsoup.compile("//div[@class=\"section\"]/li[1]/div[1]/input[1]/@value").evaluate(
            RequestUtil.doc(url)).get();
    }

    /**
     * 22听书网
     *
     * @param url /
     * @return /
     * @throws IOException /
     */
    private String ting22(String url) throws IOException {
        String[] query = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")).split("\\-");
        int chapterNumber = Integer.parseInt(query[1]);
        int page = (int) Math.ceil(chapterNumber * 1.0f / 10);
        int pageIndex = (chapterNumber - 1) % 10;
        //音频地址API
        long cur = System.currentTimeMillis();
        String api =
            String.format("https://www.ting22.com/api.php?c=Json&id=%s&page=%s&pagesize=10&callback=jQuery&_=%s",
                query[0], page, cur);
        Map<String, String> headers = new HashMap<>(2);
        headers.put("sign", cur + "");
        headers.put("Referer", url);
        //获取URL加密Code
        String rjson = RequestUtil.get(api, null, null, headers).replace("jQuery(", "").replace(");", "");
        String code = JSON.parseObject(rjson).getJSONArray("playlist").getJSONObject(pageIndex).getString("file");
        //解密
        code = getCodeString(code);
        //根据类型获取真实音频地址
        String realUrl = "";
        String[] tempUrl = code.split("\\$");
        //不是默认真实音频地址的，需要另外解析
        if (tempUrl.length == 2) {
            switch (tempUrl[1]) {
                //yousheng/29545/0$tc
                case "tc":
                    String[] data = tempUrl[0].split("/");
                    String tcApi = data[0] + "/" + data[1] + "/play_" + data[1] + "_" + data[2] + ".htm";
                    realUrl = JSON.parseObject(
                        RequestUtil.get("https://c.ting22.com/tingchina.php?file=" + tcApi, url)).getString("url");
                    break;
                // 19576598/139592587$xm
                case "xm":
                    realUrl = "http://mobile.ximalaya.com/mobile/redirect/free/play/" + tempUrl[0].split("/")[1] + "/0";
                    break;
                default:
                    break;
            }
        } else {
            realUrl = tempUrl[0];
        }
        return realUrl;
    }

    /**
     * 将45*48*78类的转字符
     *
     * @param src /
     * @return /
     */
    private String getCodeString(String src) {
        String[] u = src.split("\\*");
        StringBuilder res = new StringBuilder();
        for (String s : u) {
            if (!"".equals(s)) {
                res.append((char) Integer.parseInt(s));
            }
        }
        return res.toString();
    }
}
