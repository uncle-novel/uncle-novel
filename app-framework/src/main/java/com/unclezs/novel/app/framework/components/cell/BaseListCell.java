package com.unclezs.novel.app.framework.components.cell;

import javafx.scene.control.ListCell;

/**
 * listCell
 *
 * @author blog.unclezs.com
 * @since 2021/5/5 19:52
 */
public abstract class BaseListCell<T> extends ListCell<T> {

  @Override
  protected final void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      updateItem(item);
    }
  }

  /**
   * 更新item
   *
   * @param item T
   */
  protected abstract void updateItem(T item);
}
