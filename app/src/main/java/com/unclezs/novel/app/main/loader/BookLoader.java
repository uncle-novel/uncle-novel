package com.unclezs.novel.app.main.loader;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
import com.unclezs.novel.app.main.util.BookHelper;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 16:05
 */
@Slf4j
public class BookLoader extends AbstractBookLoader {

  private final List<Chapter> toc;
  private final AnalyzerRule rule;
  private final BitSet running = new BitSet();

  public BookLoader(Book book) {
    super(book);
    BookCache cache = BookHelper.loadCache(FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId()));
    this.toc = cache.getToc();
    this.rule = cache.getRule();
  }

  @Override
  public List<Chapter> toc() {
    return toc;
  }

  @Override
  public String loadContent(int index) {
    Chapter chapter = toc().get(index);
    File cache = FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), String.valueOf(index));
    if (cache.exists()) {
      return FileUtil.readUtf8String(cache);
    } else {
      if (!running.get(index)) {
        running.set(index);
        try {
          NovelSpider spider = new NovelSpider(rule);
          String content = spider.content(chapter.getUrl());
          content = StringUtils.removeBlankLines(content);
          FileUtil.writeUtf8String(content, cache);
          return content;
        } catch (IOException e) {
          log.error("正文加载失败：{}", chapter, e);
        } finally {
          running.clear(index);
        }
      }
    }
    return null;
  }
}
