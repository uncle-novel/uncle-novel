package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.app.main.model.ChapterProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

/**
 * 章节节点
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 15:41
 */
public class ChapterListCell extends ListCell<ChapterProperty> {

  private final CheckBox checkBox = new JFXCheckBox();
  private ObservableValue<Boolean> booleanProperty;

  @Override
  protected void updateItem(ChapterProperty item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      setGraphic(checkBox);
      if (booleanProperty != null) {
        checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
      }
      booleanProperty = item.selectedProperty();
      if (booleanProperty != null) {
        checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
      }
      setText(item.getChapter().getName());
    }
  }
}
