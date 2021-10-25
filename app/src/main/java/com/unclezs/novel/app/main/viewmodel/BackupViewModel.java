package com.unclezs.novel.app.main.viewmodel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ZipUtil;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.main.core.webdav.WebDav;
import com.unclezs.novel.app.main.db.beans.AudioBook;
import com.unclezs.novel.app.main.db.beans.Book;
import com.unclezs.novel.app.main.db.beans.SearchEngine;
import com.unclezs.novel.app.main.db.dao.AudioBookDao;
import com.unclezs.novel.app.main.db.dao.BookDao;
import com.unclezs.novel.app.main.db.dao.SearchEngineDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.config.BackupConfig;
import com.unclezs.novel.app.main.util.BookHelper;
import com.unclezs.novel.app.main.views.home.AudioBookShelfView;
import com.unclezs.novel.app.main.views.home.FictionBookshelfView;
import com.unclezs.novel.app.main.views.home.RuleManagerView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/6/1 9:57
 */
@Slf4j
public class BackupViewModel {

  public static final String WEB_DAV_BACKUP_FILE_NAME = "backup.zip";
  /**
   * 备份数据本地临时文件
   */
  public static final File BACKUP_LOCAL_FILE = FileUtil.file(ResourceManager.WORK_DIR, WEB_DAV_BACKUP_FILE_NAME);
  /**
   * 文本小说备份目录
   */
  public static final File BACKUP_BOOK_DIR =
    FileUtil.file(ResourceManager.BACKUP_DIR, FictionBookshelfView.CACHE_FOLDER_NAME);
  /**
   * 有声小说备份目录
   */
  public static final File BACKUP_AUDIO_DIR =
    FileUtil.file(ResourceManager.BACKUP_DIR, AudioBookShelfView.CACHE_FOLDER_NAME);
  /**
   * 文本小说备份数据文件名
   */
  public static final String BACKUP_BOOK_NAME = "book.json";
  /**
   * 有声小说备份数据文件名
   */
  public static final String BACKUP_AUDIO_NAME = "audio.json";
  /**
   * 配置相关备份文件夹
   */
  public static final String BACKUP_CONF_NAME = "conf";
  public static final String BACKUP_NAME_SEARCH_ENGINES = "searchEngines.json";
  @Getter
  private final BackupConfig config = SettingManager.manager().getBackup();

  /**
   * 备份
   * <p>
   * 逻辑：
   * <li>1. 将所有数据复制一份到BACKUP_DIR</li>
   * <li>2. zip BACKUP_DIR</li>
   * <li>3. 将zip文件上传webdav</li>
   * <li>4. 删除BACKUP_DIR内容</li>
   * <li>5. 移动zip到BACKUP_DIR</li>
   */
  public void backup() {
    File backupDir = ResourceManager.BACKUP_DIR;
    // 删除旧的
    FileUtil.del(BACKUP_LOCAL_FILE);
    FileUtil.del(backupDir);
    // 创建备份
    File backupZip = createBackup(backupDir);
    // 上传webDav
    getWebDav().upload(backupZip);
    FileUtil.del(backupDir);
    // 移动备份压缩包到备份目录
    FileUtil.move(backupZip, FileUtil.file(backupDir, backupZip.getName()), true);
  }

