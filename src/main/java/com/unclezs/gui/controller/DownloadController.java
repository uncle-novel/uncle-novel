package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXListView;
import com.unclezs.downloader.AbstractDownloader;
import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.gui.animation.CenterScaleTransition;
import com.unclezs.gui.components.DownloadRecordNode;
import com.unclezs.gui.components.DownloadingNode;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.AlertUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.DownloadRecordMapper;
import com.unclezs.model.DownloadRecord;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.JsonUtil;
import com.unclezs.utils.MybatisUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 下载管理
 *
 * @author unclezs.com
 * @date 2019.07.06 00:16
 */
@Slf4j
@FXController("download")
public class DownloadController implements LifeCycleFxController {
    private static final String TASK_TMP_PATH = "tmp/download_task.tmp";
    public TableView<DownloadingNode> downloadingTable;
    public JFXListView<DownloadRecordNode> recordsListView;
    public BorderPane root;
    public BorderPane downloaded, downloading, placeholder;
    public Label downloadingNum, downloadedNum;
    public ToggleButton downloadedBtn, downloadingBtn;

    @Override
    public void initialize() {
        root.setCenter(downloading);
        root.getChildren().remove(placeholder);
        initListener();
        loadTasks();
        loadRecords();
    }

    @Override
    public void onShow(Dict data) {
        downloadingTable.requestLayout();
    }

    /**
     * 退出时候保存下载任务
     */
    @Override
    public void onDestroyed() {
        downloadingTable.getItems().forEach(DownloadingNode::pause);
        List<AbstractDownloader> tasks =
            downloadingTable.getItems().stream().map(DownloadingNode::getDownloader).collect(Collectors.toList());
        String s = JsonUtil.toJson(tasks);
        File file = FileUtil.currentDirFile(TASK_TMP_PATH);
        cn.hutool.core.io.FileUtil.writeUtf8String(s, file);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        downloadingTable.getItems().addListener((ListChangeListener<? super DownloadingNode>) e -> {
            if (downloadingTable.getItems().isEmpty()) {
                this.downloadingNum.setText("没有任务");
                this.downloading.setCenter(placeholder);
            } else {
                this.downloadingNum.setText("剩余" + downloadingTable.getItems().size() + "个下载任务");
                this.downloading.setCenter(downloadingTable);
            }
        });
        recordsListView.getItems().addListener((ListChangeListener<? super DownloadRecordNode>) e -> {
            if (recordsListView.getItems().isEmpty()) {
                this.downloadedNum.setText("没有下载记录");
                downloaded.setCenter(placeholder);
            } else {
                this.downloadedNum.setText("共" + recordsListView.getItems().size() + "个下载记录");
                downloaded.setCenter(recordsListView);
            }
        });
    }

    /**
     * 加载正在下载的任务
     */
    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = FileUtil.currentDirFile(TASK_TMP_PATH);
        if (!file.exists()) {
            return;
        }
        Task<List<DownloadingNode>> task = new Task<List<DownloadingNode>>() {
            @Override
            protected List<DownloadingNode> call() throws Exception {
                List<DownloadingNode> list = new ArrayList<>();
                JSONArray json = JSON.parseArray(cn.hutool.core.io.FileUtil.readUtf8String(file));
                for (int i = 0; i < json.size(); i++) {
                    JSONObject object = json.getJSONObject(i);
                    String type = object.getString("target");
                    Class<? extends AbstractDownloader> clazz =
                        (Class<? extends AbstractDownloader>) Class.forName(type);
                    list.add(new DownloadingNode(object.toJavaObject(clazz)));
                }
                return list;
            }
        };
        task.setOnSucceeded(e -> {
            downloadingTable.getItems().addAll(task.getValue());
        });
        ThreadUtil.execute(task);
    }


    /**
     * 加载下载历史
     */
    private void loadRecords() {
        //todo 分页
        Task<List<DownloadRecordNode>> task = new Task<List<DownloadRecordNode>>() {
            @Override
            protected List<DownloadRecordNode> call() throws Exception {
                List<DownloadRecord> records =
                    MybatisUtil.execute(DownloadRecordMapper.class, mapper -> mapper.selectList(null));
                ObservableList<DownloadRecordNode> recordList = FXCollections.observableArrayList();
                if (!records.isEmpty()) {
                    recordList.addAll(records.stream().map(DownloadRecordNode::new).collect(Collectors.toList()));
                }
                return recordList;
            }
        };
        task.setOnSucceeded(e -> {
            recordsListView.getItems().addAll(task.getValue());
            this.recordsListView.setItems(recordsListView.getItems());
        });
        ThreadUtil.execute(task);
    }

    /**
     * 添加下载任务
     *
     * @param downloader /
     */
    void addTask(AbstractDownloader downloader) {
        DownloadingNode downloadingNode = new DownloadingNode(downloader);
        downloadingTable.getItems().add(downloadingNode);
        downloadingNode.start();
    }

    /**
     * 开始全部
     */
    public void startAll() {
        downloadingTable.getItems().forEach(e -> {
            if (e.getDownloader().getState() != DownloaderState.RUNNING) {
                e.start();
            }
        });
    }

    /**
     * 暂停全部
     */
    public void pauseAll() {
        downloadingTable.getItems().forEach(e -> {
            if (e.getDownloader().getState() == DownloaderState.RUNNING) {
                e.pause();
            }
        });
    }

    /**
     * 停止全部任务
     */
    public void stopAll() {
        new ArrayList<>(downloadingTable.getItems()).forEach(DownloadingNode::stop);
    }

    /**
     * 正在下载
     */
    public void showDownloading() {
        this.downloadingBtn.setDisable(true);
        this.downloadedBtn.setDisable(false);
        CenterScaleTransition transition = new CenterScaleTransition(downloadingTable);
        root.setCenter(downloading);
        if (downloadingTable.getItems().isEmpty()) {
            downloading.setCenter(placeholder);
        } else {
            downloading.setCenter(downloadingTable);
        }
        transition.play();
    }

    /**
     * 下载历史
     */
    public void showDownloaded() {
        this.downloadingBtn.setDisable(false);
        this.downloadedBtn.setDisable(true);
        CenterScaleTransition transition = new CenterScaleTransition(recordsListView);
        root.setCenter(downloaded);
        if (recordsListView.getItems().isEmpty()) {
            downloaded.setCenter(placeholder);
        } else {
            downloaded.setCenter(recordsListView);
        }
        transition.play();
    }

    /**
     * 清空下载历史
     */
    public void clearRecords() {
        AlertUtil.confirm("确认清空吗？", res -> {
            if (res) {
                if (!recordsListView.getItems().isEmpty()) {
                    recordsListView.getItems().clear();
                    MybatisUtil.execute(DownloadRecordMapper.class, DownloadRecordMapper::deleteAll);
                }
                ToastUtil.success("已清空");
            }
        });
    }
}
