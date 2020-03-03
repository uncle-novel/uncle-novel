package com.unclezs.crawl;

import com.unclezs.mapper.ChapterMapper;
import com.unclezs.model.Chapter;
import com.unclezs.utils.MybatisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网络小说加载器
 *
 * @author unclezs.com
 * @date 2019.06.26 20:55
 */
public class WebNovelLoader {
    private List<String> chapters;//章节列表
    private List<String> contentUrl;//正文URl
    private String[] content;//正文内容
    private String charset;//小说编码
    private Integer aid;//书籍id
    private NovelSpider spider;

    public WebNovelLoader(Integer aid, String charset, NovelSpider spider) {
        this.aid = aid;
        this.charset = charset;
        this.spider = spider;
        initLoad();
    }

    //加载小说信息
    private void initLoad() {
        chapters = new ArrayList<>();
        contentUrl = new ArrayList<>();
        ChapterMapper mapper = MybatisUtil.getMapper(ChapterMapper.class);
        List<Chapter> cs = mapper.findAllChapter(aid);
        chapters= cs.stream().map(Chapter::getChapterName).collect(Collectors.toList());
        contentUrl= cs.stream().map(Chapter::getChapterUrl).collect(Collectors.toList());
        //初始化缓存正文内容数组
        content = new String[contentUrl.size()];
    }

    //获取正文内容
    public String getContent(int index) {
        loadOnPage(index);
        //开始异步缓存
        new Thread(() -> cacheContent(index)).start();
        return content[index];
    }

    //获取章节名字信息
    public List<String> getChapters() {
        return chapters;
    }

    //获取章节url地址
    public List<String> getContentUrl() {
        return contentUrl;
    }

    //缓存前后5章节
    private void cacheContent(int index) {
        //缓存后5章节
        for (int i = index; i < index + 5 && i < contentUrl.size(); i++) {
            loadOnPage(i);
        }
        for (int i = index; i > index - 5 && i >= 0; i--) {
            loadOnPage(i);
        }
    }

    //爬去一章节，并且格式化处理
    public void loadOnPage(int index) {
        //已经缓存过不需要加载
        if (content[index] == null) {
            String content = spider.getContent(contentUrl.get(index), charset);
            //加载章节
            //空两行显示题目
            //进入缓存
            String buffer = "\r\n\r\n" +
                    content + "\r\n" +
                    //末尾显示本章完
                    "本章完\r\n";
            this.content[index] = buffer;
        }
    }
}
