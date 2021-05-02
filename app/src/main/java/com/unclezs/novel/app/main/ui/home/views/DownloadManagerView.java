package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.io.FileUtil;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.DownloadBundle;
import com.unclezs.novel.app.main.model.DownloadConfig;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import com.unclezs.novel.app.main.ui.home.views.widgets.DownloadActionTableCell;
import com.unclezs.novel.app.main.ui.home.views.widgets.ProgressBarTableCell;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/4/25 9:40
 */
@Slf4j
@FxView(fxml = "/layout/home/views/download-manager.fxml")
@EqualsAndHashCode(callSuper = true)
public class DownloadManagerView extends SidebarView<StackPane> {

  /**
   * 要下载的小说
   */
  public static final String BUNDLE_DOWNLOAD_KEY = "bundle_download_key";
  @FXML
  private VBox tasksPanel;
  @FXML
  private TableView<SpiderWrapper> tasksTable;
  @FXML
  private StackPane container;


  @Override
  public void onCreated() {
    createTasksTableColumns();
    restoreBackup();
    tasksTable.getItems().addListener((Observable observable) -> checkStartTask());
  }

  @Override
  public void onDestroy() {
    backup();
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    DownloadBundle downloadBundle = bundle.get(BUNDLE_DOWNLOAD_KEY);
    if (downloadBundle != null) {
      createTask(downloadBundle);
    }
  }

  @SuppressWarnings("unchecked")
  private void createTasksTableColumns() {
    // 序号
    TableColumn<SpiderWrapper, Integer> id = NodeHelper.addClass(new TableColumn<>("#"), "id");
    id.prefWidthProperty().bind(tasksTable.widthProperty().multiply(0.1));
    id.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(tasksTable.getItems().indexOf(param.getValue()) + 1));
    // 名称
    TableColumn<SpiderWrapper, String> name = new TableColumn<>("名称");
    name.prefWidthProperty().bind(tasksTable.widthProperty().multiply(0.35));
    name.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getName()));
    // 进度
    TableColumn<SpiderWrapper, SpiderWrapper> progress = new TableColumn<>("进度");
    progress.prefWidthProperty().bind(tasksTable.widthProperty().multiply(0.35));
    progress.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    progress.setCellFactory(param -> new ProgressBarTableCell());
    // 操作
    TableColumn<SpiderWrapper, SpiderWrapper> operation = NodeHelper.addClass(new TableColumn<>("操作"), "align-center");
    operation.prefWidthProperty().bind(tasksTable.widthProperty().multiply(0.15));
    operation.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
    operation.setCellFactory(param -> new DownloadActionTableCell());

    tasksTable.getColumns().addAll(id, name, progress, operation);
    tasksTable.getColumns().forEach(column -> column.setResizable(false));
  }

  /**
   * 处理完成后
   */
  public void onCompleted(SpiderWrapper wrapper) {
    // 移除任务
    tasksTable.getItems().remove(wrapper);
  }

  /**
   * 检测是否能够添加新的任务
   */
  private synchronized boolean checkStartTask() {
    // 添加新的任务
    Integer maxTaskNum = SettingManager.manager().getDownload().getTaskNum().get();
    long currentRunning = tasksTable.getItems().stream().filter(task -> !task.getSpider().isState(Spider.INIT)).count();
    long canAdd = maxTaskNum - currentRunning;
    if (canAdd > 0) {
      for (SpiderWrapper task : tasksTable.getItems()) {
        if (task.getSpider().isState(Spider.INIT)) {
          task.run();
          // 是否还可以添加新的任务
          if (--canAdd == 0) {
            break;
          }
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * 创建下载任务
   *
   * @param bundle 下载数据包
   */
  private void createTask(DownloadBundle bundle) {
    DownloadConfig downloadConfig = SettingManager.manager().getDownload();
    String savePath = downloadConfig.getFolder().getValue();
    if (!FileUtils.exist(savePath)) {
      Toast.error("下载文件夹不存在");
      return;
    }
    boolean isAudio = Boolean.TRUE.equals(bundle.getRule().getAudio());
    Novel novel = bundle.getNovel();
    Spider spider = Spider.create(novel.getUrl())
      .novel(novel)
      .rule(bundle.getRule())
      .retryTimes(downloadConfig.getRetryNum().getValue())
      .savePath(savePath)
      .thread(isAudio ? 1 : downloadConfig.getThreadNum().getValue());
    SpiderWrapper spiderWrapper = new SpiderWrapper(spider, this::onCompleted);
    tasksTable.getItems().add(spiderWrapper);
  }

  /**
   * 备份下载任务
   */
  private void backup() {
    if (!tasksTable.getItems().isEmpty()) {
      File file = ResourceManager.cacheFile("downloads.json");
      ArrayList<SpiderWrapper> downloadTasks = new ArrayList<>(tasksTable.getItems());
      downloadTasks.forEach(SpiderWrapper::pause);
      String tmp = GsonUtils.toJson(downloadTasks);
      FileUtil.writeUtf8String(tmp, file);
    }
  }

  /**
   * 恢复备份任务
   */
  private void restoreBackup() {
    File file = ResourceManager.cacheFile("downloads.json");
    if (file.exists()) {
      List<SpiderWrapper> tasks = GsonUtils.me().fromJson(FileUtil.readUtf8String(file), new TypeToken<List<SpiderWrapper>>() {
      }.getType());
      tasksTable.getItems().addAll(tasks);
      // 完成时
      tasks.forEach(task -> task.init(this::onCompleted));
      FileUtil.del(file);
    }
  }
}
