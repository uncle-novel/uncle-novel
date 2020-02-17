package com.unclezs.ui.app;

import com.unclezs.ui.utils.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 *@author unclezs.com
 *@date 2019.06.22 14:48
 */
public class Reader extends Application {
    public StageStyle stageStyle = StageStyle.DECORATED;
    private boolean isFullScreen;//全屏
    private double x, y, width, height;//
    private boolean isRight;// 是否处于右边界调整窗口状态
    private boolean isBottomRight;// 是否处于右下角调整窗口状态
    private boolean isBottom;// 是否处于下边界调整窗口状态
    private double RESIZE_WIDTH = 5.00;
    private double MIN_WIDTH = 250;
    private double MIN_HEIGHT = 350;
    private double xOffset = 0, yOffset = 0;//自定义dialog移动横纵坐标

    public Reader() {
    }

    public Reader(StageStyle stageStyle) {
        this.stageStyle = stageStyle;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage readerStage) throws IOException {
        readerStage.initStyle(stageStyle);
        DataManager.readerStage = readerStage;
        readerStage.getIcons().add(new Image("images/图标/圆角图标.png"));
        readerStage.setMinHeight(350);
        readerStage.setMinWidth(250);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/fxml/reader.fxml"));
        Pane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/reader.css").toExternalForm());
        readerStage.setScene(scene);
        readerStage.show();
        DataManager.currentStage = readerStage;
        if (stageStyle != StageStyle.DECORATED) {
            readerStage.xProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !isFullScreen) {
                    x = newValue.doubleValue();
                }
            });
            readerStage.yProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !isFullScreen) {
                    y = newValue.doubleValue();
                }
            });
            readerStage.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !isFullScreen) {
                    width = newValue.doubleValue();
                }
            });
            readerStage.heightProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !isFullScreen) {
                    height = newValue.doubleValue();
                }
            });
            //拖动事件
            root.setOnMouseMoved((MouseEvent event) -> {
                event.consume();
                double x = event.getSceneX();
                double y = event.getSceneY();
                double width = readerStage.getWidth();
                double height = readerStage.getHeight();
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
                root.setCursor(cursorType);
            });

            root.setOnMouseDragged((MouseEvent event) -> {

                //根据鼠标的横纵坐标移动dialog位置
                event.consume();
                if (yOffset != 0) {
                    readerStage.setX(event.getScreenX() - xOffset);
                    if (event.getScreenY() - yOffset < 0) {
                        readerStage.setY(0);
                    } else {
                        readerStage.setY(event.getScreenY() - yOffset);
                    }
                }

                double x = event.getSceneX();
                double y = event.getSceneY();
                // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
                double nextX = readerStage.getX();
                double nextY = readerStage.getY();
                double nextWidth = readerStage.getWidth();
                double nextHeight = readerStage.getHeight();
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
                readerStage.setX(nextX);
                readerStage.setY(nextY);
                readerStage.setWidth(nextWidth);
                readerStage.setHeight(nextHeight);

            });
            //鼠标点击获取横纵坐标
            root.setOnMousePressed(event -> {
                event.consume();
                xOffset = event.getSceneX();
                if (event.getSceneY() > 46) {
                    yOffset = 0;
                } else {
                    yOffset = event.getSceneY();
                }
            });
        }
    }

}
