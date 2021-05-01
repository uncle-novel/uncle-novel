package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @date 2021/5/1 22:35
 */
public class ProgressBarTableCell extends TableCell<SpiderWrapper, SpiderWrapper> {

  private final ProgressBar progressBar;
  private final Label progressText;
  private final HBox box;

  public ProgressBarTableCell() {
    this.progressBar = new JFXProgressBar();
    this.progressText = new Label();
    this.progressText.setMinWidth(100);
    this.box = new HBox(progressBar, progressText);
    box.setSpacing(5);
    box.setAlignment(Pos.CENTER_LEFT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateItem(SpiderWrapper item, boolean empty) {
    super.updateItem(item, empty);

    if (empty) {
      setGraphic(null);
    } else {
      progressBar.progressProperty().unbind();
      progressText.textProperty().unbind();
      progressBar.progressProperty().bind(item.getProgress());
      progressText.textProperty().bind(item.getProgressText());
      setGraphic(box);
    }
  }
}
