package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.util.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 23:10
 */
@Data
public class BackupConfig {

  /**
   * webdav配置
   */
  private ObjectProperty<String> url = new SimpleObjectProperty<>(StringUtils.EMPTY);
  private ObjectProperty<String> username = new SimpleObjectProperty<>(StringUtils.EMPTY);
  private ObjectProperty<String> password = new SimpleObjectProperty<>(StringUtils.EMPTY);

  /**
   * 备份内容
   */
  private ObjectProperty<Boolean> bookshelf = new SimpleObjectProperty<>(true);
  private ObjectProperty<Boolean> audio = new SimpleObjectProperty<>(true);
  private ObjectProperty<Boolean> rule = new SimpleObjectProperty<>(true);
  private ObjectProperty<Boolean> read = new SimpleObjectProperty<>(true);
  private ObjectProperty<Boolean> setting = new SimpleObjectProperty<>(true);
}
