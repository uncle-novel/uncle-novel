package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.unclezs.novel.app.framework.components.Tag;
import javafx.scene.control.TableCell;

/**
 * 表格标签列
 *
 * @author blog.unclezs.com
 * @date 2021/5/2 17:00
 */
public class TagTableCell<S> extends TableCell<S, String> {

  private final Tag tag;

  public TagTableCell() {
    this.tag = new Tag();
  }

  @Override
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      tag.setText(item);
      setGraphic(tag);
    }
  }
}
