package com.unclezs.novel.app.framework.components.properties;

import javafx.beans.DefaultProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于fxml的字符串
 *
 * @author blog.unclezs.com
 * @since 2021/4/16 16:04
 */
@Data
@NoArgsConstructor
@DefaultProperty("value")
public class Str {

  /**
   * 字符串值
   */
  private String value;

}
