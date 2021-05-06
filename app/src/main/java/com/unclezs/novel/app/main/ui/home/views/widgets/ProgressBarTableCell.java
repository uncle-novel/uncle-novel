package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author blog.unclezs.com
 * @date 2021/5/1 22:35
 */
public class ProgressBarTableCell extends TableCell<SpiderWrapper, SpiderWrapper> {

  private final JFXProgressBar progressBar;
  private final Label progressText;
  private final Label state;
  private final Label error;
  private final VBox cell;
  private final InvalidationListener stateListener;
  private SpiderWrapper item;

  public ProgressBarTableCell() {
    this.progressBar = new JFXProgressBar();
    this.progressBar.setPrefWidth(Double.MAX_VALUE);
    this.progressText = new Label();
    HBox progressTextBox = new HBox(progressText);
    progressTextBox.setAlignment(Pos.CENTER_RIGHT);
    progressTextBox.setPrefWidth(90);
    this.state = new Label("等待中...");
    state.setPrefWidth(90);
    this.error = new Label();
    BorderPane.setAlignment(error, Pos.CENTER);
    BorderPane box = new BorderPane(error, null, progressTextBox, null, state);
    NodeHelper.addClass(box, "progress-info-box");
    this.cell = new VBox(progressBar, box);
    this.cell.setAlignment(Pos.CENTER_LEFT);
    NodeHelper.addClass(cell, "progress-cell");
    // 状态监听
    this.stateListener = e -> setState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateItem(SpiderWrapper item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      // 移除旧的监听
      if (this.item != null) {
        this.item.getState().removeListener(stateListener);
      }
      this.item = item;
      setState();
      this.item.getState().addListener(stateListener);

      progressBar.progressProperty().unbind();
      error.textProperty().unbind();
      progressText.textProperty().unbind();

      progressBar.progressProperty().bind(item.getProgress());
      progressText.textProperty().bind(item.getProgressText());
      error.textProperty().bind(Bindings.createStringBinding(() -> {
        if (item.getErrorCount().get() > 0) {
          return String.format("%d失败", item.getErrorCount().get());
        }
        return null;
      }, item.getErrorCount()));
      setGraphic(cell);
    }
  }

  private void setState() {
    Integer spiderState = item.getState().get();
    switch (spiderState) {
      case Spider.RUNNING:
        state.setText("下载中...");
        break;
      case Spider.PAUSED:
        state.setText("暂停中...");
        break;
      case Spider.COMPLETE:
        state.setText("等待手动重试...");
        break;
      case SpiderWrapper.WAIT_RUN:
        state.setText("等待中...");
        break;
      case Spider.PIPELINE:
        state.setText("转码中...");
        break;
      default:
    }
  }
}
