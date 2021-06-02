package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.app.main.views.home.DownloadManagerView;
import lombok.Data;

/**
 * 小说下载数据包
 *
 * @author blog.unclezs.com
 * @date 2021/4/30 15:18
 * @see DownloadManagerView
 */
@Data
public class BookBundle {

  /**
   * 小说信息
   */
  private Novel novel;
  /**
   * 规则
   */
  private AnalyzerRule rule;

  /**
   * 全部深克隆一份新的
   *
   * @param novel 小说信息
   * @param rule  规则
   */
  public BookBundle(Novel novel, AnalyzerRule rule) {
    this.novel = SerializationUtils.deepClone(novel);
    this.rule = SerializationUtils.deepClone(rule);
  }
}
