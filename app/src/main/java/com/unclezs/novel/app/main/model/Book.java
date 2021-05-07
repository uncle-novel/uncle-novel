package com.unclezs.novel.app.main.model;

import cn.hutool.core.util.IdUtil;
import com.j256.ormlite.field.DatabaseField;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import java.util.List;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 10:57
 */
@Data
public class Book {

  @DatabaseField(id = true)
  protected String id;
  @DatabaseField
  protected String url;
  @DatabaseField
  protected String name;
  @DatabaseField
  protected String group;
  @DatabaseField
  protected String author;
  @DatabaseField
  protected String cover;
  @DatabaseField
  protected int order;
  /**
   * 当前的章节
   */
  @DatabaseField
  protected int currentChapterIndex;
  /**
   * 当前进度
   */
  @DatabaseField
  protected double currentProgress;
  protected List<Chapter> toc;
  protected AnalyzerRule rule;

  /**
   * 从bookBundle转换得到
   *
   * @param bookBundle 小说信息
   * @return audio book
   */
  public static Book fromBookBundle(BookBundle bookBundle) {
    Novel novel = bookBundle.getNovel();
    Book book = new Book();
    book.url = novel.getUrl();
    book.author = novel.getAuthor();
    book.name = novel.getTitle();
    book.cover = novel.getCoverUrl();
    book.toc = novel.getChapters();
    book.rule = bookBundle.getRule();
    book.id = IdUtil.fastSimpleUUID();
    return book;
  }
}
