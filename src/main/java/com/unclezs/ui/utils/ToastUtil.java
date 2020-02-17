package com.unclezs.ui.utils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Timer;
import java.util.TimerTask;

/*
 *弹出工具
 *@author unclezs.com
 *@date 2019.07.06 12:46
 */
public class ToastUtil {
    private static Stage stage = new Stage();
    private static Label label = new Label();
    private static TimerTask task;

    static {
        stage.initStyle(StageStyle.TRANSPARENT);//舞台透明
        stage.setAlwaysOnTop(true);//置顶
    }

    //默认3秒
    public static void toast(String msg) {
        toast(msg, 3000, DataManager.mainStage);
    }

    /**
     * 指定时间消失
     *
     * @param msg
     * @param time
     */
    public static void toast(String msg, int time, Stage parentStage) {
        label.setText(msg);
        task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (stage.isShowing()) {
                        stage.close();
                    }
                });
            }
        };
        init(msg, parentStage);
        Timer timer = new Timer();
        timer.schedule(task, time);
        stage.show();
    }

    public static void toast(String msg, Stage parentStage) {
        toast(msg,3000,parentStage);
    }

    //设置消息
    private static void init(String msg, Stage parentStage) {
        Label label = new Label(msg);//默认信息
        label.setStyle("-fx-background: rgba(56,56,56,0.9);-fx-border-radius: 25;-fx-background-radius: 25");//label透明,圆角
        label.setTextFill(Color.rgb(255, 255, 255));//消息字体颜色
        label.setPrefHeight(50);
        label.setPadding(new Insets(15));
        label.setAlignment(Pos.CENTER);//居中
        label.setFont(new Font(20));//字体大小
        Scene scene = new Scene(label);
        scene.setFill(null);//场景透明
        Platform.runLater(() -> {
            //居中
            stage.setX(parentStage.getX() + parentStage.getWidth() / 2 - stage.getWidth() / 2);
            stage.setY(parentStage.getY() + parentStage.getHeight() / 2 - stage.getHeight() / 2);
        });
        stage.setScene(scene);
    }
}
