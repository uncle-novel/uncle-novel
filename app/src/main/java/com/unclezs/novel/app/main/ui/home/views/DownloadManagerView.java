package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.io.FileUtil;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.PlaceHolder;
import com.unclezs.novel.app.framework.components.TabButton;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.serialize.PropertyJsonSerializer;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.dao.DownloadHistoryDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.DownloadBundle;
import com.unclezs.novel.app.main.model.DownloadConfig;
import com.unclezs.novel.app.main.model.DownloadHistory;
import com.unclezs.novel.app.main.model.SpiderWrapper;
import com.unclezs.novel.app.main.ui.home.views.widgets.DownloadActionTableCell;
import com.unclezs.novel.app.main.ui.home.views.widgets.DownloadHistoryActionTableCell;
import com.unclezs.novel.app.main.ui.home.views.widgets.ProgressBarTableCell;
import com.unclezs.novel.app.main.ui.home.views.widgets.TagsTableCell;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
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
  /**
   * 下载历史DAO
   */
  private final DownloadHistoryDao historyDao = new DownloadHistoryDao();
  @FXML
  private TabButton tasksTab;
  @FXML
  private TabButton historyTab;
  @FXML
  private VBox tasksPanel;
  private VBox historyPanel;
  @FXML
  private TableView<SpiderWrapper> tasksTable;
  private TableView<DownloadHistory> historyTable;
  @FXML
  private StackPane container;

  @Override
  public void onCreated() {
    createTasksTableColumns();
    restoreBackup();
    // 最大任务数量控制
    tasksTable.getItems().addListener((Observable observable) -> runTask());
    SettingManager.manager().getDownload().getTaskNum().addListener(e -> runTask());
    historyTab.setOnAction(e -> container.getChildren().setAll(getHistoryPanel()));
    tasksTab.setOnAction(e -> container.getChildren().setAll(tasksPanel));
  }

  @Override
  public void onDestroy() {
    backup();
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    DownloadBundle downloadBundle = bundle.get(BUNDLE_DOWNLOAD_KEY);
    if (downloadBundle != null) {
      tasksTab.fireEvent(new ActionEvent());
      createTask(downloadBundle);
    }
  }

  /**
   * 创建下载历史面板
   */
  private Node getHistoryPanel() {
    if (historyPanel == null) {
      historyPanel = new VBox(getHistoryTable());
    }
    return historyPanel;
  }

  /**
   * 获取下载历史表格，不存在则创建
   *
   * @return 下载历史表格
   */
  @SuppressWarnings("unchecked")
  private TableView<DownloadHistory> getHistoryTable() {
    if (historyTable == null) {
      historyTable = new TableView<>();
      historyTable.setPlaceholder(new PlaceHolder("还没有下载历史~"));
      VBox.setVgrow(historyTable, Priority.ALWAYS);
      historyTable.getItems().setAll(historyDao.selectAll());
      historyTable.getItems().addListener((ListChangeListener<DownloadHistory>) c -> {
        while (c.next()) {
          c.getRemoved().forEach(historyDao::delete);
          c.getAddedSubList().forEach(historyDao::save);
        }
      });
      // 序号
      TableColumn<DownloadHistory, Integer> id = NodeHelper.addClass(new TableColumn<>("#"), "id");
      id.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.1));
      id.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(historyTable.getItems().indexOf(param.getValue()) + 1));
      // 名称
      TableColumn<DownloadHistory, String> name = new TableColumn<>("名称");
      name.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.3));
      name.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getName()));
      // 类型
      TableColumn<DownloadHistory, String> type = new TableColumn<>("类型");
      type.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.25));
      type.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getType()));
      type.setCellFactory(param -> new TagsTableCell<>());

      TableColumn<DownloadHistory, String> date = new TableColumn<>("时间");
      date.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.2));
      date.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getDate()));
      // 操作
      TableColumn<DownloadHistory, DownloadHistory> operation = NodeHelper.addClass(new TableColumn<>("操作"), "align-center");
      operation.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.1));
      operation.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(col.getValue()));
      operation.setCellFactory(param -> new DownloadHistoryActionTableCell());

      historyTable.getColumns().addAll(id, name, type, date, operation);
      historyTable.getColumns().forEach(column -> column.setResizable(false));
    }
    return historyTable;
  }

  /**
   * 创建下载任务列表表格
   */
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
    TableColumn<SpiderWrapper, SpiderWrapper> operation = NodeHelper.addClass(new TableColumn<>("操作"), "download-action-col");
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
    // 保存下载历史，不存在下载历史页面则直接入库
    DownloadHistory downloadHistory = DownloadHistory.fromWrapper(wrapper);
    if (historyTable != null) {
      historyTable.getItems().add(downloadHistory);
    } else {
      historyDao.save(downloadHistory);
    }
  }

  /**
   * 启动任务，限制设置的任务数量
   */
  public void runTask() {
    // 添加新的任务
    int canRunTasksNumber = canRunTasksNumber();
    if (canRunTasksNumber > 0) {
      // 等待中的任务
      List<SpiderWrapper> waitingTask = tasksTable.getItems().stream()
        .filter(task -> task.isState(SpiderWrapper.WAIT_RUN))
        .collect(Collectors.toList());
      for (int i = 0; i < canRunTasksNumber && i < waitingTask.size(); i++) {
        waitingTask.get(i).run();
      }
      // 任务数量超过了，则让超出的任务等待执行
    } else if (canRunTasksNumber < 0) {
      List<SpiderWrapper> runningTask = tasksTable.getItems().stream()
        .filter(task -> task.getSpider().isState(Spider.RUNNING))
        .collect(Collectors.toList());
      for (int i = canRunTasksNumber; i < 0 && runningTask.size() + i >= 0; i++) {
        runningTask.get(runningTask.size() + i).waiting();
      }
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
    Spider spider = new Spider();
    spider.setUrl(novel.getUrl());
    spider.setNovel(bundle.getNovel());
    spider.setAnalyzerRule(bundle.getRule());
    spider.setRetryTimes(downloadConfig.getRetryNum().getValue());
    spider.setSavePath(savePath);
    spider.setThreadNum(isAudio ? 1 : downloadConfig.getThreadNum().getValue());
    SpiderWrapper spiderWrapper = new SpiderWrapper(spider, this::onCompleted);
    tasksTable.getItems().add(spiderWrapper);
    // 如果还有任务数量剩余则直接启动
    if (canRunTasksNumber() > 0) {
      spiderWrapper.run();
    }
  }

  /**
   * 备份下载任务
   */
  private void backup() {
    if (!tasksTable.getItems().isEmpty()) {
      File file = ResourceManager.cacheFile("downloads.json");
      ArrayList<SpiderWrapper> downloadTasks = new ArrayList<>(tasksTable.getItems());
      downloadTasks.forEach(SpiderWrapper::pause);
      String tmp = PropertyJsonSerializer.toJson(downloadTasks);
      FileUtil.writeUtf8String(tmp, file);
    }
  }

  /**
   * 恢复备份任务
   */
  private void restoreBackup() {
    File file = ResourceManager.cacheFile("downloads.json");
    if (file.exists()) {
      List<SpiderWrapper> tasks = PropertyJsonSerializer.GSON.fromJson(FileUtil.readUtf8String(file), new TypeToken<List<SpiderWrapper>>() {
      }.getType());
      tasksTable.getItems().addAll(tasks);
      // 完成时
      tasks.forEach(task -> task.init(this::onCompleted));
      FileUtil.del(file);
    }
  }

  /**
   * 剩余可以运行的任务数量
   *
   * @return 任务数量
   */
  public int canRunTasksNumber() {
    Integer maxTaskNum = SettingManager.manager().getDownload().getTaskNum().get();
    long currentRunning = tasksTable.getItems().stream().filter(task -> task.isState(Spider.RUNNING)).count();
    return (int) (maxTaskNum - currentRunning);
  }
}
