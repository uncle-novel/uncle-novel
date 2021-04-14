package com.unclezs.novel.app.framework.components.icon;

import lombok.Getter;

/**
 * 字体图标 Generate By Gradle
 *
 * @author blog.unclezs.com
 * @date 2021/4/13 12:27
 */
@Getter
public enum IconFont {
  /**
   * 字体图标
   */
  AUDIO_BOOKSHELF('\ue603'),
  SEARCH_AUDIO('\ue6d9'),
  BOOKSHELF('\ue612'),
  MAX('\ue614'),
  SEARCH_NETWORK('\ue605'),
  MENU('\ue600'),
  ANALYSIS_DOWNLOAD('\ue606'),
  SETTING('\ue604'),
  MAX_RESTORE('\ue609'),
  EXIT('\ue634'),
  DOWNLOAD('\ue61d'),
  SEARCH('\ue61c'),
  MIN('\ue637'),
  DOWNLOAD_MANAGEMENT('\ue716'),
  THEME('\ue65d');

  private final char unicode;

  IconFont(char unicode) {
    this.unicode = unicode;
  }
}
