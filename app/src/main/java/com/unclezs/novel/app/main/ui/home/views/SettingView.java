package com.unclezs.novel.app.main.ui.home.views;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.FileSelector;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.DownloadConfig;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@Slf4j
@FxView(fxml = "/layout/home/views/setting/setting.fxml")
@EqualsAndHashCode(callSuper = false)
public class SettingView extends SidebarView<StackPane> {

  public static final int MAX_THREAD_NUM = 32;
  public static final int MAX_TASK_NUM = 5;
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
  @FXML
  private ComboBox<String> language;
  /**
   * 设置管理器
   */
  private SettingManager manager;

  @Override
  public void onCreated() {
    manager = SettingManager.manager();
    language.valueProperty().bindBidirectional(manager.getLang());
    // 下载配置
    initDownloadConfig();
  }


  @Override
  public void onShow(SidebarNavigateBundle bundle) {
  }

  /**
   * 初始化下载配置
   */
  private void initDownloadConfig() {
    DownloadConfig downloadConfig = manager.getDownload();
    for (int i = 1; i < MAX_THREAD_NUM; i++) {
      threadNum.getItems().add(i);
    }
    for (int i = 1; i < MAX_TASK_NUM; i++) {
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
}
