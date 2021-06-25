package com.unclezs.novel.app.main.manager;

import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyCombination;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.model.config.HotKeyConfig;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/6/2 20:05
 */
@UtilityClass
public class HotkeyManager {

  private static void registerBossKey(String keystroke) {
    HotKeyManager.registerGlobal(HotKeyCombination.fromStroke(keystroke), key -> triggerBossKey());
  }


  /**
   * 触发老板键
   */
  public static void triggerBossKey() {
    Executor.runFx(() -> {
      if (App.stage().isIconified()) {
        App.stage().setIconified(false);
        return;
      }
      if (App.stage().isShowing()) {
        // 隐藏窗口且隐藏托盘图标
        App.tray();
        TrayManager.hide();
      } else {
        // 显示窗口且显示托盘图标
        App.requestShow();
        TrayManager.tray();
      }
    });
  }

  /**
   * 初始化热键
   */
  public static void init() {
    HotKeyConfig config = SettingManager.manager().getHotkey();
    registerBossKey(config.getGlobalBossKey());
    if (StringUtils.isNotBlank(config.getReaderNextChapter())) {
      HotKeyManager.registerWindowHotkey(HotKeyCombination.fromStroke(config.getReaderNextChapter()));
    }
    if (StringUtils.isNotBlank(config.getReaderToc())) {
      HotKeyManager.registerWindowHotkey(HotKeyCombination.fromStroke(config.getReaderToc()));
    }
    if (StringUtils.isNotBlank(config.getReaderNextPage())) {
      HotKeyManager.registerWindowHotkey(HotKeyCombination.fromStroke(config.getReaderNextPage()));
    }
    if (StringUtils.isNotBlank(config.getReaderPreChapter())) {
      HotKeyManager.registerWindowHotkey(HotKeyCombination.fromStroke(config.getReaderPreChapter()));
    }
    if (StringUtils.isNotBlank(config.getReaderPrePage())) {
      HotKeyManager.registerWindowHotkey(HotKeyCombination.fromStroke(config.getReaderPrePage()));
    }
  }

  public static void destroy() {
    HotKeyManager.clearGlobal();
  }
}
