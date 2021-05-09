package com.unclezs.novel.app.main.model;

import cn.hutool.core.util.IdUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.unclezs.novel.analyzer.model.Novel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 有声小说
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 19:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@DatabaseTable(tableName = "audio_book")
public class AudioBook extends Book {

  /**
   * 当前进度
   */
  @DatabaseField
  private double currentProgress;
  /**
   * 播音
   */
  @DatabaseField
  private String broadcast;
  /**
   * 上次听到的一章节名字
   */
  @DatabaseField
  private String currentChapterName;

  /**
   * 从bookBundle转换得到
   *
   * @param bookBundle 小说信息
   * @return audio book
   */
  public static AudioBook fromBookBundle(BookBundle bookBundle) {
    Novel novel = bookBundle.getNovel();
    AudioBook book = new AudioBook();
    book.url = novel.getUrl();
    book.author = novel.getAuthor();
    book.name = novel.getTitle();
    book.broadcast = novel.getBroadcast();
    book.cover = novel.getCoverUrl();
    book.toc = novel.getChapters();
    book.id = IdUtil.fastSimpleUUID();
    book.currentChapterIndex = 0;
    book.currentChapterName = book.toc.get(0).getName();
    book.currentProgress = 0;
    book.rule = bookBundle.getRule();
    return book;
  }

  /**
   * 转换为novel
   *
   * @return novel
   */
  public Novel toNovel() {
    Novel novel = new Novel();
    novel.setUrl(url);
    novel.setAuthor(author);
    novel.setTitle(name);
    novel.setBroadcast(broadcast);
    novel.setCoverUrl(cover);
    novel.setChapters(toc);
    return novel;
  }
}
