package com.unclezs.novel.app.framework.components.properties;

import javafx.beans.DefaultProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于fxml的字符串
 *
 * @author blog.unclezs.com
 * @date 2021/4/16 16:04
 */
@DefaultProperty("value")
public class Str {

  @Getter
  @Setter
  private String value;
}
