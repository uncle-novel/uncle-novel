package com.unclezs.gui.components;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.gui.controller.DownloadController;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.DownloadRecordMapper;
import com.unclezs.model.DownloadRecord;
import com.unclezs.utils.MybatisUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.controlsfx.glyphfont.Glyph;

import java.io.File;
import java.io.IOException;


/**
 * @author unclezs.com
 * @date 2019.07.06 16:26
 */
@Getter
public class DownloadRecordNode extends HBox {
    private ImageView cover;
    private Glyph remove = new Glyph("FontAwesome", '\uf014');
    private Glyph openDir = new Glyph("FontAwesome", '\uf114');
    private Label size;
    private DownloadRecord record;
    private HBox actionBox = new HBox();
    private VBox infoBox = new VBox();

    public DownloadRecordNode(DownloadRecord record) {
        this.record = record;
        initView();
        initStyleClass();
        initEventHandler();
    }

    private void initStyleClass() {
        this.getStyleClass().addAll("download-record");
        cover.getStyleClass().addAll("download-record-cover");
        infoBox.getStyleClass().addAll("download-record-info");
        openDir.getStyleClass().addAll("icon-hover-color-default");
        remove.getStyleClass().addAll("icon-hover-color-danger");
        actionBox.getStyleClass().addAll("download-record-action-box");
    }

    private void initView() {
        if (StrUtil.isNotEmpty(record.getCover())) {
            cover = new ImageView(FileUtil.file(record.getCover()).toURI().toString());
        } else {
            cover = new ImageView("images/non_cover.png");
        }
        cover.setFitWidth(40);
        cover.setFitHeight(50);
        Label title = new Label("文件名称:" + record.getTitle());
        Label time = new Label("下载时间:" + record.getDatetime());
        Label type = new Label("文件类型:" + (record.getType()));
        infoBox.getChildren().addAll(title, type, time);
        this.size = new Label(record.getSize());
        //操作按钮
        BorderPane rightAction = new BorderPane();
        rightAction.setCenter(size);
        rightAction.setRight(actionBox);
        HBox.setHgrow(rightAction, Priority.ALWAYS);
        actionBox.setSpacing(25);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.getChildren().addAll(openDir, remove);
        //设置居中
        BorderPane.setAlignment(actionBox, Pos.CENTER_LEFT);
        BorderPane.setAlignment(size, Pos.CENTER);
        getChildren().addAll(cover, infoBox, rightAction);
    }

    private void initEventHandler() {
        remove.setOnMouseClicked(e -> {
            try {
                ContentUtil.getController(DownloadController.class).recordsListView.getItems().remove(this);
                ThreadUtil.execute(
                    () -> MybatisUtil.execute(DownloadRecordMapper.class, mapper -> mapper.deleteById(record.getId())));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        openDir.setOnMouseClicked(e -> {
            File file = FileUtil.file(this.record.getPath());
            if (file.exists()) {
                DesktopUtil.openDir(file.getParentFile());
            } else {
                ToastUtil.error("文件不存在，或已经被删除了");
            }
        });
    }
}
