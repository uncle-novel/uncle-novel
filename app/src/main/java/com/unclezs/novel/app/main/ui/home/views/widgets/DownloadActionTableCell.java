package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @date 2021/4/30 11:54
 */
public class DownloadActionTableCell extends TableCell<SpiderWrapper, SpiderWrapper> {

  private final HBox box;
  private final IconButton start;
  private final IconButton pause;
  private SpiderWrapper item;

  public DownloadActionTableCell() {
    setGraphic(null);
    this.start = NodeHelper.addClass(new IconButton(IconFont.START, "开始"));
    this.pause = NodeHelper.addClass(new IconButton(IconFont.PAUSE, "暂停"));
    IconButton stop = NodeHelper.addClass(new IconButton(IconFont.DELETE, "停止"), "delete");
    this.box = NodeHelper.addClass(new HBox(start, stop), "action-cell");
    start.setOnMouseClicked(event -> {
      item.run();
      Toast.success("启动成功");
      box.getChildren().set(0, pause);
    });
    pause.setOnMouseClicked(e -> {
      item.pause();
      Toast.success("已暂停");
      box.getChildren().set(0, start);
    });
    stop.setOnMouseClicked(event -> {
      item.stop();
      getTableView().getItems().remove(item);
      Toast.success("已丢弃任务");
    });
  }

  @Override
  protected void updateItem(SpiderWrapper item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      this.item = item;
      if (item.getSpider().state() == Spider.RUNNING) {
        box.getChildren().set(0, pause);
      } else {
        box.getChildren().set(0, start);
      }
      setGraphic(box);
    }
  }
}
