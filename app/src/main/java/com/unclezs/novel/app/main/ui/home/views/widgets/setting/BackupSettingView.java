package com.unclezs.novel.app.main.ui.home.views.widgets.setting;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ZipUtil;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.dao.AudioBookDao;
import com.unclezs.novel.app.main.dao.BookDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.AudioBook;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.WebDav;
import com.unclezs.novel.app.main.model.config.BackupConfig;
import com.unclezs.novel.app.main.ui.home.views.AudioBookShelfView;
import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
import com.unclezs.novel.app.main.ui.home.views.RuleManagerView;
import com.unclezs.novel.app.main.util.BookHelper;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

/**
 * 备份与恢复配置页面
 *
 * @author blog.unclezs.com
 * @date 2021/5/10 23:27
 */
@Slf4j
public class BackupSettingView extends SettingItems {

  public static final String WEB_DAV_BACKUP_FILE_NAME = "backup.zip";
  public static final File BACKUP_LOCAL_FILE = FileUtil.file(ResourceManager.WORK_DIR, "backup.zip");
  public static final File BACKUP_BOOK_DIR = FileUtil.file(ResourceManager.BACKUP_DIR, FictionBookshelfView.CACHE_FOLDER_NAME);
  public static final File BACKUP_AUDIO_DIR = FileUtil.file(ResourceManager.BACKUP_DIR, AudioBookShelfView.CACHE_FOLDER_NAME);
  private final BackupConfig config = SettingManager.manager().getBackupConfig();

  public BackupSettingView() {
    super("备份与恢复");
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

  private TextField createTextField(String name, ObjectProperty<String> property) {
    TextField textField = new TextField();
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
    JFXCheckBox bookshelf = createBackupContent("小说书架", config.getBookshelf());
    JFXCheckBox setting = createBackupContent("设置数据", config.getSetting());
    JFXCheckBox rule = createBackupContent("书源规则", config.getRule());
    JFXCheckBox audio = createBackupContent("有声书架", config.getAudio());
    HBox hBox = new HBox(bookshelf, audio, rule, setting);
    hBox.setSpacing(10);
    return new SettingItem("备份内容", hBox);
  }

  private SettingItem webDavView() {
    TextField url = createTextField("服务器地址", config.getUrl());
    TextField username = createTextField("账号", config.getUsername());
    TextField password = createTextField("授权码", config.getPassword());

    IconButton backup = NodeHelper.addClass(new IconButton("备份"), "btn");
    backup.setOnAction(e -> doSync(true));
    IconButton restore = NodeHelper.addClass(new IconButton("恢复"), "btn");
    restore.setOnAction(e -> doSync(false));
    HBox box = new HBox(url, username, password, backup, restore);
    box.setSpacing(10);
    return new SettingItem("WebDav配置", box);
  }

  private void doSync(final boolean backup) {
    if (enabledWebDav()) {
      String type = backup ? "备份" : "恢复";
      TaskFactory.create(() -> {
        if (backup) {
          backup();
        } else {
          restore();
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
      Toast.error("请先填写WebDav配置");
    }
  }

  /**
   * 备份
   */
  private void backup() {
    File dir = ResourceManager.BACKUP_DIR;
    FileUtil.del(BACKUP_LOCAL_FILE);
    FileUtil.del(dir);

    File confDir = FileUtil.file(dir, "conf");
    FileUtil.mkdir(confDir);
    // 设置数据
    if (Boolean.TRUE.equals(config.getSetting().get())) {
      File settingFile = ResourceManager.confFile(SettingManager.CONFIG_FILE_NAME);
      FileUtil.copy(settingFile, confDir, true);
    }
    // 文本小说
    if (Boolean.TRUE.equals(config.getBookshelf().get())) {
      List<Book> books = new BookDao().selectAll();
      if (!books.isEmpty()) {
        for (Book book : books) {
          File cover = FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), BookHelper.COVER_NAME);
          File manifest = FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), BookHelper.MANIFEST);
          FileUtil.copy(cover, FileUtil.file(BACKUP_BOOK_DIR, book.getId(), BookHelper.COVER_NAME), true);
          FileUtil.copy(manifest, FileUtil.file(BACKUP_BOOK_DIR, book.getId(), BookHelper.MANIFEST), true);
        }
        FileUtil.writeUtf8String(GsonUtils.toJson(books), FileUtil.file(BACKUP_BOOK_DIR, "book.json"));
      }
    }
    // 有声小说
    if (Boolean.TRUE.equals(config.getAudio().get())) {
      List<AudioBook> books = new AudioBookDao().selectAll();
      if (!books.isEmpty()) {
        for (AudioBook book : books) {
          File cover = FileUtil.file(AudioBookShelfView.CACHE_FOLDER, book.getId(), BookHelper.COVER_NAME);
          File manifest = FileUtil.file(AudioBookShelfView.CACHE_FOLDER, book.getId(), BookHelper.MANIFEST);
          FileUtil.copy(cover, FileUtil.file(BACKUP_AUDIO_DIR, book.getId(), BookHelper.COVER_NAME), true);
          FileUtil.copy(manifest, FileUtil.file(BACKUP_AUDIO_DIR, book.getId(), BookHelper.MANIFEST), true);
        }
        FileUtil.writeUtf8String(GsonUtils.toJson(books), FileUtil.file(BACKUP_AUDIO_DIR, "audio.json"));
      }
    }
    // 规则数据
    if (Boolean.TRUE.equals(config.getRule().get())) {
      File ruleFile = ResourceManager.confFile(RuleManager.RULES_FILE_NAME);
      FileUtil.copy(ruleFile, confDir, true);
    }
    // 压缩文件
    File zip = ZipUtil.zip(dir.getAbsolutePath(), BACKUP_LOCAL_FILE.getAbsolutePath());
    getWebDav().upload(zip);
    FileUtil.del(zip);
    FileUtil.del(dir);
  }

