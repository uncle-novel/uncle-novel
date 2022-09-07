package com.unclezs.novel.app.main.core.loader;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.views.home.FictionBookshelfView;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

/**
 * @author blog.unclezs.com
 * @since 2021/5/7 16:05
 */
@Slf4j
public class BookLoader extends AbstractBookLoader {

  private final BitSet running = new BitSet();

  @Override
  public String loadContent(int index) {
    File cache = FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), String.valueOf(index));
    String content;
    if (cache.exists()) {
      content = FileUtil.readUtf8String(cache);
    } else {
      content = load(index);
    }
    if (!isCached(index + 1)) {
      preloading(index + 1);
    }
    return content;
  }

  private String load(int index) {
    if (running.get(index) && isCached(index)) {
      return null;
    }
    running.set(index);
    Chapter chapter = toc().get(index);
    File cache = FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), String.valueOf(index));
    try {
      NovelSpider spider = new NovelSpider(rule);
      String content = spider.content(chapter.getUrl());
      if (!isCached(index)) {
        FileUtil.writeUtf8String(content, cache);
      }
      return content;
    } catch (IOException e) {
      log.error("正文加载失败：{}", chapter, e);
    } finally {
      running.clear(index);
    }
    return null;
  }

  /**
   * 预加载
   */
  public void preloading(int index) {
    Executor.run(() -> {
      for (int i = index; i < index + 10 && i < toc().size(); i++) {
        if (isCached(i)) {
          break;
        }
        load(i);
      }
    });
  }
}
