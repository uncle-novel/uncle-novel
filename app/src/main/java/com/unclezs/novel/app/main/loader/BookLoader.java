package com.unclezs.novel.app.main.loader;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 16:05
 */
@Slf4j
public class BookLoader extends AbstractBookLoader {

  private final BitSet running = new BitSet();

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
