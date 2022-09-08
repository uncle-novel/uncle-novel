package com.unclezs.crawl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.mapper.BookMapper;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.utils.JsonUtil;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * 网络小说加载器
 *
 * @author unclezs.com
 * @date 2019.06.26 20:55
 */
@Getter
@Slf4j
@NoArgsConstructor
public class WebNovelLoader implements NovelLoader {
    private Book book;
    private TextNovelSpider spider;
    private Config config;

    /**
     * 缓存章节
     *
     * @param index /
     */
    private void cache(int index) {
        ThreadUtil.execute(() -> {
            if (index < 0 || index >= config.getChapters().size()) {
                return;
            }
            Chapter chapter = config.getChapters().get(index);
            if (StrUtil.isEmpty(chapter.getContentPath())) {
                try {
                    String content = spider.content(chapter.getUrl());
                    String path = ApplicationUtil.saveCache(String.format("%s/%s", book.getId(), index), content);
                    config.getChapters().get(index).setContentPath(path);
                } catch (IOException e) {
                    log.info("缓存失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public String content(int chapterIndex) {
        Chapter chapter = config.getChapters().get(chapterIndex);
        if (StrUtil.isEmpty(chapter.getContentPath())) {
            try {
                cache(chapterIndex - 1);
                cache(chapterIndex + 1);
                String content = spider.content(chapter.getUrl());
                content = TextUtil.removeText(content, chapter.getName());
                String path = ApplicationUtil.saveCache(String.format("%s/%s", book.getId(), chapterIndex), content);
                config.getChapters().get(chapterIndex).setContentPath(path);
                return content;
            } catch (IOException e) {
                return "章节抓取失败: " + e.getMessage();
            }
        } else {
            return FileUtil.readUtf8String(chapter.getContentPath());
        }
    }

    @Override
    public List<Chapter> chapters() {
        return config.getChapters();
    }

    @Override
    public void load(Book book) {
        this.book = book;
        config = JSON.parseObject(FileUtil.readUtf8String(book.getChapterPath()), Config.class);
        spider = new TextNovelSpider(config.getRule());
    }

    /**
     * 持久化书籍信息
     *
     * @param book      书籍
     * @param blackList 章节列表黑名单
     * @return /
     */
    public boolean load(Book book, List<String> blackList, List<Chapter> chapters, AnalysisConfig rule) {
        String path = ApplicationUtil.saveCache(String.format("%s/book.json", book.getId()),
            JsonUtil.toJson(new Config(chapters, blackList, rule)));
        book.setChapterPath(path);
        book.setWeb(true);
        MybatisUtil.execute(BookMapper.class, mapper -> mapper.insert(book));
        return true;
    }

    /**
     * 保存配置
     */
    @Override
    public String store() {
        return ApplicationUtil.saveCache(String.format("%s/book.json", book.getId()), JsonUtil.toJson(config));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Config {
        List<Chapter> chapters;
        List<String> blackList;
        AnalysisConfig rule;
    }


}
