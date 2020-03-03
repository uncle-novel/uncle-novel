package com.unclezs.crawl;


import cn.hutool.core.util.StrUtil;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Chapter;
import com.unclezs.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自动识别文章编码，支持乱序重排，过滤重复，过滤多余非章节的url
 *
 * @author Uncle
 */
public class NovelSpider {
    /**
     * 中文标点符号
     */
    private static final String CH_PUNCTUATION = "~\\u000A\\u0009\\u00A0\\u0020\\u3000\\uFEFF";
    /**
     * unicode符号
     */
    private static final String UNICODE_AZAZ09 = "\\uFF41-\\uFF5a\\uFF21-\\uFF3a\\uFF10-\\uFF19";
    /**
     * 中文
     */
    private static final String CHINESE = "\\u4E00-\\u9FFF";
    /**
     * 标题正则
     */
    private static Pattern TITLE_PATTERN = Pattern.compile("(.{1,10}?)最新");
    /**
     * 正文规则1
     */
    private static Pattern CONTENT_RULE_1 = Pattern.compile("[pvri\\-/\"]>([^字<*][\\pP\\w\\pN\\pL\\pM" + UNICODE_AZAZ09 + CHINESE + CH_PUNCTUATION + "]{3,}[^字\\w>]{0,2})(<br|</p|</d|<p|<!|<d|</li)", Pattern.CASE_INSENSITIVE);
    /**
     * 正文规则2
     */
    private static Pattern CONTENT_RULE_2 = Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);
    /**
     * 配置信息
     */
    private AnalysisConfig conf;
    private String defCharset = "utf-8";
    private String novelTitle = "Uncle小说";

    public NovelSpider(AnalysisConfig conf) {
        this.conf = conf;
    }

    /**
     * 获取小说名字
     *
     * @param html 章节列表源码
     * @return 小说名字（没有匹配到就返回uncle小说）
     */
    public String getTitle(String html) {
        String title;
        Document document = Jsoup.parse(html);
        Element element = document.select("title").first();
        String text = element.text().trim();
        Matcher m = TITLE_PATTERN.matcher(text);
        if (m.find()) {
            title = m.group(1);
        } else {
            title = text.substring(0, text.length() > 10 ? 10 : text.length());
        }
        return title.replaceAll("[\\pP]", "").trim();
    }

    /**
     * 获取章节列表
     *
     * @param url 章节目录地址
     * @return 章节列表
     */
    public List<Chapter> getChapterList(String url) {
        //存url
        List<String> urls = new ArrayList<>();
        //存标题
        Map<String, String> title = new HashMap<>();
        List<Chapter> chapters = new ArrayList<>(500);
        //默认编码
        String charset = "gbk";
        try {
            //抓取源码自动识别网页编码
            String html = getHtml(url, charset);
            charset = HtmlUtil.getEncode(html);
            //g2312采用gbk编码更全
            if (charset.toLowerCase().contains("gb")) {
                charset = "gbk";
            } else {//utf-8重新抓取
                html = getHtml(url, charset);
            }
            //根据用户输入章节头尾删减html
            html = getDelHtml(conf.getChapterHead(), conf.getChapterTail(), html);
            //解析章节列表的所有url
            Document document = Jsoup.parse(html);
            document.setBaseUri(url);
            Elements aTags = document.select("a");
            for (Element e : aTags) {
                String href = e.absUrl("href");
                //剔除空标签
                if (StrUtil.isNotBlank(href) && StrUtil.isNotBlank(e.text())&& href.startsWith("http")) {
                    urls.add(href);
                    title.put(href, e.text().trim());
                }
            }
            //过滤出不需要的
            if (conf.isChapterFilter()) {
                urls = UrlFilter.getChapterFilterRes(urls);
            }
            //乱序重排
            if (conf.isChapterSort()) {
                Collections.sort(urls, new SortUrl());
            }
            //预处理完后放入map集合
            for (String s : urls) {
                chapters.add(new Chapter(title.get(s), s));
            }
            //更新题目与小说名字
            novelTitle = getTitle(html);
            defCharset = charset;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * 爬取小说正文
     *
     * @param url 正文url
     * @return 正文内容
     */
    public String getContent(String url, String charset) {
        StringBuffer content = new StringBuffer();
        String html;
        //抓取网页源码
        html = getHtml(url, charset);
        if (html == null) {
            return "";
        }
        //自定义范围
        html = getDelHtml(conf.getContentHead(), conf.getContentTail(), html);
        //两种规则爬取
        switch (conf.getRule()) {
            case "1":
                Matcher m = CONTENT_RULE_1.matcher(html);
                while (m.find()) {
                    String c;
                    if (((c = m.group(1).replaceAll("&[#\\w]{3,6}[;:]?", " ")).length()) > 0) {
                        content.append(c + "\r\n");
                    }
                }
                break;
            case "2":
                Matcher m2 = CONTENT_RULE_2.matcher(html);
                while (m2.find()) {
                    boolean pass = (Pattern.matches("[\\s\\S]*?[^字\\w<*][" + CHINESE + "]+[\\s\\S]*?", m2.group(2))
                            || Pattern.matches("[\\s\\S]*?&#\\d{4,5}[\\s\\S]*?", m2.group(2)))
                            && (m2.group(1).endsWith("br />")
                            || m2.group(1).endsWith("br/>")
                            || m2.group(1).endsWith("br>")
                            || m2.group(1).endsWith("abc\">")
                            || m2.group(1).endsWith("p>")
                            || m2.group(1).endsWith("v>")
                            || m2.group(1).endsWith("->"))
                            && m2.group(2).replaceAll("&[#\\w]{3,6}[;:]?", " ").trim().length() > 0;
                    if (pass) {
                        content.append(m2.group(2).replaceAll("&[#\\w]{3,6}[;:]?", " ") + "\r\n");
                    }
                }
                break;
            default:
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("p", "br", "div");
                String parse = Jsoup.clean(html, whitelist);
                parse = parse.replaceAll("&[#\\w]{3,6}[;:]?", "{空格}");
                parse = parse.replaceAll("(<br/>|<br>|<br />)", "{换行}");
                parse = parse.replaceAll("(\n|\r\n|<p>)", "{换行}");
                parse = parse.replace("　", "");
                Document document = Jsoup.parse(parse);
                Elements divs = document.select("div");
                String text = "";
                int maxLen = 0;
                for (Element div : divs) {
                    String ownText = div.ownText();
                    if (ownText.length() > maxLen) {
                        text = ownText;
                        maxLen = ownText.length();
                    }
                }
                content.append(text.replace("{换行}{换行}", "\r\n").replace("{换行}", "\r\n").replace("{空格}", " "));
                break;
        }
        //缩进处理
        String[] strings = content.toString().split("[\r]?\n");
        content = new StringBuffer();
        for (String s : strings) {
            if (s.trim().length() > 0){
                content.append("    ").append(s.trim()).append("\r\n\r\n");
            }
        }
        //转码
        String text = content.toString();
        //ncr转中文
        if (conf.isNcrToZh()) {
            text = CharacterUtil.NCR2Chinese(text);
        }
        //繁体转简体
        if (conf.isTraToSimple()) {
            text = CharacterUtil.traditional2Simple(text);
        }
        //去广告
        for (String ad : conf.getAdStr().split("\n")) {
            text = text.replaceAll(ad, "");
        }
        return text;
    }

    /**
     * 获取章节标题和编码
     *
     * @return
     */
    public Map<String, String> getConfig() {
        Map<String, String> conf = new HashMap<>(2);
        conf.put("charset", defCharset);
        conf.put("title", novelTitle);
        return conf;
    }

    /**
     * 获取添加范围后的html
     *
     * @param header 范围头
     * @param tail   范围尾部
     * @param src    源
     * @return 删减后的html
     */
    private String getDelHtml(String header, String tail, String src) {
        int end = tail != null && tail.length() > 1 ? src.indexOf(tail) : src.length();
        int st = header != null && header.length() > 1 ? src.indexOf(header) : 0;
        if (st == -1) {
            st = 0;
        }
        if (end == -1) {
            end = src.length();
        }
        if (st != 0) {
            st -= 5;
        }
        if (end != src.length()) {
            end += 5;
        }
        return src.substring(st, end);
    }

    /**
     * 抓小说图片
     *
     * @param name
     * @return
     */
    public String crawlDescImage(String name) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
            String html = HtmlUtil.getHtml("https://www.qidian.com/search?kw=" + name, "UTF-8");
            if(StrUtil.isNotEmpty(html)){
                Document document = Jsoup.parse(html);
                document.setBaseUri("http:");
                Element element = document.select(".res-book-item").first();
                return element.select("img").first().absUrl("src");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setConf(AnalysisConfig conf) {
        this.conf = conf;
    }

    public AnalysisConfig getConf() {
        return conf;
    }

    /**
     * 获取网页源码
     *
     * @param url
     * @param charset
     * @return
     */
    private String getHtml(String url, String charset) {
        //动态网页
        if (conf.isStartDynamic()) {
            return HtmlUnitUtil.doRequest(url, conf.getCookies(), null).asXml();
        } else {
            //静态网页,根据cookies和浏览器标识设置
            return HtmlUtil.getHtml(url, charset, conf.getCookies(), conf.getUserAgent());
        }
    }

    public String getDefCharset() {
        return defCharset;
    }

    public void setDefCharset(String defCharset) {
        this.defCharset = defCharset;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }
}