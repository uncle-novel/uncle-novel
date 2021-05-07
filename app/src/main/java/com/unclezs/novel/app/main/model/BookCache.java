package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书籍缓存
 *
 * @author blog.unclezs.com
 * @date 2021/5/7 16:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCache implements Serializable {

  /**
   * 规则
   */
  private AnalyzerRule rule = new AnalyzerRule();
  /**
   * 目录
   */
  private List<Chapter> toc = Collections.emptyList();
}
