package com.unclezs.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.unclezs.crawl.LocalNovelLoader;
import com.unclezs.crawl.TextNovelSpider;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.mapper.SearchTextRuleMapper;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.NovelInfo;
import com.unclezs.model.rule.SearchTextRule;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.XpathUtil;
import javafx.beans.property.SimpleStringProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author unclezs.com
 * @date 2019.06.20 21:27
 */
public class AppTest {
    @Before
    public static void init() {
        RequestUtil.initSSL();
    }

    public static void main(String[] args) throws IOException {
        boolean a = true;
        if (a != true) {

        }
        String s1 = IoUtil.read(AppTest.class.getResourceAsStream("/conf/search_text_rule.json")).toString("utf-8");
        System.out.println(s1);
        String keyword = "完美世界";
        String[] ignores = {"searchKey", "searchLink", "resultList", "name", "site"};
        List<String> ignored = Arrays.stream(ignores).collect(Collectors.toList());
        List<SearchTextRule> rules = null;
        for (SearchTextRule rule : rules) {
            HttpRequest s = HttpUtil.createGet(rule.getSearchLink())
                .form(rule.getSearchKey(), keyword)
                .header("Referer", rule.getSearchLink())
                .header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36");
            HttpResponse execute = s.execute();
            String body = execute.body();
            Document document = Jsoup.parse(body, rule.getSearchLink());
            Elements elements = Xsoup.compile(rule.getResultList()).evaluate(document).getElements();
            for (Element element : elements) {
                NovelInfo map = XpathUtil.xpath(element, rule, ignored, NovelInfo.class);
                System.out.println(map);
            }
        }
    }

    @Test
    void mybatisPlusTest() throws Exception {
        LocalNovelLoader localNovelLoader = new LocalNovelLoader();
        boolean b = localNovelLoader.load("F:\\小说\\完美世界.txt");
        if (b) {
            String content = localNovelLoader.content(0);
            System.out.println(content);
            localNovelLoader.store();
        }
    }

    @Test
    void serializableTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        String text = FileUtil.readString("D:\\java\\NovelHarvester\\2.txt", CharsetUtil.UTF_8);
        String s = new String(text.getBytes(), StandardCharsets.UTF_8);
//        text = Convert.convertCharset(text, "GBK",  CharsetUtil.UTF_8);
        System.out.println(s);
    }


    @Test
    public void testAudio() throws IOException {
        SearchTextRule execute =
            MybatisUtil.execute(SearchTextRuleMapper.class, mapper -> mapper.selectById("x23qb.com"));
        TextNovelSpider spider = new TextNovelSpider();
        List<NovelInfo> search = spider.search("完美", execute, SearchKeyType.TITLE);
        System.out.println(search);
//        System.out.println(spider.getAudioLink("http://www.lrts.me/ajax/playlist/2/35746/6"));
    }

    @Test
    public void testStr() throws IOException {
        String baiduApi = "https://www.baidu.com/s?wd=%s&pn=%s";
        Document html = RequestUtil.doc(String.format(baiduApi, "完美世界 小说章节目录", 0));
        Elements items = html.select(".t a");
        for (Element item : items) {
//            System.out.println(Re);
            System.out.println(item.text() + "     " + item.absUrl("href"));
        }
    }


    @Test
    public void testTextNovel() throws IOException {
        AnalysisConfig config = new AnalysisConfig();
        config.setCookies(new SimpleStringProperty(
            "_ga=GA1.2.1817444373.1594970578; authtoken1=eGlhb2h1YTEyMTM4; authtoken6=1; bgcolor=bg-default; word=select-m; _paabbcc=ftq545k2em94ce3g5bfvt7dsa0; _po18rf-tk001=1f7bbff881fe2f367b9ff1f2ccfd7737f6fc63beacaa462eabb25ffc8c26e682a%3A2%3A%7Bi%3A0%3Bs%3A13%3A%22_po18rf-tk001%22%3Bi%3A1%3Bs%3A32%3A%22mjXs4qJgLGJd_NnlQnJU7xBZsmIaVBzw%22%3B%7D; _gid=GA1.2.704400469.1598143190; url=https%3A%2F%2Fwww.po18.tw%2Fsite%2Falarm; authtoken2=N2NlY2IyODUwNDhiNDkxMTRkNGRhNDg4MDBmZmE0ZWI%3D; authtoken3=2392904587; authtoken4=1200103161; authtoken5=1598143247"));
        TextNovelSpider spider = new TextNovelSpider(config);
        System.out.println(RequestUtil.get("http://myip.ipip.net/"));
        System.setProperty("proxyHost", "127.0.0.1");
        System.setProperty("proxyPort", "1080");
        System.out.println(spider.chapters("https://www.po18.tw/books/699327/articles"));
    }


}
