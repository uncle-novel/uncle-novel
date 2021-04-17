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
  SEARCH_NETWORK('\ue605'),
  ERROR('\ue647'),
  ANALYSIS_DOWNLOAD('\ue606'),
  EMPTY('\ue601'),
  SETTING('\ue604'),
  DOWNLOAD('\ue61d'),
  SEARCH('\ue61c'),
  MIN('\ue637'),
  DOWNLOAD_MANAGEMENT('\ue716'),
  CHECKED('\ue602'),
  THEME('\ue65d'),
  INFO('\ue9af'),
  NO_COVER('\ue723'),
  SEARCH_AUDIO('\ue6d9'),
  BOOKSHELF('\ue612'),
  MAX('\ue614'),
  MENU('\ue600'),
  CONFIRM('\ue8a5'),
  WARN('\ue688'),
  MAX_RESTORE('\ue609'),
  EXIT('\ue634'),
  SUCCESS('\ue76c');

  private final char unicode;

  IconFont(char unicode) {
    this.unicode = unicode;
  }
}
