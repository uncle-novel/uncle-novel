package com.unclezs.novel.app.framework.support.hotkey;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.util.StrUtils;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.KeyEvent;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/tulskiy/jkeymaster
 *
 * @author blog.unclezs.com
 * @since 2019.07.31
 */
@Slf4j
@UtilityClass
public class HotKeyManager {

  private static final Provider PROVIDER = Provider.getCurrentProvider(true);
  @Getter
  private static final Map<String, HotKeyCombination> HOT_KEY_COMBINATIONS = new HashMap<>(16);
  private static final Map<String, HotKeyCombination> GLOBAL_HOT_KEY_COMBINATIONS = new HashMap<>(16);


  /**
   * 获取窗口热键
   *
   * @param key 热键stroke
   * @return 组合键
   */
  public static HotKeyCombination getWindowHotKey(String key) {
    return HOT_KEY_COMBINATIONS.get(key);
  }

  /**
   * 判断是否匹配窗口热键
   *
   * @param key   热键stroke
   * @param event 按键事件
   * @return true 匹配
   */
  public static boolean windowHotKeyMatch(String key, KeyEvent event) {
    HotKeyCombination combination = HOT_KEY_COMBINATIONS.get(key);
    if (combination == null) {
      return false;
    }
    return combination.getCombination().match(event);
  }

  /**
   * 停用热键
   */
  public static void unbind() {
    PROVIDER.close();
  }

  /**
   * 清空全局热键
   */
  public static void clearGlobal() {
    Executor.run(() -> {
      PROVIDER.reset();
      GLOBAL_HOT_KEY_COMBINATIONS.clear();
      log.debug("全局热键已清空");
    });
  }

  /**
   * 注册全局热键
   *
   * @param combination 组合键
   */
  public static void registerGlobal(HotKeyCombination combination, HotKeyListener hotKeyListener) {
    if (combination == null || HOT_KEY_COMBINATIONS.containsKey(combination.getStroke())) {
      return;
    }
    GLOBAL_HOT_KEY_COMBINATIONS.put(combination.getStroke(), combination);
    registerGlobal(combination.getKeyStroke(), hotKeyListener);
  }

  /**
   * 更新全局热键
   *
   * @param oldStroke   旧的
   * @param combination 新的
   */
  public static void updateGlobal(String oldStroke, HotKeyCombination combination, HotKeyListener listener) {
    unregisterGlobal(oldStroke);
    registerGlobal(combination, listener);
    log.debug("更新全局热键：{} -> {}", oldStroke, combination.getStroke());
  }

  /**
   * 注册全局热键
   *
   * @param keyStroke 热键
   * @param listener  回调
   */
  private static void registerGlobal(KeyStroke keyStroke, HotKeyListener listener) {
    if (keyStroke != null && listener != null) {
      PROVIDER.register(keyStroke, listener);
      log.debug("成功注册【全局】热键：{}", keyStroke);
    }
  }

  /**
   * 取消注册全局热键
   *
   * @param keyText 热键
   */
  public static void unregisterGlobal(String keyText) {
    if (StrUtils.isNotBlank(keyText)) {
      GLOBAL_HOT_KEY_COMBINATIONS.remove(keyText);
      KeyStroke stroke = KeyStroke.getKeyStroke(keyText);
      if (stroke != null) {
        PROVIDER.unregister(KeyStroke.getKeyStroke(keyText));
      }
      log.debug("取消【全局】热键：{}", keyText);
    }
  }

  /**
   * 取消注册热键
   *
   * @param keyText 组合键
   * @param global  true 全局
   */
  public static void unregister(String keyText, boolean global) {
    if (global) {
      unregisterGlobal(keyText);
    } else {
      unregisterWindowHotkey(keyText);
    }
  }


  /**
   * 注册窗口热键
   *
   * @param combination 组合键
   */
  public static void registerWindowHotkey(HotKeyCombination combination) {
    if (combination == null || HOT_KEY_COMBINATIONS.containsKey(combination.getStroke())) {
      return;
    }
    HOT_KEY_COMBINATIONS.put(combination.getStroke(), combination);
    log.debug("成功注册【窗口】热键：{} , 当前热键数量：{}", combination, HOT_KEY_COMBINATIONS.size());
  }

  /**
   * 取消注册热键
   *
   * @param keyText 热键
   */
  public static void unregisterWindowHotkey(String keyText) {
    if (StrUtils.isNotBlank(keyText)) {
      HOT_KEY_COMBINATIONS.remove(keyText);
      log.debug("取消【窗口】热键：{} , 当前热键数量：{}", keyText, HOT_KEY_COMBINATIONS.size());
    }
  }

  /**
   * 是否已经注册过热键
   *
   * @param stroke 文字
   * @return true 存在
   */
  public static boolean existed(String stroke) {
    return HOT_KEY_COMBINATIONS.containsKey(stroke) || GLOBAL_HOT_KEY_COMBINATIONS.containsKey(stroke);
  }
}
