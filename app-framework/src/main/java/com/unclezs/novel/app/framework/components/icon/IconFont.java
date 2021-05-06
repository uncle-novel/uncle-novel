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
  PLAY('\ue610'),
  AUDIO_BOOKSHELF('\ue603'),
  PLAY_NEXT('\ue619'),
  SETTING('\ue604'),
  PLAY_PRE('\ue618'),
  TEXT('\ue60b'),
  DOWNLOAD_HISTORY('\ue754'),
  EDIT('\ue648'),
  BOOKSHELF('\ue612'),
  ANALYSIS('\ue616'),
  AIRPORT('\ue60c'),
  EXIT('\ue634'),
  FOLDER('\ue74f'),
  STOP('\ue613'),
  DETAIL('\ue64e'),
  SAVE('\ue608'),
  TOC('\ue611'),
  COOKIES('\ueb04'),
  ENABLED('\ue68f'),
  ANALYSIS_DOWNLOAD('\ue606'),
  SEARCH('\ue61c'),
  MIN('\ue637'),
  DISABLED('\ue735'),
  THEME('\ue65d'),
  COPY('\ue621'),
  VALIDATE('\ue758'),
  MAX('\ue614'),
  REVERSE('\ue60d'),
  MENU('\ue600'),
  CONFIRM('\ue8a5'),
  RENAME('\ue670'),
  SEARCH_NETWORK('\ue605'),
  RUN('\ue636'),
  DIR('\ue649'),
  EMPTY('\ue601'),
  DOWNLOAD('\ue61d'),
  DOWNLOAD_MANAGEMENT('\ue716'),
  CHECKED('\ue602'),
  EXPORT('\ue68a'),
  INFO('\ue9af'),
  GO_BACK('\ue6ab'),
  RULE_AUDIO('\ue657'),
  PAUSE('\ue60f'),
  PLUS('\ue790'),
  BOOKMARK('\ue61e'),
  WARN('\ue688'),
  PLAY_PAUSE('\ue61b'),
  DOWNLOAD_SIMPLE('\ue61a'),
  SUCCESS('\ue76c'),
  PAUSE_RUN('\ue645'),
  PLAY_TOC('\ue626'),
  CODE('\ue662'),
  IMPORT('\ue615'),
  RULE('\ue646'),
  ERROR('\ue647'),
  DELETE('\ue607'),
  REMOVE('\ue638'),
  BROWSER('\ue60e'),
  RETRY('\ue7c7'),
  RULE_TEXT('\ue641'),
  NO_COVER('\ue723'),
  DEBUG('\ue60a'),
  SEARCH_AUDIO('\ue6d9'),
  CLEAR('\ue617'),
  OPEN_DIR('\ue87f'),
  START('\ue6cb'),
  MAX_RESTORE('\ue609');

  private final char unicode;

  IconFont(char unicode) {
    this.unicode = unicode;
  }
}
