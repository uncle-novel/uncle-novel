package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.app.framework.components.cell.BaseListCell;
import com.unclezs.novel.app.main.model.ChapterWrapper;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

/**
 * 章节节点
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 15:41
 */
public class ChapterListCell extends BaseListCell<ChapterWrapper> {

  private final CheckBox checkBox = new JFXCheckBox();
  private ObservableValue<Boolean> booleanProperty;

  public ChapterListCell() {
    checkBox.setOnAction(e -> {
      // 防止n平方次遍历
      if (getListView().getUserData() == null) {
        getListView().setUserData(checkBox);
        getListView().getSelectionModel().getSelectedItems().forEach(selectedItem -> {
          if (!Objects.equals(checkBox.isSelected(), selectedItem.isSelected())) {
            selectedItem.setSelected(checkBox.isSelected());
          }
        });
        getListView().setUserData(null);
      }
    });
  }

  @Override
  protected void updateItem(ChapterWrapper item) {
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
