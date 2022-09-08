package com.unclezs.gui.components;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXSpinner;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.ResourceUtil;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 加载Loading...
 *
 * @author unclezs.com
 * @date 2019.06.25 18:35
 */
public abstract class AbstractLoadingTask<R> extends Task<R> {
    private JFXAlert<?> dialog;

    /**
     * 加载条
     */
    protected AbstractLoadingTask() {
        this(DataManager.currentStage);
    }

    /**
     * 加载条
     */
    protected AbstractLoadingTask(Stage stage) {
        this.initDialog(stage);
        //启动时显示
        super.setOnRunning(e -> show());
        super.setOnSucceeded(e -> close());
        super.setOnFailed(e -> close());
        super.setOnCancelled(e -> close());
    }

    private void initDialog(Stage stage) {
        if (dialog == null) {
            dialog = new JFXAlert<>(stage);
            dialog.setOverlayClose(false);
            Label cancel = new Label("取消");
            cancel.getStyleClass().addAll("loading-cancel-btn");
            cancel.setOnMouseClicked(e -> {
                boolean cancelRes = this.cancel();
                if (!cancelRes && dialog.isShowing()) {
                    close();
                }
            });
            JFXSpinner loading = new JFXSpinner();
            loading.getStyleClass().add("loading-spinner");
            loading.setRadius(13);
            VBox container = new VBox();
            container.getStyleClass().addAll("loading-box", "loading-container", "bg-color-theme");
            //主题设置
            container.getStylesheets().addAll(ResourceUtil.loadCss("/css/components/loading.css"));
            container.setPrefWidth(100);
            container.getChildren().addAll(loading, cancel);
            dialog.setContent(container);
        }
    }

    public void close() {
        if (dialog.isShowing()) {
            dialog.hideWithAnimation();
        }
    }

    public void show() {
        this.dialog.show();
    }


    public void setSuccessHandler(EventHandler<WorkerStateEvent> value) {
        super.setOnSucceeded(e -> {
            value.handle(e);
            close();
        });
    }

    public void setFailedHandler(EventHandler<WorkerStateEvent> value) {
        super.setOnFailed(e -> {
            value.handle(e);
            close();
        });
    }

    public void setCancelHandler(EventHandler<WorkerStateEvent> value) {
        super.setOnCancelled(e -> {
            value.handle(e);
            close();
        });
    }
}
