package com.unclezs.novel.app.main.manager;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.framework.serialize.PropertyJsonSerializer;
import com.unclezs.novel.app.main.model.DownloadConfig;
import com.unclezs.novel.app.main.model.Proxy;
import com.unclezs.novel.app.main.model.SearchEngine;
import java.io.File;
import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @date 2021/4/28 12:22
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
  /**
   * 系统语言，读取默认
   */
  private ObjectProperty<String> lang = new SimpleObjectProperty<>(LanguageManager.name(Locale.getDefault()));
  /**
   * 下载配置
   */
  private DownloadConfig download = new DownloadConfig();
  /**
   * 全网搜书的搜索引擎配置
   */
  private ObservableList<SearchEngine> searchEngines = FXCollections.observableArrayList();
  /**
   * 网络代理
   */
  private Proxy proxy = new Proxy();

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
      resetDefault();
    }
    // 初始化默认语言
    Locale.setDefault(LanguageManager.locale(manager.getLang().getValue()));
    // 初始化代理
    Proxy.initHttpProxy();
  }

  /**
   * 持久化到配置文件
   */
  public static void save() {
    File confFile = ResourceManager.confFile(CONFIG_FILE_NAME);
    FileUtil.writeUtf8String(PropertyJsonSerializer.toJson(manager), confFile);
  }

  public static void resetDefault() {
    manager().getSearchEngines().setAll(SearchEngine.getDefault());
  }
}
