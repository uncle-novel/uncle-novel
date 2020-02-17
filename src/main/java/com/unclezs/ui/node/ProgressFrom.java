package com.unclezs.ui.node;

import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.ToastUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Timer;
import java.util.TimerTask;

/*
 *@author unclezs.com
 *@date 2019.06.25 18:35
 */
public class ProgressFrom {
    private Stage dialogStage;
    private ProgressIndicator progressIndicator;
    private Task task;

    /**
     * 加载条
     *
     * @param primaryStage 父舞台
     */
    public ProgressFrom(Stage primaryStage,Task task) {
        this.task=task;
        dialogStage = new Stage();
        progressIndicator = new ProgressIndicator();
        // 窗口父子关系
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        // progress bar
        Label label = new Label("取消");
        label.setMinWidth(150);
        label.setTextFill(Color.valueOf("#009688"));
        label.setAlignment(Pos.CENTER);
        label.setOnMouseClicked(e->{
            dialogStage.close();
            if(task.isRunning()){
                task.cancel();
            }
        });
        progressIndicator.setProgress(-1F);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().addAll(progressIndicator,label);
        Scene scene = new Scene(vBox);
        scene.setFill(null);
        if (primaryStage.equals(DataManager.mainStage)) {
            Platform.runLater(() -> {
                //居中  舞台X坐标+（舞台宽度-菜单宽度）/2 +弹窗宽度+菜单宽度
                dialogStage.setX(primaryStage.getX() + (primaryStage.getWidth() - 150) / 2 - dialogStage.getWidth() / 2 + 150);
                dialogStage.setY(primaryStage.getY() + (primaryStage.getHeight()) / 2 - dialogStage.getHeight() / 2);
            });
        } else {
            Platform.runLater(() -> {
                dialogStage.setX(primaryStage.getX() + (primaryStage.getWidth()) / 2 - dialogStage.getWidth() / 2);
                dialogStage.setY(primaryStage.getY() + (primaryStage.getHeight()) / 2 - dialogStage.getHeight() / 2);
            });
        }
        dialogStage.setScene(scene);
    }

    public void activateProgressBar() {
        dialogStage.show();
        new Thread(task).start();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (dialogStage.isShowing()) {
                        cancelProgressBar();
                        ToastUtil.toast("请求超时");
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 30000);
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void cancelProgressBar() {
        hidenProgressBar();
    }
    public void hidenProgressBar() {
        dialogStage.close();
    }
}
