package com.unclezs.novel.app.main.views.home;

import com.jfoenix.controls.JFXCheckBox;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.FileSelector;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.support.hotkey.KeyRecorder;
import com.unclezs.novel.app.framework.util.FontUtils;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.manager.HotkeyManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.config.DownloadConfig;
import com.unclezs.novel.app.main.model.config.HotKeyConfig;
import com.unclezs.novel.app.main.util.DebugUtils;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@Slf4j
@FxView(fxml = "/layout/home/setting.fxml")
@EqualsAndHashCode(callSuper = false)
public class SettingView extends SidebarView<StackPane> {

  public static final int MAX_THREAD_NUM = 32;
  public static final int MAX_TASK_NUM = 5;
  private static final String PAGE_NAME = "软件设置";
  /**
   * 快捷键
   */
  @FXML
  private TextField readerNextChapter;
  @FXML
  private TextField readerPreChapter;
  @FXML
  private TextField readerPrePage;
  @FXML
  private TextField readerNextPage;
  @FXML
  private TextField readerToc;
  @FXML
  private TextField bossKey;
  @FXML
  private JFXCheckBox enabledGlobalHotKey;
  /**
   * 书架
   */
  @FXML
  private JFXCheckBox bookAutoUpdate;
  @FXML
  private JFXCheckBox bookTitleStyle;
  /**
   * 基本设置
   */
  @FXML
  private JFXCheckBox tray;
  @FXML
  private ComboBox<String> language;
  @FXML
  private ComboBox<String> fonts;
  /**
   * 调试模式
   */
  @FXML
  private JFXCheckBox debug;
  /**
   * 下载配置
   */
  @FXML
  private JFXCheckBox volume;
  @FXML
  private Spinner<Integer> retryNum;
  @FXML
  private JFXCheckBox epub;
  @FXML
  private JFXCheckBox txt;
  @FXML
  private JFXCheckBox mobi;
  @FXML
  private ComboBox<Integer> taskNum;
  @FXML
  private ComboBox<Integer> threadNum;
  @FXML
  private FileSelector downloadFolder;
  /**
   * 设置管理器
   */
  private SettingManager manager;

  @Override
  public void onCreated() {
    manager = SettingManager.manager();
    language.valueProperty().bindBidirectional(manager.getBasic().getLang());
    fonts.getItems().setAll(FontUtils.getAllFontFamilies());
    fonts.valueProperty().bindBidirectional(manager.getBasic().getFonts());
    fonts.valueProperty().addListener(e -> ((App) AppContext.getView(HomeView.class).getApp()).changeFont(fonts.getValue()));
    tray.selectedProperty().bindBidirectional(manager.getBasic().getTray());
    // 书架
    bookAutoUpdate.selectedProperty().bindBidirectional(manager.getBookShelf().getAutoUpdate());
    bookTitleStyle.selectedProperty().bindBidirectional(manager.getBookShelf().getAlwaysShowBookTitle());
    // 热键
    initHotKey();
    // 下载配置
    initDownloadConfig();
    // 调式模式
    initDebugMode();
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    MixPanelHelper.event(PAGE_NAME);
  }

  /**
   * 初始化调试模式
   */
  private void initDebugMode() {
    debug.setSelected(manager.getDebug().get());
    debug.selectedProperty().addListener(e -> {
      manager.getDebug().set(debug.isSelected());
      if (debug.isSelected()) {
        DebugUtils.debug();
      } else {
        DebugUtils.info();
      }
    });
  }

  /**
   * 初始化下载配置
   */
  private void initDownloadConfig() {
    DownloadConfig downloadConfig = manager.getDownload();
    for (int i = 1; i <= MAX_THREAD_NUM; i++) {
      threadNum.getItems().add(i);
    }
    for (int i = 1; i <= MAX_TASK_NUM; i++) {
      taskNum.getItems().add(i);
    }
    threadNum.valueProperty().bindBidirectional(downloadConfig.getThreadNum());
    taskNum.valueProperty().bindBidirectional(downloadConfig.getTaskNum());
    downloadFolder.getInput().textProperty().bindBidirectional(downloadConfig.getFolder());
    // 重试次数
    retryNum.getValueFactory().setValue(downloadConfig.getRetryNum().get());
    retryNum.valueProperty().addListener(e -> downloadConfig.getRetryNum().set(retryNum.getValue()));
    // 分卷下载
    volume.selectedProperty().bindBidirectional(downloadConfig.getVolume());
    // 下载类型
    epub.selectedProperty().bindBidirectional(downloadConfig.getEpub());
    txt.selectedProperty().bindBidirectional(downloadConfig.getTxt());
    mobi.selectedProperty().bindBidirectional(downloadConfig.getMobi());
  }

