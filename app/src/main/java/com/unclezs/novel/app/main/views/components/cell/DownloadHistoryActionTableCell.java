package com.unclezs.novel.app.main.views.components.cell;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.db.beans.DownloadHistory;
import java.io.File;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

/**
 * @author blog.unclezs.com
 * @date 2021/5/5 13:20
 */
public class DownloadHistoryActionTableCell extends TableCell<DownloadHistory, DownloadHistory> {

  private final HBox box;
  private DownloadHistory item;

  public DownloadHistoryActionTableCell() {
    Icon open = NodeHelper.addClass(new Icon(IconFont.FOLDER));
    open.setTooltip(new Tooltip("打开"));
    Icon clear = NodeHelper.addClass(new Icon(IconFont.CLEAR));
    clear.setTooltip(new Tooltip("清除"));
    this.box = NodeHelper.addClass(new HBox(open, clear), "action-cell");
    open.setOnMouseClicked(e -> {
      File dir = FileUtil.file(item.getPath());
      if (FileUtil.exist(dir)) {
        DesktopUtils.openDir(dir);
      }
    });
    clear.setOnMouseClicked(e -> getTableView().getItems().remove(item));
  }

  @Override
  protected void updateItem(DownloadHistory item, boolean empty) {
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
