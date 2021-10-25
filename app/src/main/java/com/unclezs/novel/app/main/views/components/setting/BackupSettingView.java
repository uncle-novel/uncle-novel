package com.unclezs.novel.app.main.views.components.setting;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.support.LocalizedSupport;
import com.unclezs.novel.app.framework.util.Choosers;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.viewmodel.BackupViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 备份与恢复配置页面
 *
 * @author blog.unclezs.com
 * @date 2021/5/10 23:27
 */
@Slf4j
public class BackupSettingView extends SettingItems implements LocalizedSupport {

  private final BackupViewModel viewModel = new BackupViewModel();

  public BackupSettingView() {
    super(LocalizedSupport.app("setting.backup"));
    setItems(backupContentView(), webDavView());
  }

  /**
   * 创建内容box
   *
   * @param name     名称
   * @param property 属性
   * @return box
   */
  private JFXCheckBox createBackupContent(String name, ObjectProperty<Boolean> property) {
    JFXCheckBox checkBox = new JFXCheckBox(name);
    checkBox.selectedProperty().bindBidirectional(property);
    return checkBox;
  }

  private TextField createTextField(String name, ObjectProperty<String> property, boolean password) {
    TextField textField = password ? new PasswordField() : new TextField();
    textField.setPromptText(name);
    textField.textProperty().bindBidirectional(property);
    return textField;
  }

  /**
   * 备份内容
   *
   * @return 设置Item
   */
  private SettingItem backupContentView() {
    JFXCheckBox bookshelf =
      createBackupContent(localized("setting.backup.bookshelf"), viewModel.getConfig().getBookshelf());
    JFXCheckBox rule = createBackupContent(localized("setting.backup.rule"), viewModel.getConfig().getRule());
    JFXCheckBox audio =
      createBackupContent(localized("setting.backup.bookshelf.audio"), viewModel.getConfig().getAudio());
    JFXCheckBox searchEngine =
      createBackupContent(localized("setting.backup.search.engine"), viewModel.getConfig().getSearchEngine());
    HBox hBox = new HBox(bookshelf, audio, rule, searchEngine);
    hBox.setSpacing(10);
    return new SettingItem(localized("setting.backup.content"), hBox);
  }

  private SettingItem webDavView() {
    TextField url = createTextField(localized("setting.backup.webdav.server"), viewModel.getConfig().getUrl(), false);
    TextField username =
      createTextField(localized("setting.backup.webdav.username"), viewModel.getConfig().getUsername(), false);
    TextField password =
      createTextField(localized("setting.backup.webdav.password"), viewModel.getConfig().getPassword(), true);

    IconButton backup = NodeHelper.addClass(new IconButton(localized("setting.backup.webdav.action.backup")), "btn");
    backup.setOnAction(e -> doSync(true));
    IconButton restore = NodeHelper.addClass(new IconButton(localized("setting.backup.webdav.action.restore")), "btn");
    restore.setOnAction(e -> doSync(false));
    IconButton backupToFile =
      NodeHelper.addClass(new IconButton(localized("setting.backup.webdav.action.export")), "btn");
    backupToFile.setOnAction(e -> exportBackup());
    IconButton restoreFromFile =
      NodeHelper.addClass(new IconButton(localized("setting.backup.webdav.action.import")), "btn");
    restoreFromFile.setOnAction(e -> importBackup());
    HBox inputs = new HBox(url, username, password);
    inputs.setSpacing(20);
    HBox actions = new HBox(backup, restore, backupToFile, restoreFromFile);
    actions.setSpacing(10);
    VBox container = new VBox(inputs, actions);
    container.setSpacing(10);
    return new SettingItem(localized("setting.backup.webdav"), container);
  }

  private void importBackup() {
    File backupFile = Choosers.chooseFile();
    if (backupFile != null) {
      viewModel.restoreBackup(backupFile);
    }
  }

  private void exportBackup() {
    File folder = Choosers.chooseFolder();
    if (folder != null) {
      viewModel.createBackup(folder);
    }
  }

  private void doSync(final boolean backup) {
    if (viewModel.enabledWebDav()) {
      String type = backup ? "备份" : "恢复";
      TaskFactory.create(() -> {
          if (backup) {
            viewModel.backup();
          } else {
            viewModel.restore();
          }
          return null;
        }).onFailed(e -> {
          String message = String.format("%s失败", type);
          log.error("{}：{}", message, e.getMessage(), e);
          Toast.error(message + ": " + e.getMessage());
        })
        .onSuccess(v -> Toast.success(String.format("%s成功", type)))
        .start();
    } else {
      Toast.error(localized("setting.backup.webdav.tip.error"));
    }
  }
}
