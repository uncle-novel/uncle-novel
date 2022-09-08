package com.unclezs.gui.components;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.svg.SVGGlyph;
import com.unclezs.downloader.AbstractDownloader;
import com.unclezs.downloader.NovelDownloader;
import com.unclezs.downloader.config.DownloaderState;
import com.unclezs.gui.controller.DownloadController;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.mapper.DownloadRecordMapper;
import com.unclezs.model.DownloadRecord;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 下载管理节点
 *
 * @author unclezs.com
 * @date 2019.07.06 14:21
 */
@Data
public class DownloadingNode {
    /**
     * 进度条
     */
    private JFXProgressBar progress = new JFXProgressBar();
    private Label progressText = new Label("0/0");
    private HBox progressBox = new HBox();
    private Long id;
    private String title;
    private Label errorNum = new Label("0");
    private SVGGlyph startIcon = new SVGGlyph(
        "M384 189.38l433.82 323.97L384 837.31V189.38m-28.5-92.56c-18.36 0-35.5 14.56-35.5 35.44v762.16c0 20.88 17.13 35.44 35.5 35.44 6.86 0 13.9-2.03 20.28-6.53l514.67-384.35c17.14-12.8 17.14-38.48 0-51.28L375.77 103.36c-6.37-4.5-13.41-6.54-20.27-6.54z");
    private SVGGlyph pauseIcon = new SVGGlyph(
        "M320 864c-17.67 0-32-14.31-32-32V192c0-17.67 14.33-32 32-32s32 14.33 32 32v640c0 17.69-14.33 32-32 32zM704 864c-17.69 0-32-14.31-32-32V192c0-17.67 14.31-32 32-32s32 14.33 32 32v640c0 17.69-14.31 32-32 32z");
    private SVGGlyph stopIcon = new SVGGlyph(
        "M557.25 512l265.38-265.38c12.5-12.5 12.5-32.75 0-45.25s-32.75-12.5-45.25 0L512 466.75 246.62 201.38c-12.5-12.5-32.75-12.5-45.25 0s-12.5 32.75 0 45.25L466.75 512 201.38 777.38c-12.5 12.5-12.5 32.75 0 45.25 6.25 6.25 14.44 9.38 22.62 9.38s16.38-3.12 22.62-9.38L512 557.25l265.38 265.38c6.25 6.25 14.44 9.38 22.62 9.38s16.38-3.12 22.62-9.38c12.5-12.5 12.5-32.75 0-45.25L557.25 512z");
    private JFXButton start = new JFXButton("", startIcon);
    private JFXButton pause = new JFXButton("", pauseIcon);
    private JFXButton stop = new JFXButton("", stopIcon);
    private HBox actionBox = new HBox();
    private AbstractDownloader downloader;
    private Task<DownloadRecord> task;
    /**
     * 对动态网页的下载
     */
    private WebView webView;
    private WebEngine engine;
    private String html = "";
    private boolean webViewLoading = false;

    public DownloadingNode(AbstractDownloader downloader) {
        this.title = downloader.getTitle();
        this.downloader = downloader;
        this.id = downloader.getStartTime();
        this.progressText.setText(String.format("%s/%s", downloader.current(), downloader.total()));
        progressText.setPrefWidth(80);
        progress.setProgress(downloader.current() / downloader.total());
        progressBox.getChildren().addAll(progress, progressText);
        initStyleClass();
        initEventHandler();
        changeState();
    }

