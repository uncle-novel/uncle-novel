package com.unclezs.novel.app.main.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import java.util.List;
import lombok.Data;

/**
 * 有声小说
 *
 * @author blog.unclezs.com
 * @date 2021/5/5 19:38
 */
@Data
@DatabaseTable(tableName = "audio_book")
public class AudioBook {

  /**
   * ID
   */
  @DatabaseField(id = true)
  private String id;
  /**
   * 目录地址
   */
  @DatabaseField
  private String url;
  /**
   * 书名
   */
  @DatabaseField
  private String title;
  /**
   * 作者
   */
  @DatabaseField
  private String author;
  /**
   * 播音
   */
  @DatabaseField
  private String broadcast;
  /**
   * 封面
   */
  @DatabaseField
  private String cover;
  /**
   * 上次听到的章节
   */
  @DatabaseField
  private int currentChapterIndex = 0;
  /**
   * 上次听到的位置
   */
  @DatabaseField
  private double currentProgress;
  /**
   * 上次听到的一章节名字
   */
  @DatabaseField
  private String currentChapterName;
  /**
   * 排序 desc
   */
  @DatabaseField
  private int order;
  /**
   * 章节列表
   */
  private List<Chapter> toc;

  /**
   * 从novel转换得到
   *
   * @param novel 小说
   * @return audio book
   */
  public static AudioBook fromNovel(Novel novel) {
    AudioBook book = new AudioBook();
    book.url = novel.getUrl();
    book.author = novel.getAuthor();
    book.title = novel.getTitle();
    book.broadcast = novel.getBroadcast();
    book.cover = novel.getCoverUrl();
    book.toc = novel.getChapters();
    book.currentChapterIndex = 0;
    book.currentChapterName = book.toc.get(0).getName();
    book.currentProgress = 0;
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
    novel.setTitle(title);
    novel.setBroadcast(broadcast);
    novel.setCoverUrl(cover);
    novel.setChapters(toc);
    return novel;
  }
}
