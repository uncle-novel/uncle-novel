package com.unclezs.test;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.crawl.NovelSpider;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.utils.HtmlUnitUtil;
import com.unclezs.utils.HtmlUtil;
import com.unclezs.utils.URLEncoder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *@author unclezs.com
 *@date 2019.06.20 21:27
 */
public class AppTest {

    public static void main(String[] args) throws IOException {
        String url="https://www.ting22.com/search.php?q=%E5%AE%8C%E7%BE%8E%E4%B8%96%E7%95%8C";
        String host = ReUtil.get("http[s]{0,1}://(.+?)/", url, 1);
        System.out.println(host);
    }

    @Test
    public void testAudio(){
        AudioNovelSpider audioNovelSpider=new AudioNovelSpider();
        List<AudioBook> books = audioNovelSpider.searchBook("完美世界", "ting22", "书名");
        System.out.println(books);
    }

    @Test
    public void testStr() throws IOException {
        AudioNovelSpider audioNovelSpider=new AudioNovelSpider();
        List<AudioChapter> chapters = audioNovelSpider.getChapters("https://www.ting22.com/book/205.html");
        System.out.println(chapters);
    }
}
