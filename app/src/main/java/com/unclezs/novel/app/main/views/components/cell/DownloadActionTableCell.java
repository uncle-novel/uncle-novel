package com.unclezs.novel.app.main.views.components.cell;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.core.spider.SpiderWrapper;
import com.unclezs.novel.app.main.util.EbookUtils;
import com.unclezs.novel.app.main.views.home.DownloadManagerView;
import javafx.beans.InvalidationListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.io.File;

/**
 * @author blog.unclezs.com
 * @since 2021/4/30 11:54
 */
public class DownloadActionTableCell extends TableCell<SpiderWrapper, SpiderWrapper> {

  private final HBox box;
  private final Icon start;
  private final Icon pause;
  private final Icon stop;
  private final Icon retry;
  private final Icon folder;
  private final Icon save;
  private final InvalidationListener stateListener;
  private SpiderWrapper item;

  public DownloadActionTableCell() {
    NodeHelper.addClass(this, "download-action-cell");
    setGraphic(null);
    this.start = NodeHelper.addClass(new Icon(IconFont.RUN));
    this.start.setTooltip(new Tooltip("开始"));
    this.pause = NodeHelper.addClass(new Icon(IconFont.PAUSE_RUN));
    this.pause.setTooltip(new Tooltip("暂停"));
    this.retry = NodeHelper.addClass(new Icon(IconFont.RETRY));
    this.retry.setTooltip(new Tooltip("重试失败章节"));
    this.save = NodeHelper.addClass(new Icon(IconFont.SAVE));
    this.save.setTooltip(new Tooltip("忽略错误"));
    this.stop = NodeHelper.addClass(new Icon(IconFont.STOP), "delete");
    stop.setTooltip(new Tooltip("停止"));
    this.folder = NodeHelper.addClass(new Icon(IconFont.FOLDER));
    folder.setTooltip(new Tooltip("文件夹"));
    this.box = NodeHelper.addClass(new HBox(start, stop, folder), "action-cell", "download-action");
    start.setOnMouseClicked(event -> {
      item.runTask();
      Toast.success("启动");
    });
    pause.setOnMouseClicked(e -> {
      item.pause();
      Toast.success("暂停");
    });
    retry.setOnMouseClicked(e -> item.retry());
    save.setOnMouseClicked(e -> ModalBox.confirm(confirmSave -> {
      if (Boolean.TRUE.equals(confirmSave)) {
        item.save();
      }
    }).title("确定忽略错误吗").message("忽略错误可能出现章节内容不完整的情况").show());
    stop.setOnMouseClicked(event -> {
      String savePath = item.getSpider().getSavePath();
      String name = StringUtils.removeInvalidSymbol(item.getName());
      item.stop();
      getTableView().getItems().remove(item);
      // 删除临时文件
      if (StringUtils.isNotBlank(savePath) && StringUtils.isNotBlank(name)) {
        FileUtil.del(FileUtil.file(savePath, name));
        FileUtil.del(FileUtil.file(savePath, name + EbookUtils.EBOOK_TMP_SUFFIX));
      }
      // 删除缓存
      FileUtil.del(FileUtil.file(DownloadManagerView.TMP_DIR, item.getId()));
    });
    folder.setOnMouseClicked(e -> DesktopUtils.openDir(new File(item.getSpider().getSavePath())));
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
    pause.setDisable(false);
    switch (state) {
      case Spider.COMPLETE:
        box.getChildren().setAll(retry, stop, folder, save);
        break;
      case Spider.RUNNING:
      case SpiderWrapper.WAIT_RUN:
        box.getChildren().setAll(pause, stop, folder);
        break;
      case Spider.PIPELINE:
        box.getChildren().setAll(pause, stop, folder);
        pause.setDisable(true);
        break;
      case Spider.PAUSED:
      default:
        box.getChildren().setAll(start, stop, folder);
    }
  }
}
