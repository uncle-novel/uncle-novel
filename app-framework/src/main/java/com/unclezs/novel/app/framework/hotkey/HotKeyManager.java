package com.unclezs.novel.app.framework.hotkey;

import com.tulskiy.keymaster.common.Provider;
import com.unclezs.novel.app.framework.util.StrUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javax.swing.KeyStroke;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/tulskiy/jkeymaster
 *
 * @author blog.unclezs.com
 * @date 2019.07.31
 */
@Slf4j
@UtilityClass
public class HotKeyManager {

  private static final Provider PROVIDER = Provider.getCurrentProvider(true);
  private static final Map<String, HotKeyCombination> HOT_KEY_COMBINATIONS = new HashMap<>(16);
  private static final Map<String, HotKeyCombination> GLOBAL_HOT_KEY_COMBINATIONS = new HashMap<>(
      16);
  private static EventHandler<? super KeyEvent> windowKeyEventHandler;
  private static KeyRecorder recorder;

  public static Map<String, HotKeyCombination> getGlobalHotKeyCombinations() {
    return Collections.unmodifiableMap(GLOBAL_HOT_KEY_COMBINATIONS);
  }

  public static Map<String, HotKeyCombination> getHotKeyCombinations() {
    return Collections.unmodifiableMap(HOT_KEY_COMBINATIONS);
  }

  /**
   * 初始化热键
   */
  public static void init() {
    PROVIDER.reset();
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
    PROVIDER.reset();
  }

  /**
   * 注册热键
   *
   * @param keyStroke 热键
   * @param listener  回调
   */
  private static void register(KeyStroke keyStroke, HotKeyListener listener) {
    if (keyStroke != null && listener != null) {
      PROVIDER.register(keyStroke, listener);
    }
  }

  /**
   * 取消注册热键
   *
   * @param keyText 热键
   */
  public static void unregister(String keyText) {
    if (StrUtils.isNotBlank(keyText)) {
      HOT_KEY_COMBINATIONS.remove(keyText);
      log.debug("取消【窗口】热键：{} , 当前热键数量：{}", keyText, HOT_KEY_COMBINATIONS.size());
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
      log.debug("取消【全局】热键：{} , 当前热键数量：{}", keyText, GLOBAL_HOT_KEY_COMBINATIONS.size());
    }
  }

  /**
   * 注册全局热键
   *
   * @param combination 组合键
   */
  public static void registerGlobal(HotKeyCombination combination) {
    if (combination == null || GLOBAL_HOT_KEY_COMBINATIONS.containsKey(combination.getText())) {
      return;
    }
    register(combination.getKeyStroke(), combination.getListener());
    GLOBAL_HOT_KEY_COMBINATIONS.put(combination.getText(), combination);
    log.debug("成功注册【全局】热键：{} , 当前热键数量：{}", combination, GLOBAL_HOT_KEY_COMBINATIONS.size());
  }

  /**
   * 注册热键
   *
   * @param combination 组合键
   */
  public static void register(HotKeyCombination combination) {
    if (combination == null || HOT_KEY_COMBINATIONS.containsKey(combination.getText())) {
      return;
    }
    HOT_KEY_COMBINATIONS.put(combination.getText(), combination);
    log.debug("成功注册【窗口】热键：{} , 当前热键数量：{}", combination, HOT_KEY_COMBINATIONS.size());
  }

  public static void bindWindowHotKeyListener(Window window) {
    if (windowKeyEventHandler == null) {
      windowKeyEventHandler = event -> {
        if (recorder().record(event)) {
          HotKeyCombination combination = HOT_KEY_COMBINATIONS.get(recorder().getKeyText());
          if (combination != null && combination.getCombination().match(event)) {
            combination.getListener().onWindowHotKey(combination);
          }
        }
      };
    }
    window.addEventFilter(KeyEvent.KEY_PRESSED, windowKeyEventHandler);
    log.debug("成功绑定窗口热键");
  }

  public static void unBindWindowHotKeyListener(Window window) {
    if (windowKeyEventHandler != null) {
      window.removeEventFilter(KeyEvent.KEY_PRESSED, windowKeyEventHandler);
    }
    log.debug("成功解绑窗口所有热键");
  }

  public static KeyRecorder recorder() {
    if (recorder == null) {
      recorder = new KeyRecorder();
    }
    return recorder;
  }
}
