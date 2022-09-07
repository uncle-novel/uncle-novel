package com.unclezs.novel.app.main.db.beans;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.app.main.core.spider.SpiderWrapper;
import lombok.Data;

import java.util.StringJoiner;

/**
 * 下载历史
 *
 * @author blog.unclezs.com
 * @since 2021/5/4 20:49
 */
@Data
@DatabaseTable(tableName = "download_history")
public class DownloadHistory {

  public static final int AUDIO = 1;
  public static final int TXT = 2;
  public static final int EPUB = 3;
  public static final int MOBI = 4;

  @DatabaseField(generatedId = true)
  private int id;
  /**
   * 小说名称
   */
  @DatabaseField
  private String name;
  /**
   * 下载的路径
   */
  @DatabaseField
  private String path;
  /**
   * 时间
   */
  @DatabaseField
  private String date;
  /**
   * 保存的格式
   */
  @DatabaseField
  private String type;

  public static DownloadHistory fromWrapper(SpiderWrapper wrapper) {
    DownloadHistory history = new DownloadHistory();
    history.setDate(DateUtil.today());
    history.setName(wrapper.getName());
    history.setPath(wrapper.getSpider().getSavePath());
    AnalyzerRule rule = wrapper.getSpider().getAnalyzerRule();
    if (Boolean.TRUE.equals(rule.getAudio())) {
      history.setType("有声");
    } else {
      StringJoiner joiner = new StringJoiner(StrUtil.COMMA);
      if (wrapper.isTxt()) {
        joiner.add("TXT");
      }
      if (wrapper.isMobi()) {
        joiner.add("MOBI");
      }
      if (wrapper.isEpub()) {
        joiner.add("EPUB");
      }
      history.setType(joiner.toString());
    }
    return history;
  }
}