  /**
   * 还原
   */
  public void restore() {
    File dir = ResourceManager.BACKUP_DIR;
    FileUtil.del(BACKUP_LOCAL_FILE);
    FileUtil.del(dir);

    getWebDav().download(BACKUP_LOCAL_FILE);
    if (!BACKUP_LOCAL_FILE.exists()) {
      throw new IORuntimeException("云端备份不存在");
    }
    restoreBackup(BACKUP_LOCAL_FILE);
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

  public boolean enabledWebDav() {
    return !CharSequenceUtil.hasBlank(this.config.getPassword().get(), this.config.getUrl().get(),
      this.config.getUsername().get());
  }

  /**
   * 创建备份
   *
   * @param backupDir 备份到的文件夹
   * @return 备份文件压缩包
   */
  public File createBackup(File backupDir) {
    File confDir = FileUtil.file(backupDir, BACKUP_CONF_NAME);
    FileUtil.mkdir(confDir);
    // 文本小说
    if (Boolean.TRUE.equals(config.getBookshelf().get())) {
      List<Book> books = new BookDao().selectAll();
      if (!books.isEmpty()) {
        for (Book book : books) {
          copy(FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), BookHelper.COVER_NAME),
            FileUtil.file(BACKUP_BOOK_DIR, book.getId(), BookHelper.COVER_NAME));
          copy(FileUtil.file(FictionBookshelfView.CACHE_FOLDER, book.getId(), BookHelper.MANIFEST),
            FileUtil.file(BACKUP_BOOK_DIR, book.getId(), BookHelper.MANIFEST));
        }
        FileUtil.writeUtf8String(GsonUtils.toJson(books), FileUtil.file(BACKUP_BOOK_DIR, BACKUP_BOOK_NAME));
      }
    }
    // 有声小说
    if (Boolean.TRUE.equals(config.getAudio().get())) {
      List<AudioBook> books = new AudioBookDao().selectAll();
      if (!books.isEmpty()) {
        for (AudioBook book : books) {
          copy(FileUtil.file(AudioBookShelfView.CACHE_FOLDER, book.getId(), BookHelper.COVER_NAME),
            FileUtil.file(BACKUP_AUDIO_DIR, book.getId(), BookHelper.COVER_NAME));
          copy(FileUtil.file(AudioBookShelfView.CACHE_FOLDER, book.getId(), BookHelper.MANIFEST),
            FileUtil.file(BACKUP_AUDIO_DIR, book.getId(), BookHelper.MANIFEST));
        }
        FileUtil.writeUtf8String(GsonUtils.toJson(books), FileUtil.file(BACKUP_AUDIO_DIR, BACKUP_AUDIO_NAME));
      }
    }
    // 规则数据
    if (Boolean.TRUE.equals(config.getRule().get())) {
      File ruleFile = ResourceManager.confFile(RuleManager.RULES_FILE_NAME);
      copy(ruleFile, confDir);
    }
    // 搜索引擎
    if (Boolean.TRUE.equals(config.getSearchEngine().get())) {
      FileUtil.writeUtf8String(GsonUtils.toJson(SearchEngineDao.me().all()),
        FileUtil.file(backupDir, BACKUP_NAME_SEARCH_ENGINES));
    }
    // 创建压缩文件
    return ZipUtil.zip(backupDir.getAbsolutePath(), BACKUP_LOCAL_FILE.getAbsolutePath());
  }

  /**
   * 从备份压缩包中恢复数据
   *
   * @param backupZip 备份压缩包
   */
  public void restoreBackup(File backupZip) {
    File backupDir = ZipUtil.unzip(backupZip);
    File confDir = FileUtil.file(backupDir, BACKUP_CONF_NAME);
    // 规则数据
    File ruleFile = FileUtil.file(confDir, RuleManager.RULES_FILE_NAME);
    if (ruleFile.exists()) {
      copy(ruleFile, ResourceManager.CONF_DIR);
      AppContext.getView(RuleManagerView.class).importRule(ruleFile);
    }
    // 小说数据
    if (BACKUP_BOOK_DIR.exists()) {
      File bookFile = FileUtil.file(BACKUP_BOOK_DIR, BACKUP_BOOK_NAME);
      List<Book> books = GsonUtils.me().fromJson(FileUtil.readUtf8String(bookFile), new TypeToken<List<Book>>() {
      }.getType());
      FileUtil.del(bookFile);
      FictionBookshelfView bookshelf = AppContext.getView(FictionBookshelfView.class);
      books.forEach(bookshelf::addOrUpdateBook);
      if (!books.isEmpty()) {
        copy(BACKUP_BOOK_DIR, FictionBookshelfView.CACHE_FOLDER.getParentFile());
      }
    }
    // 有声小说
    if (BACKUP_AUDIO_DIR.exists()) {
      File file = FileUtil.file(BACKUP_AUDIO_DIR, BACKUP_AUDIO_NAME);
      String string = FileUtil.readUtf8String(file);
      List<AudioBook> books = GsonUtils.me().fromJson(string, new TypeToken<List<AudioBook>>() {
      }.getType());
      FileUtil.del(file);
      AudioBookShelfView bookshelf = AppContext.getView(AudioBookShelfView.class);
      books.forEach(bookshelf::addOrUpdateBook);
      if (!books.isEmpty()) {
        copy(BACKUP_AUDIO_DIR, AudioBookShelfView.CACHE_FOLDER.getParentFile());
      }
    }
    // 搜索引擎
    File searchEngineJsonFile = FileUtil.file(backupDir, BACKUP_NAME_SEARCH_ENGINES);
    if (searchEngineJsonFile.exists()) {
      List<SearchEngine> searchEngines =
        GsonUtils.me().fromJson(FileUtil.readUtf8String(searchEngineJsonFile), new TypeToken<List<SearchEngine>>() {
        }.getType());
      searchEngines.forEach(searchEngine -> SearchEngineDao.me().createOrUpdate(searchEngine));
      SearchEngineDao.me().all().setAll(SearchEngineDao.me().selectAll());
    }
  }

  private void copy(File src, File target) {
    if (!src.exists()) {
      log.warn("文件不存在，忽略：{}", src);
      return;
    }
    FileUtil.copy(src, target, true);
  }
}