  /**
   * 还原
   */
  private void restore() {
    File dir = ResourceManager.BACKUP_DIR;
    FileUtil.del(BACKUP_LOCAL_FILE);
    FileUtil.del(dir);

    getWebDav().download(BACKUP_LOCAL_FILE);
    if (!BACKUP_LOCAL_FILE.exists()) {
      throw new IORuntimeException("云端备份不存在");
    }
    ZipUtil.unzip(BACKUP_LOCAL_FILE);
    File confDir = FileUtil.file(dir, "conf");
    // 设置数据
    File settingFile = FileUtil.file(confDir, SettingManager.CONFIG_FILE_NAME);
    if (settingFile.exists()) {
      FileUtil.copy(settingFile, ResourceManager.CONF_DIR, true);
    }
    // 规则数据
    File ruleFile = FileUtil.file(confDir, RuleManager.RULES_FILE_NAME);
    if (ruleFile.exists()) {
      FileUtil.copy(ruleFile, ResourceManager.CONF_DIR, true);
      AppContext.getView(RuleManagerView.class).importRule(ruleFile);
    }
    // 小说数据
    if (BACKUP_BOOK_DIR.exists()) {
      File file = FileUtil.file(BACKUP_BOOK_DIR, "book.json");
      String string = FileUtil.readUtf8String(file);
      List<Book> books = GsonUtils.me().fromJson(string, new TypeToken<List<Book>>() {
      }.getType());
      FileUtil.del(file);
      BookDao bookDao = new BookDao();
      for (Book book : books) {
        try {
          bookDao.getDao().createOrUpdate(book);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (!books.isEmpty()) {
        FileUtil.copy(BACKUP_BOOK_DIR, FictionBookshelfView.CACHE_FOLDER.getParentFile(), true);
      }
    }
    if (BACKUP_AUDIO_DIR.exists()) {
      File file = FileUtil.file(BACKUP_AUDIO_DIR, "audio.json");
      String string = FileUtil.readUtf8String(file);
      List<AudioBook> books = GsonUtils.me().fromJson(string, new TypeToken<List<AudioBook>>() {
      }.getType());
      AudioBookDao bookDao = new AudioBookDao();
      FileUtil.del(file);
      for (AudioBook book : books) {
        try {
          bookDao.getDao().createOrUpdate(book);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (!books.isEmpty()) {
        FileUtil.copy(BACKUP_AUDIO_DIR, AudioBookShelfView.CACHE_FOLDER.getParentFile(), true);
      }
    }
    // 删除备份文件
    FileUtil.del(BACKUP_LOCAL_FILE);
    FileUtil.del(dir);
  }

  /**
   * 创建 WebDev，默认文件夹 /uncle-novel
   *
   * @return WebDav
   */
  private WebDav getWebDav() {
    return WebDav.createDefault()
      .setUrl(config.getUrl().get())
      .setPassword(config.getPassword().get())
      .setUsername(config.getUsername().get())
      .child(WEB_DAV_BACKUP_FILE_NAME);
  }

  private boolean enabledWebDav() {
    return StringUtils.isNotBlank(this.config.getPassword().get()) || StringUtils.isNotBlank(this.config.getUrl().get()) || StringUtils.isNotBlank(this.config.getUsername().get());
  }
}
