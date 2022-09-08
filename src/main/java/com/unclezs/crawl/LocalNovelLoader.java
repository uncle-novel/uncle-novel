package com.unclezs.crawl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.utils.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 本地小说解析器
 *
 * @author unclezs.com
 * @date 2020.05.12 16:30
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class LocalNovelLoader implements NovelLoader {
    private String regex = "([第序][一二三四五六七八九十零0-9 ]*?[章卷节回][\\s\\S]*?)\r\n";
    /**
     * 文件路径
     */
    private String path;
    /**
     * 识别出来的编码
     */
    private String charset;
    /**
     * 正文
     */
    private String[] content;
    /**
     * 书名
     */
    private String title;
    private Book book;
    private String id = IdUtil.simpleUUID();
    /**
     * 章节列表
     */
    private List<Chapter> chapters;

    @Override
    public void load(Book book) {
        this.book = book;
        this.path = book.getPath();
        String s = FileUtil.readUtf8String(book.getChapterPath());
        this.chapters = JSON.parseArray(s, Chapter.class);
    }

    /**
     * 加载本地书籍
     *
     * @param path 书籍路径
     * @return /
     */
    public boolean load(String path) {
        this.path = path;
        try {
            if (!FileUtil.exist(path)) {
                return false;
            }
            this.title = FileUtil.mainName(path);
            this.charset = com.unclezs.utils.FileUtil.getEncode(path, true);
            String text = FileUtil.readString(path, charset);
            this.content = text.split(regex);
            this.chapters =
                ReUtil.findAll(regex, text, 1).stream().map(c -> new Chapter(c, "")).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("本地书籍加载失败:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 格式化之后储存便于阅读
     * 转码为UTF8
     *
     * @return 格式化之后的配置JSON文件
     */
    @Override
    public String store() {
        for (int i = 0; i < chapters.size(); i++) {
            String text = content[i + 1];
            String contentPath = ApplicationUtil.saveCache(String.format("%s/%s", id, i), text);
            chapters.get(i).setContentPath(contentPath);
        }
        return ApplicationUtil.saveCache(String.format("%s/book.json", id), JsonUtil.toJson(chapters));
    }

    @Override
    public String content(int chapter) {
        return FileUtil.readUtf8String(chapters.get(chapter).getContentPath());
    }

    public String getContent(int chapter) {
        return content[chapter];
    }

    @Override
    public List<Chapter> chapters() {
        return this.chapters;
    }
}