    /**
     * 开始下载
     */
    public void start() {
        if (downloader.isStartDynamic()) {
            initWebView();
            ThreadUtil.execute(() -> this.downloader.start(url -> {
                try {
                    webViewLoading = true;
                    Platform.runLater(() -> {
                        if (downloader instanceof NovelDownloader) {
                            NovelDownloader novelDownloader = (NovelDownloader) downloader;
                            if (StrUtil.isNotEmpty(novelDownloader.getConfig().getCookies().get())) {
                                try {
                                    Map<String, List<String>> headers = new LinkedHashMap<>();
                                    headers.put("Set-Cookie", Arrays.stream(
                                        novelDownloader.getConfig().getCookies().get().split(";")).collect(
                                        Collectors.toList()));
                                    java.net.CookieHandler.getDefault().put(URI.create(url), headers);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        engine.load(url);
                    });
                    while (webViewLoading) {
                        ThreadUtil.sleep(1000);
                    }
                    return html;
                } finally {
                    html = "";
                }
            }));
        } else {
            ThreadUtil.execute(() -> this.downloader.start(null));
        }
        task = new Task<DownloadRecord>() {
            @Override
            protected DownloadRecord call() {
                while (!downloader.finished() && !isCancelled()) {
                    updateProgress(downloader.current(), downloader.total());
                    updateMessage(downloader.processText());
                    updateTitle(String.valueOf(downloader.errorNum()));
                    ThreadUtil.sleep(1000);
                }
                if (!isCancelled()) {
                    //下载完成存记录
                    DownloadRecord record = new DownloadRecord();
                    record.setCover(downloader.getCover());
                    record.setDatetime(DateUtil.now());
                    record.setPath(downloader.getSaveFile().getAbsolutePath());
                    record.setTitle(downloader.getTitle());
                    record.setId(IdUtil.simpleUUID());
                    record.setSize(FileUtil.size(downloader.getSaveFile()));
                    record.setType(downloader.getType());
                    MybatisUtil.execute(DownloadRecordMapper.class, mapper -> mapper.insert(record));
                    return record;
                }
                return null;
            }
        };
        progress.progressProperty().bind(task.progressProperty());
        progressText.textProperty().bind(task.messageProperty());
        errorNum.textProperty().bind(task.titleProperty());
        // 移除列表
        task.setOnSucceeded(e -> {
            try {
                ContentUtil.getController(DownloadController.class).downloadingTable.getItems().remove(
                    DownloadingNode.this);
                ContentUtil.getController(DownloadController.class).recordsListView.getItems().add(
                    new DownloadRecordNode(task.getValue()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        ThreadUtil.execute(() -> {
            while (true) {
                if (downloader.getState() == DownloaderState.RUNNING) {
                    break;
                }
            }
            Platform.runLater(this::changeState);
            task.run();
        });
    }

    /**
     * 暂停下载
     */
    public void pause() {
        this.downloader.pause();
        if (task != null) {
            this.task.cancel();
        }
        changeState();
    }

    /**
     * 停止下载
     */
    public void stop() {
        this.downloader.stop();
        changeState();
        if (task != null) {
            this.task.cancel();
        }
        try {
            ContentUtil.getController(DownloadController.class).downloadingTable.getItems().remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化css
     */
    private void initStyleClass() {
        progress.getStyleClass().addAll("progress-bar-success", "progress");
        actionBox.getStyleClass().addAll("action-box");
        progressBox.getStyleClass().addAll("progress-box");
        startIcon.getStyleClass().addAll("download-action-btn", "border-color-theme");
        pauseIcon.getStyleClass().addAll("download-action-btn", "border-color-theme");
        stopIcon.getStyleClass().addAll("download-action-btn-stop", "border-color-theme");
    }

    /**
     * 初始话时间
     */
    private void initEventHandler() {
        start.setOnMouseClicked(e -> start());
        start.setTooltip(new Tooltip("开始"));
        stop.setOnMouseClicked(e -> stop());
        stop.setTooltip(new Tooltip("取消下载"));
        pause.setOnMouseClicked(e -> pause());
        pause.setTooltip(new Tooltip("暂停"));
    }

    private void changeState() {
        switch (downloader.getState()) {
            case RUNNING:
                this.actionBox.getChildren().clear();
                this.actionBox.getChildren().addAll(pause, stop);
                break;
            case PAUSE:
                this.actionBox.getChildren().clear();
                progressText.textProperty().unbind();
                this.progressText.setText("已暂停");
                this.actionBox.getChildren().addAll(start, stop);
                break;
            case STOP:
                break;
            default:
                this.actionBox.getChildren().clear();
                this.actionBox.getChildren().addAll(start, stop);
                break;
        }
    }

    private void initWebView() {
        if (webView != null) {
            return;
        }
        webView = new WebView();
        webView.setPrefSize(1, 1);
        progressBox.getChildren().add(0, webView);
        engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener(
            (ov, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    html = engine.executeScript("document.documentElement.outerHTML").toString();
                    webViewLoading = false;
                }
            });
        engine.setUserAgent(
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36");
        engine.setOnError(e -> webViewLoading = false);
    }
}
