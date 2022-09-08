package com.unclezs.gui.utils;

import cn.hutool.core.thread.ThreadUtil;
import com.jfoenix.effects.JFXDepthManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.glyphfont.Glyph;

import java.util.concurrent.TimeUnit;

/**
 * 弹窗工具
 *
 * @author unclezs.com
 * @date 2019.07.06 12:46
 */
public class ToastUtil {
    private static final char SUCCESS_ICON = '\uf058';
    private static final char ERROR_ICON = '\uf057';
    private static final char WARNING_ICON = '\uf06a';
    private static final int DEFAULT_SHOW_TIME = 2;
    private static Stage toast;
    private static Label label;
    private static StackPane contentContainer;
    private static Glyph icon = new Glyph("FontAwesome", SUCCESS_ICON);
    private static Task closeTask;


    /**
     * 成功弹窗
     *
     * @param msg /
     */
    public static void success(String msg) {
        toast(msg, DEFAULT_SHOW_TIME, Type.SUCCESS);
    }

    /**
     * 失败弹窗
     *
     * @param msg /
     */
    public static void error(String msg) {
        toast(msg, DEFAULT_SHOW_TIME, Type.ERROR);
    }


    /**
     * 警告弹窗
     *
     * @param msg /
     */
    public static void warning(String msg) {
        toast(msg, DEFAULT_SHOW_TIME, Type.WARNING);
    }

    /**
     * 吐丝弹窗
     *
     * @param msg  消息
     * @param time 存在时间
     * @param type 类型 Type
     */
    public static void toast(String msg, int time, int type) {
        init();
        label.setText(msg);
        initStyle(type);
        Platform.runLater(ToastUtil::initLocation);
        if (toast.isShowing()) {
            toast.close();
        }
        if (closeTask != null && closeTask.isRunning()) {
            closeTask.cancel();
        }
        closeTask = new Task() {
            @Override
            protected Object call() {
                ThreadUtil.sleep(time, TimeUnit.SECONDS);
                Platform.runLater(() -> {
                    if (!this.isCancelled()) {
                        toast.close();
                    }
                });
                return null;
            }
        };
        closeTask.setOnRunning(e -> toast.show());
        closeTask.setOnSucceeded(e -> toast.close());
        closeTask.setOnCancelled(e -> toast.close());
        if (!toast.isShowing()) {
            toast.show();
        }
        ThreadUtil.execute(closeTask);
    }

    /**
     * 根据类型改变图标颜色
     *
     * @param type /
     */
    private static void initStyle(int type) {
        switch (type) {
            case Type.WARNING:
                icon.setIcon(WARNING_ICON);
                contentContainer.getStyleClass().setAll("toast-warning");
                break;
            case Type.ERROR:
                icon.setIcon(ERROR_ICON);
                contentContainer.getStyleClass().setAll("toast-error");
                break;
            default:
                icon.setIcon(SUCCESS_ICON);
                contentContainer.getStyleClass().setAll("toast-success");
        }
    }

    private static void init() {
        if (toast != null) {
            return;
        }
        toast = new Stage();
        contentContainer = new StackPane();
        toast.initStyle(StageStyle.TRANSPARENT);
        toast.initOwner(DataManager.currentStage);
        label = new Label("操作成功", icon);
        icon.setColor(Color.GREEN);
        icon.getStyleClass().setAll("toast-icon");
        label.getStyleClass().setAll("toast-text");
        contentContainer.getChildren().addAll(label);
        BorderPane container = new BorderPane();
        container.getStyleClass().setAll("container");
        JFXDepthManager.setDepth(contentContainer, 1);
        container.setCenter(contentContainer);
        container.setPadding(new Insets(1, 1, 1, 1));
        Scene scene = new Scene(container);
        scene.getStylesheets().setAll(ResourceUtil.loadCss("/css/components/toast.css"));
        scene.setFill(Color.TRANSPARENT);
        toast.setScene(scene);
    }


    /**
     * 初始化 绑定 位置监听
     */
    private static void initLocation() {
        double height = DataManager.currentStage.getHeight();
        double width = DataManager.currentStage.getWidth();
        toast.setY(DataManager.currentStage.getY() - toast.getHeight() / 2 + height / 2);
        toast.setX(DataManager.currentStage.getX() - toast.getWidth() / 2 + width / 2);
        DataManager.currentStage.xProperty().addListener(e -> {
            toast.setX(
                DataManager.currentStage.getX() - toast.getWidth() / 2 + DataManager.currentStage.getWidth() / 2);
        });
        DataManager.currentStage.yProperty().addListener(e -> {
            toast.setY(
                DataManager.currentStage.getY() - toast.getHeight() / 2 + DataManager.currentStage.getHeight() / 2);
        });
    }

    /**
     * 弹窗类型
     */
    interface Type {
        int SUCCESS = 1;
        int ERROR = 2;
        int WARNING = 3;
    }
}
