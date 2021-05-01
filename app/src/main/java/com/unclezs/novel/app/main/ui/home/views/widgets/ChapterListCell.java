package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXCheckBox;
import com.unclezs.novel.app.main.model.ChapterWrapper;
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
public class ChapterListCell extends ListCell<ChapterWrapper> {

  private final CheckBox checkBox = new JFXCheckBox();
  private ObservableValue<Boolean> booleanProperty;

  public ChapterListCell() {
//    checkBox.selectedProperty().addListener(e -> {
//      // 防止n平方次遍历
//      if (getListView().getUserData() == null) {
//        getListView().setUserData(checkBox);
//        getListView().getSelectionModel().getSelectedItems().forEach(selectedItem -> {
//          if (!Objects.equals(checkBox.isSelected(), selectedItem.isSelected())) {
//            selectedItem.setSelected(checkBox.isSelected());
//          }
//        });
//        getListView().setUserData(null);
//      }
//    });
  }

  @Override
  protected void updateItem(ChapterWrapper item, boolean empty) {
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
