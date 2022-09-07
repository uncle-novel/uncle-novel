package com.unclezs.novel.app.main.manager;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.framework.serialize.PropertyJsonSerializer;
import com.unclezs.novel.app.main.model.config.BackupConfig;
import com.unclezs.novel.app.main.model.config.BasicConfig;
import com.unclezs.novel.app.main.model.config.BookShelfConfig;
import com.unclezs.novel.app.main.model.config.DownloadConfig;
import com.unclezs.novel.app.main.model.config.HotKeyConfig;
import com.unclezs.novel.app.main.model.config.ProxyConfig;
import com.unclezs.novel.app.main.model.config.ReaderConfig;
import com.unclezs.novel.app.main.util.DebugUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Locale;

/**
 * 设置管理器
 *
 * @author blog.unclezs.com
 * @since 2021/4/28 12:22
 */
@Setter
@Getter
public class SettingManager {

  /**
   * 配置文件名字
   */
  public static final String CONFIG_FILE_NAME = "conf.json";
  /**
   * 初始化默认的设置管理器
   */
  private static SettingManager manager;
  private String version = "5.0";
  /**
   * 基本设置
   */
  private BasicConfig basic = new BasicConfig();
  /**
   * 下载配置
   */
  private DownloadConfig download = new DownloadConfig();
  /**
   * 阅读器配置
   */
  private ReaderConfig reader = new ReaderConfig();
  /**
   * 书架配置
   */
  private BookShelfConfig bookShelf = new BookShelfConfig();
  /**
   * 备份配置
   */
  private BackupConfig backup = new BackupConfig();
  /**
   * 网络代理
   */
  private ProxyConfig proxy = new ProxyConfig();
  /**
   * 快捷键
   */
  private HotKeyConfig hotkey = new HotKeyConfig();
  /**
   * 调试模式
   */
  private ObjectProperty<Boolean> debug = new SimpleObjectProperty<>(false);

  /**
   * 获取管理器
   *
   * @return manager
   */
  public static SettingManager manager() {
    return manager;
  }

  /**
   * 从配置文件加载
   */
  public static void init() {
    File confFile = ResourceManager.confFile(CONFIG_FILE_NAME);
    if (confFile.exists()) {
      String confJson = FileUtil.readUtf8String(confFile);
      manager = PropertyJsonSerializer.fromJson(confJson, SettingManager.class);
    } else {
      manager = new SettingManager();
    }
    // 初始化默认语言
    Locale.setDefault(LanguageManager.locale(manager.basic.getLang().getValue()));
    // 初始化代理
    ProxyConfig.initHttpProxy();
    // 日志初始化
    DebugUtils.debug(manager().getDebug().get());
  }

  /**
   * 持久化到配置文件
   */
  public static void save() {
    File confFile = ResourceManager.confFile(CONFIG_FILE_NAME);
    FileUtil.writeUtf8String(PropertyJsonSerializer.toJson(manager), confFile);
  }
}
