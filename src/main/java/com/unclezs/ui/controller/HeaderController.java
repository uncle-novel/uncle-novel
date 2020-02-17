package com.unclezs.ui.controller;

import com.unclezs.ui.utils.AlertUtil;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.TrayUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
 *窗口头部
 *@author unclezs.com
 *@date 2019.06.21 11:25
 */
public class HeaderController implements Initializable {
    //组件
    @FXML
    BorderPane header;//头部容器
    @FXML
    Label setting, max, min, exit, minTray;

    /***********成员************/
    private boolean isFullScreen;//全屏
    private double x, y, width, height;//
    private boolean isRight;// 是否处于右边界调整窗口状态
    private boolean isBottomRight;// 是否处于右下角调整窗口状态
    private boolean isBottom;// 是否处于下边界调整窗口状态
    private double RESIZE_WIDTH = 5.00;
    private double MIN_WIDTH = 934;
    private double MIN_HEIGHT = 618;
    private double xOffset = 0, yOffset = 0;//自定义dialog移动横纵坐标
    VBox box;//设置面板

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
        initEventHandler();
    }

    //最大化
    void max() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        isFullScreen = !isFullScreen;//更改全屏状态
        if (isFullScreen) {//显示全屏
            reSizeAndLocation(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        } else {//缩放回以前尺寸
            reSizeAndLocation(x, y, width, height);
        }
    }

    //最小化
    void min() {
        DataManager.mainStage.setIconified(true);
    }

    //设置
    void setting() {
        try {
            if (box == null) {
                box = new FXMLLoader(getClass().getResource("/fxml/setting_header.fxml")).load();
                box.layoutXProperty().bind(DataManager.mainStage.widthProperty().subtract(box.widthProperty()).subtract(230));
                box.layoutYProperty().bind(DataManager.content.layoutYProperty());
                DataManager.content.getChildren().add(box);
            } else {
                if (!DataManager.content.getChildren().contains(box)) {
                    box.setVisible(false);
                    DataManager.content.getChildren().add(box);
                }

                box.setVisible(!box.isVisible());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //退出
    void exit() {
        DataManager.mainStage.hide();
        AudioBookSelfController.saveInfo();
        System.exit(0);
    }

    //初始化
    void init() {
        //主窗口大小改变事件
        DataManager.mainStage.xProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isFullScreen) {
                x = newValue.doubleValue();
            }
        });
        DataManager.mainStage.yProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isFullScreen) {
                y = newValue.doubleValue();
            }
        });
        DataManager.mainStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isFullScreen) {
                width = newValue.doubleValue();
            }
        });
        DataManager.mainStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isFullScreen) {
                height = newValue.doubleValue();
            }
        });
        //拖动事件
        DataManager.root.setOnMouseMoved((MouseEvent event) -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();
            double width = DataManager.mainStage.getWidth();
            double height = DataManager.mainStage.getHeight();
            Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
            // 先将所有调整窗口状态重置
            isRight = isBottomRight = isBottom = false;
            if (y >= height - RESIZE_WIDTH) {
                if (x <= RESIZE_WIDTH) {// 左下角调整窗口状态
                    //不处理

                } else if (x >= width - RESIZE_WIDTH) {// 右下角调整窗口状态
                    isBottomRight = true;
                    cursorType = Cursor.SE_RESIZE;
                } else {// 下边界调整窗口状态
                    isBottom = true;
                    cursorType = Cursor.S_RESIZE;
                }
            } else if (x >= width - RESIZE_WIDTH) {// 右边界调整窗口状态
                isRight = true;
                cursorType = Cursor.E_RESIZE;
            }
            // 最后改变鼠标光标
            DataManager.root.setCursor(cursorType);
        });

        DataManager.root.setOnMouseDragged((MouseEvent event) -> {

            //根据鼠标的横纵坐标移动dialog位置
            event.consume();
            if (yOffset != 0) {
                DataManager.mainStage.setX(event.getScreenX() - xOffset);
                if (event.getScreenY() - yOffset < 0) {
                    DataManager.mainStage.setY(0);
                } else {
                    DataManager.mainStage.setY(event.getScreenY() - yOffset);
                }
            }

            double x = event.getSceneX();
            double y = event.getSceneY();
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = DataManager.mainStage.getX();
            double nextY = DataManager.mainStage.getY();
            double nextWidth = DataManager.mainStage.getWidth();
            double nextHeight = DataManager.mainStage.getHeight();
            if (isRight || isBottomRight) {// 所有右边调整窗口状态
                nextWidth = x;
            }
            if (isBottomRight || isBottom) {// 所有下边调整窗口状态
                nextHeight = y;
            }
            if (nextWidth <= MIN_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                nextWidth = MIN_WIDTH;
            }
            if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                nextHeight = MIN_HEIGHT;
            }
            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            DataManager.mainStage.setX(nextX);
            DataManager.mainStage.setY(nextY);
            DataManager.mainStage.setWidth(nextWidth);
            DataManager.mainStage.setHeight(nextHeight);

        });
        //鼠标点击获取横纵坐标
        DataManager.root.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            if (event.getSceneY() > 46) {
                yOffset = 0;
            } else {
                yOffset = event.getSceneY();
            }
        });

    }

    void initEventHandler() {
        exit.setOnMouseClicked(e -> exit());
        max.setOnMouseClicked(e -> max());
        min.setOnMouseClicked(e -> min());
        setting.setOnMouseClicked(e -> setting());
        minTray.setOnMouseClicked(e-> {
            try {
                TrayUtil.tray();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        min.setTooltip(AlertUtil.setTipTime(new Tooltip("最小化")));
        max.setTooltip(AlertUtil.setTipTime(new Tooltip("最大化")));
        minTray.setTooltip(AlertUtil.setTipTime(new Tooltip("最小化到托盘(Alt+U)")));
        exit.setTooltip(AlertUtil.setTipTime(new Tooltip("退出")));
        setting.setTooltip(AlertUtil.setTipTime(new Tooltip("设置")));
    }

    private void reSizeAndLocation(double x, double y, double width, double height) {
        DataManager.mainStage.setX(x);
        DataManager.mainStage.setY(y);
        DataManager.mainStage.setWidth(width);
        DataManager.mainStage.setHeight(height);
    }
}
