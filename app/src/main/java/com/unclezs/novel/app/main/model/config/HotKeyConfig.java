package com.unclezs.novel.app.main.model.config;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/6/2 19:32
 */
@Data
public class HotKeyConfig {

  /**
   * 阅读器下一页
   */
  private String readerNextPage = "RIGHT";
  /**
   * 阅读器上一页
   */
  private String readerPrePage = "LEFT";
  /**
   * 阅读器下一章
   */
  private String readerNextChapter = "DOWN";
  /**
   * 阅读器上一章
   */
  private String readerPreChapter = "UP";
  /**
   * 阅读器目录
   */
  private String readerToc = "ctrl C";
  /**
   * 老板键
   */
  private String globalBossKey = "alt U";

  /**
   * 启用全局热键
   */
  private boolean enabledGlobal = true;
}
