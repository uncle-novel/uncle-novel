package com.unclezs.crawl;

import static com.unclezs.constrant.RuleConstant.LINK_ATTR;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.annotation.JSONField;
import com.unclezs.constrant.Charsets;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Chapter;
import com.unclezs.model.NovelInfo;
import com.unclezs.model.rule.SearchTextRule;
import com.unclezs.utils.CharacterUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.TextUtil;
import com.unclezs.utils.UrlUtil;
import com.unclezs.utils.XpathUtil;
import com.unclezs.utils.comparator.ChapterComparator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本小说爬虫
 *
 * @author uncle
 * @date 2020/4/25 18:54
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TextNovelSpider implements Serializable, NovelSpider {
    /**
     * 特殊网站
     */
    public static final List<String> specialSite = ListUtil.list(false,
        "po18"
    );
    private static final long serialVersionUID = 1L;
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
     * 正文规则1
     */
    private static Pattern CONTENT_RULE_1 = Pattern.compile(
        "[pvri\\-/\"]>([^字<*][\\pP\\w\\pN\\pL\\pM" + UNICODE_AZAZ09 + CHINESE + CH_PUNCTUATION
            + "]{3,}[^字\\w>]{0,2})(<br|</p|</d|<p|<!|<d|</li)", Pattern.CASE_INSENSITIVE);
    /**
     * 正文规则2
     */
    private static Pattern CONTENT_RULE_2 =
        Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);

    private AnalysisConfig config;
    @JSONField(serialize = false)
    private String title = "公众号[书虫无书荒]";

    public TextNovelSpider(AnalysisConfig config) {
        this.config = config;
    }

    /**
     * 获取封面
     *
     * @param title 小说名称
     * @return cover url
     * @throws Exception /
     */
    public static String getCover(String title) throws Exception {
        Document doc = RequestUtil.doc("https://www.qidian.com/search?kw=" + URLEncoder.encode(title, Charsets.UTF8));
        String cover = XpathUtil.xpath(doc, "//*[@id=\"result-list\"]/div/ul/li[1]/div[1]/a/img/@abs:src");
        if (UrlUtil.isHttpUrl(cover)) {
            return cover;
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 搜索
     *
     * @param keyword 关键词
     * @param rule    规则
     * @param type    类型
     * @return /
     */
    public List<NovelInfo> search(String keyword, SearchTextRule rule, SearchKeyType type) {
        HttpRequest request = sendSearchRequest(keyword, rule);
        String html = request.execute().body();
        String preUrl = request.getUrl();
        Set<String> urls = new HashSet<>();
        List<NovelInfo> list = new ArrayList<>(16);
        while (true) {
            Document doc = Jsoup.parse(html, rule.getSearchLink());
            Elements elements = Xsoup.compile(rule.getResultList()).evaluate(doc).getElements();
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
                list.add(new NovelInfo(url, author, title, cover));
            }
            //翻页处理
            if (StrUtil.isNotEmpty(rule.getNextPage())) {
                String nextUrl = XpathUtil.xpath(doc, rule.getNextPage());
                if (!nextUrl.startsWith("http") || preUrl.equals(nextUrl)) {
                    break;
                }
                HttpRequest req = RequestUtil.execute(nextUrl, null, "GET", false, null, rule.getCharset(), preUrl);
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
    private HttpRequest sendSearchRequest(String keyword, SearchTextRule rule) {
        if (StrUtil.isNotEmpty(rule.getSearchKey())) {
            return RequestUtil.execute(rule.getSearchLink(), Dict.create().set(rule.getSearchKey(), keyword),
                rule.getMethod(), false, null, rule.getCharset());
        } else {
            return RequestUtil.execute(
                rule.getSearchLink() + cn.hutool.core.net.URLEncoder.createDefault().encode(keyword,
                    Charset.forName(rule.getCharset())), null, rule.getMethod(), false, null, rule.getCharset());
        }
    }

    public String contentByHtml(String html) {
        StringBuffer content = new StringBuffer();
        //自定义范围
        if (config.getRule().get() == 1 || config.getRule().get() == 2) {
            html = TextUtil.getDelHtml(config.getContentHead().get(), config.getContentTail().get(), html);
        }
        //三种规则爬取
        switch (config.getRule().get()) {
            case 1:
                Matcher m = CONTENT_RULE_1.matcher(html);
                while (m.find()) {
                    String c;
                    if (((c = m.group(1).replaceAll("&[#\\w]{3,6}[;:]?", " ")).length()) > 0) {
                        content.append(c).append("\r\n");
                    }
                }
                break;
            case 2:
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
                        content.append(m2.group(2).replaceAll("&[#\\w]{3,6}[;:]?", " ")).append("\r\n");
                    }
                }
                break;
            default:
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("p", "br", "div");
                String parse = Jsoup.clean(html, whitelist);
                parse = parse.replaceAll("&[#\\w]{3,6}[;:]?", "{空格}")
                    .replaceAll("<.*?br.*>", "{换行}")
                    .replaceAll("(\n|\r\n|<.*?p.*?>)", "{换行}")
                    .replace("　", "");
                Document document = Jsoup.parse(parse);
                Elements divs = document.select("div");
                String text = "";
                int maxLen = 0;
                for (Element div : divs) {
                    String ownText = div.ownText();
                    if (ownText.length() > maxLen && ownText.matches(".*?\\{换行}.*?\\{换行}.*?")) {
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
            if (s.trim().length() > 0) {
                content.append("    ").append(s.trim()).append("\r\n\r\n");
            }
        }
        //转码
        String text = content.toString();
        //ncr转中文
        if (config.getNcrToZh().get()) {
            text = CharacterUtil.ncr2Chinese(text);
        }
        //繁体转简体
        if (config.getTraToSimple().get()) {
            text = CharacterUtil.traditional2Simple(text);
        }
        //去广告
        if (StrUtil.isNotBlank(config.getAdStr().get())) {
            text = TextUtil.remove(text, config.getAdStr().get().split("\n"));
        }
        return text;
    }

    public String content(String url) throws IOException {
        String html = RequestUtil.get(url, config.getCookies().get(), config.getUserAgent().get(), null);
        return contentByHtml(html);
    }

    /**
     * 章节目录抓取
     *
     * @param url 目录Html
     * @return /
     */
    public List<Chapter> chaptersByHtml(String html, String url) {
        // 自定义范围
        if (config.getRule().get() == 1 || config.getRule().get() == 2) {
            html = TextUtil.getDelHtml(config.getChapterHead().get(), config.getChapterTail().get(), html);
        }
        Document document = Jsoup.parse(html, url);
        crawlTitle(document);
        Element element = document.body();
        CopyOnWriteArrayList<Element> elements = new CopyOnWriteArrayList<>(element.select("a"));
        //章节过滤
        if (config.getChapterFilter().get()) {
            filterLinks(elements);
        }
        CopyOnWriteArrayList<Chapter> chapters =
            elements.stream().map(a -> new Chapter(a.text(), a.attr(LINK_ATTR))).collect(
                Collectors.toCollection(CopyOnWriteArrayList::new));
        //去重
        removeDuplicates(chapters);
        //乱序重排
        if (config.getChapterSort().get()) {
            chapters.sort(new ChapterComparator());
        }
        return chapters;
    }

    /**
     * 章节目录抓取
     *
     * @param url 目录地址URL
     * @return /
     * @throws IOException /
     */
    public List<Chapter> chapters(String url) throws IOException {
        String html = RequestUtil.get(url, config.getCookies().get(), config.getUserAgent().get(), null);
        return chaptersByHtml(html, url);
    }

    /**
     * 过滤URL
     * 找出节点所在dom树深度次数最多的a标签
     *
     * @param as a节点列表
     */
    private void filterLinks(CopyOnWriteArrayList<Element> as) {
        final int depth = findMax(as, a -> a.parents().size());
        final int part = findMax(as, a -> a.attr(LINK_ATTR).split("/").length);
        as.forEach(a -> {
            if (a.parents().size() != depth || !UrlUtil.notAnchor(a.attr(LINK_ATTR)) || part != a.attr(LINK_ATTR).split(
                "/").length || !a.hasText()) {
                as.remove(a);
            }
        });
    }

    /**
     * 找出出现次数最多的
     *
     * @return /
     */
    private int findMax(List<Element> list, Function<Element, Integer> function) {
        Map<Integer, Integer> map = new HashMap<>(10);
        for (Element a : list) {
            int attrCount = function.apply(a);
            map.compute(attrCount, (k, c) -> {
                if (c == null) {
                    return 1;
                }
                return c + 1;
            });
        }
        int maxKey = 0;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        return maxKey;
    }

    /**
     * 抓取标题
     *
     * @param doc html doc
     */
    private void crawlTitle(Document doc) {
        Element element = doc.select("title").first();
        if (element != null) {
            String titleElement = element.text();
            String title = ReUtil.get("(.{1,10}?)最新", titleElement, 1);
            if (StrUtil.isNotBlank(title)) {
                this.title = title;
            } else {
                this.title = titleElement.substring(0, titleElement.length() > 10 ? 10 : titleElement.length());
            }
        }
    }

    /**
     * 移除重复的
     *
     * @param chapters /
     */
    private void removeDuplicates(CopyOnWriteArrayList<Chapter> chapters) {
        Set<String> existLinks = new HashSet<>(chapters.size());
        chapters.forEach(chapter -> {
            String href = chapter.getUrl();
            if (existLinks.contains(href)) {
                chapters.remove(chapter);
            } else {
                existLinks.add(href);
            }
        });
    }
}
