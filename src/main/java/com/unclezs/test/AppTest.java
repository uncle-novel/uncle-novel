package com.unclezs.test;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.utils.HtmlUnitUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 *@author unclezs.com
 *@date 2019.06.20 21:27
 */
public class AppTest {

    public static void main(String[] args) throws IOException {
        AudioNovelSpider audioNovelSpider=new AudioNovelSpider();
        String ting22 = audioNovelSpider.getSrc("http://www.ting56.com/video/1579-0-0.html");
        System.out.println(ting22);
    }

    @Test
    public void testAudio(){
        String url="https://ting55.com/book/1717-3";
        AudioNovelSpider audioNovelSpider=new AudioNovelSpider();
        System.out.println(audioNovelSpider.getSrc(url));
    }

    @Test
    public void testStr() throws IOException {
        String s = HtmlUnitUtil.doRequest("https://www.mirrorfiction.com/zh-Hant/book/11418/103043").getPage().asXml();
        System.out.println(s);
    }
}
