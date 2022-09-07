package com.unclezs.novel.app.framework.components;

import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.control.Label;

/**
 * @author blog.unclezs.com
 * @since 2021/4/17 2:26
 */
public class Tag extends Label {

  public Tag() {
    this(null);
  }

  public Tag(String text) {
    super(text);
    NodeHelper.addClass(this, "tag");
  }
}
