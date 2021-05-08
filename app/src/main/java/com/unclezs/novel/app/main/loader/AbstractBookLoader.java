package com.unclezs.novel.app.main.loader;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.app.main.model.Book;
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

  protected AbstractBookLoader(Book book) {
    this.book = book;
  }


  /**
   * 返回章节列表
   *
   * @return 章节列表
   */
  public abstract List<Chapter> toc();

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
  public abstract boolean isCached(int index);
}
