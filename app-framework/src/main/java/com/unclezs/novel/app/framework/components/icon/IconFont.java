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
  EMPTY('\ue601'),
  SETTING('\ue604'),
  DOWNLOAD('\ue61d'),
  DOWNLOAD_MANAGEMENT('\ue716'),
  CHECKED('\ue602'),
  TEXT('\ue60b'),
  EXPORT('\ue68a'),
  INFO('\ue9af'),
  EDIT('\ue648'),
  BOOKSHELF('\ue612'),
  GO_BACK('\ue6ab'),
  RULE_AUDIO('\ue657'),
  ANALYSIS('\ue616'),
  PLUS('\ue790'),
  AIRPORT('\ue60c'),
  WARN('\ue688'),
  EXIT('\ue634'),
  SUCCESS('\ue76c'),
  DETAIL('\ue64e'),
  CODE('\ue662'),
  IMPORT('\ue615'),
  SAVE('\ue608'),
  RULE('\ue646'),
  ERROR('\ue647'),
  DELETE('\ue607'),
  REMOVE('\ue638'),
  ENABLED('\ue68f'),
  ANALYSIS_DOWNLOAD('\ue606'),
  SEARCH('\ue61c'),
  MIN('\ue637'),
  DISABLED('\ue735'),
  THEME('\ue65d'),
  RULE_TEXT('\ue641'),
  NO_COVER('\ue723'),
  DEBUG('\ue60a'),
  SEARCH_AUDIO('\ue6d9'),
  MAX('\ue614'),
  START('\ue6cb'),
  REVERSE('\ue60d'),
  MENU('\ue600'),
  CONFIRM('\ue8a5'),
  MAX_RESTORE('\ue609'),
  RENAME('\ue670');

  private final char unicode;

  IconFont(char unicode) {
    this.unicode = unicode;
  }
}
