package com.unclezs.novel.app.main.loader;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
import com.unclezs.novel.app.main.util.BookHelper;
import java.util.List;
import lombok.Getter;

/**
 * 书籍加载器
 *
 * @author blog.unclezs.com
 * @date 2021/5/7 16:00
 */
public abstract class AbstractBookLoader {

  @Getter
  protected Book book;
  protected List<Chapter> toc;
  protected AnalyzerRule rule;

  public void setBook(Book book) {
    this.book = book;
    BookCache cache = BookHelper.loadCache(FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId()));
    this.toc = cache.getToc();
    this.rule = cache.getRule();
  }


  /**
   * 返回章节列表
   *
   * @return 章节列表
   */
  public List<Chapter> toc() {
    return toc;
  }

  /**
   * 获取正文
   *
   * @param index 章节索引
   * @return 正文
   */
  public String content(int index) {
    if (index >= 0 && index < toc().size()) {
      return loadContent(index);
    }
    return null;
  }


  /**
   * 获取正文
   *
   * @param index 章节索引
   * @return 正文
   */
  public abstract String loadContent(int index);

  /**
   * 正文是否已经被缓存了
   *
   * @param index 索引
   * @return true 已经被缓存
   */
  public boolean isCached(int index) {
    return FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), String.valueOf(index)).exists();
  }
}
