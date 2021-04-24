package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXCheckBox;
import java.util.function.ObjIntConsumer;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

/**
 * @author blog.unclezs.com
 * @date 2021/4/20 22:53
 */
public class CheckBoxTableCell<S> extends TableCell<S, Boolean> {

  private final CheckBox checkBox = new JFXCheckBox();

  public CheckBoxTableCell(ObjIntConsumer<Boolean> onChange) {
    checkBox.selectedProperty().addListener(e -> onChange.accept(checkBox.isSelected(), getTableRow().getIndex()));
  }

  @Override
  protected void updateItem(Boolean item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setText(null);
      setGraphic(null);
    } else {
      checkBox.setSelected(item);
      setGraphic(checkBox);
    }
  }

}
