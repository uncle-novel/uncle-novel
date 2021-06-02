package com.unclezs.novel.app.main.manager;

import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyCombination;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.model.config.HotKeyConfig;
import com.unclezs.novel.app.main.util.TrayManager;
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
   * 初始化全局热键
   */
  public static void init() {
    HotKeyConfig config = SettingManager.manager().getHotkey();
    registerBossKey(config.getGlobalBossKey());
  }

  public static void destroy() {
    HotKeyManager.clearGlobal();
  }
}
