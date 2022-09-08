package com.unclezs.crawl;

import com.unclezs.model.Book;
import com.unclezs.model.Chapter;

import java.util.List;

/**
 * @author uncle
 * @date 2020/5/13 17:00
 */
public interface NovelLoader {
    /**
     * 正文
     *
     * @param chapter 章节索引
     * @return 本章节的正文内容
     */
    String content(int chapter);

    /**
     * 章节列表
     *
     * @return 章节列表
     */
    List<Chapter> chapters();

    /**
     * 加载一本书
     *
     * @param book /
     */
    void load(Book book);

    /**
     * 当前得书籍信息
     *
     * @return book
     */
    Book getBook();

    /**
     * 保存到文件
     *
     * @return 文件位置
     */
    String store();
}