  private void initHotKey() {
    HotKeyConfig hotkeyConfig = manager.getHotkey();
    readerNextChapter.setText(hotkeyConfig.getReaderNextChapter());
    readerPreChapter.setText(hotkeyConfig.getReaderPreChapter());
    readerNextPage.setText(hotkeyConfig.getReaderNextPage());
    readerPrePage.setText(hotkeyConfig.getReaderPrePage());
    readerToc.setText(hotkeyConfig.getReaderToc());
    bossKey.setText(hotkeyConfig.getGlobalBossKey());
    // 启用禁用全局热键
    enabledGlobalHotKey.setSelected(hotkeyConfig.isEnabledGlobal());
    enabledGlobalHotKey.selectedProperty().addListener(e -> {
      hotkeyConfig.setEnabledGlobal(enabledGlobalHotKey.isSelected());
      if (hotkeyConfig.isEnabledGlobal()) {
        HotkeyManager.init();
      } else {
        HotkeyManager.destroy();
      }
    });
    // 监听修改
    KeyRecorder recorder = new KeyRecorder();
    listenerHotKeyRecord(key -> HotkeyManager.triggerBossKey(), recorder, bossKey, hotkeyConfig::setGlobalBossKey, hotkeyConfig::getGlobalBossKey);
    listenerHotKeyRecord(null, recorder, readerNextChapter, hotkeyConfig::setReaderNextChapter, hotkeyConfig::getReaderNextChapter);
    listenerHotKeyRecord(null, recorder, readerPreChapter, hotkeyConfig::setReaderPreChapter, hotkeyConfig::getReaderPreChapter);
    listenerHotKeyRecord(null, recorder, readerNextPage, hotkeyConfig::setReaderNextPage, hotkeyConfig::getReaderNextPage);
    listenerHotKeyRecord(null, recorder, readerPrePage, hotkeyConfig::setReaderPrePage, hotkeyConfig::getReaderPrePage);
    listenerHotKeyRecord(null, recorder, readerToc, hotkeyConfig::setReaderToc, hotkeyConfig::getReaderToc);
  }

  /**
   * 监听触发热键录入器
   *
   * @param listener 全局热键触发回调
   * @param recorder 记录器
   * @param input    输入控件
   * @param setter   /
   * @param getter   /
   */
  private void listenerHotKeyRecord(HotKeyListener listener, KeyRecorder recorder, TextField input, Consumer<String> setter, Supplier<String> getter) {
    boolean global = listener != null;
    input.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.BACK_SPACE) {
        recorder.reset();
        input.setText(null);
      } else {
        recorder.record(event);
        // 实时显示
        input.setText(recorder.getKeyText());
      }
      event.consume();
    });
    input.focusedProperty().addListener(e -> {
      if (input.isFocused()) {
        recorder.reset();
        input.setPromptText(getter.get());
      } else {
        input.setPromptText("无");
        String keyText = recorder.getKeyText();
        // 修改为有效快捷键
        if (recorder.isEffective() && !Objects.equals(keyText, getter.get()) && !HotKeyManager.existed(keyText)) {
          if (global) {
            HotKeyManager.updateGlobal(getter.get(), recorder.getCombination(), listener);
          } else {
            HotKeyManager.unregisterWindowHotkey(getter.get());
            HotKeyManager.registerWindowHotkey(recorder.getCombination());
          }
          setter.accept(keyText);
          Toast.success("快捷键修改成功");
          // 删除快捷键
        } else if (StringUtils.isBlank(input.getText())) {
          input.setText(null);
          HotKeyManager.unregister(getter.get(), global);
          setter.accept(StringUtils.EMPTY);
          // 或者快捷键冲突
        } else if (!Objects.equals(keyText, getter.get()) && HotKeyManager.existed(keyText)) {
          input.setText(getter.get());
          Toast.error("快捷键冲突：" + keyText);
          // 未修改
        } else {
          input.setText(getter.get());
        }
      }
    });
  }
}
