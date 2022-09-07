package com.unclezs.novel.app.main.model.config;

import com.unclezs.novel.app.main.manager.ResourceManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

/**
 * 下载配置
 *
 * @author blog.unclezs.com
 * @since 2021/4/30 13:47
 */
@Data
public class DownloadConfig {

  /**
   * 下载目录
   */
  private ObjectProperty<String> folder = new SimpleObjectProperty<>(ResourceManager.DOWNLOAD_DIR.getAbsolutePath());
  /**
   * 下载线程数量
   */
  private ObjectProperty<Integer> threadNum = new SimpleObjectProperty<>(1);
  /**
   * 任务数量
   */
  private ObjectProperty<Integer> taskNum = new SimpleObjectProperty<>(1);
  /**
   * 重试次数
   */
  private ObjectProperty<Integer> retryNum = new SimpleObjectProperty<>(0);
  /**
   * true则 每章单独一个文件，不合并
   */
  private ObjectProperty<Boolean> volume = new SimpleObjectProperty<>(true);
  /**
   * 下载类型
   */
  private ObjectProperty<Boolean> mobi = new SimpleObjectProperty<>(false);
  private ObjectProperty<Boolean> txt = new SimpleObjectProperty<>(true);
  private ObjectProperty<Boolean> epub = new SimpleObjectProperty<>(false);
}
