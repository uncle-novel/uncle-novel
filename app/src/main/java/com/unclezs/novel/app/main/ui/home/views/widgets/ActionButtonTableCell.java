package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import java.util.function.ObjIntConsumer;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @date 2021/4/20 23:20
 */
public class ActionButtonTableCell<T> extends TableCell<T, T> {

  private final HBox box;
  private T item;

  public ActionButtonTableCell(ObjIntConsumer<T> onEdit, ObjIntConsumer<T> onDelete) {
    setGraphic(null);
    this.box = NodeHelper.addClass(new HBox(), "action-cell");
    if (onEdit != null) {
      Icon edit = NodeHelper.addClass(new Icon(IconFont.EDIT), "edit");
      box.getChildren().add(edit);
      edit.setOnMouseClicked(event -> onEdit.accept(item, getTableRow().getIndex()));
    }
    if (onDelete != null) {
      Icon delete = NodeHelper.addClass(new Icon(IconFont.DELETE), "delete");
      box.getChildren().add(delete);
      delete.setOnMouseClicked(event -> onDelete.accept(item, getTableRow().getIndex()));
    }
  }

  @Override
  protected void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      this.item = item;
      setGraphic(box);
    }
  }
}
