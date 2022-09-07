package com.unclezs.novel.app.main.views.components.cell;

import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.function.ObjIntConsumer;

/**
 * tableview的按钮列
 *
 * @author blog.unclezs.com
 * @since 2021/4/20 23:20
 */
public class ActionButtonTableCell<T> extends TableCell<T, T> {

  private final HBox box;
  private T item;

  public ActionButtonTableCell() {
    this.box = NodeHelper.addClass(new HBox(), "action-cell");
  }

  public ActionButtonTableCell(ObjIntConsumer<T> onEdit, ObjIntConsumer<T> onDelete) {
    this();
    setGraphic(null);
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

  /**
   * 添加action
   *
   * @param button   按钮
   * @param onAction 触发回调
   */
  public void addAction(IconButton button, ObjIntConsumer<T> onAction) {
    box.getChildren().add(button);
    button.setOnMouseClicked(event -> onAction.accept(item, getTableRow().getIndex()));
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
