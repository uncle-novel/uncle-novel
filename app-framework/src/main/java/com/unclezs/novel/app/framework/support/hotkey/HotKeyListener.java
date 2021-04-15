package com.unclezs.novel.app.framework.support.hotkey;

import com.tulskiy.keymaster.common.HotKey;

/**
 * @author blog.unclezs.com
 * @since 2021/03/09 11:02
 */
public interface HotKeyListener extends com.tulskiy.keymaster.common.HotKeyListener {

  /**
   * 全局热键触发时回调，
   *
   * @param hotKey 热键
   */
  @Override
  default void onHotKey(HotKey hotKey) {
  }

  /**
   * 窗口热键触发时回调
   *
   * @param hotKey 热键
   */
  default void onWindowHotKey(HotKeyCombination hotKey) {

  }
}
