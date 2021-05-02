package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import javafx.beans.InvalidationListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @date 2021/4/30 11:54
 */
public class DownloadActionTableCell extends TableCell<SpiderWrapper, SpiderWrapper> {

  private final HBox box;
  private final Icon start;
  private final Icon pause;
  private final Icon stop;
  private final Icon retry;
  private final InvalidationListener stateListener;
  private SpiderWrapper item;

  public DownloadActionTableCell() {
    setGraphic(null);
    this.start = NodeHelper.addClass(new Icon(IconFont.START));
    this.start.setTooltip(new Tooltip("开始"));
    this.pause = NodeHelper.addClass(new Icon(IconFont.PAUSE));
    this.pause.setTooltip(new Tooltip("暂停"));
    this.retry = NodeHelper.addClass(new Icon(IconFont.REFRESH));
    this.retry.setTooltip(new Tooltip("重试失败章节"));
    this.stop = NodeHelper.addClass(new Icon(IconFont.DELETE), "delete");
    stop.setTooltip(new Tooltip("停止"));
    this.box = NodeHelper.addClass(new HBox(start, stop), "action-cell");
    start.setOnMouseClicked(event -> {
      item.run();
      Toast.success("启动成功");
    });
    pause.setOnMouseClicked(e -> {
      item.pause();
      Toast.success("已暂停");
    });
    retry.setOnMouseClicked(e -> item.retry());
    stop.setOnMouseClicked(event -> {
      item.stop();
      getTableView().getItems().remove(item);
      Toast.success("已丢弃任务");
    });
    // 状态监听
    this.stateListener = e -> setState();
  }

  @Override
  protected void updateItem(SpiderWrapper item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      // 移除旧的监听
      if (this.item != null) {
        this.item.getState().removeListener(stateListener);
      }
      // 添加新的监听
      this.item = item;
      setState();
      item.getState().addListener(stateListener);
      setGraphic(box);
    }
  }

  private void setState() {
    Integer state = item.getState().get();
    switch (state) {
      case Spider.FINISHED:
        box.getChildren().setAll(retry, stop);
        break;
      case Spider.RUNNING:
        box.getChildren().setAll(pause, stop);
        break;
      case Spider.PAUSED:
        box.getChildren().setAll(start, stop);
        break;
      case SpiderWrapper.TRANSCODE:
        box.getChildren().setAll(stop);
        break;
      default:
    }
  }
}
