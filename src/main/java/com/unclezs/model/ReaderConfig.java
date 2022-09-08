package com.unclezs.model;

import static com.unclezs.gui.controller.ReaderController.EYE_COLOR;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

/**
 * 阅读器配置
 *
 * @author unclezs.com
 * @date 2019.06.24 09:19
 */
@Data
public class ReaderConfig {
    /**
     * 背景颜色
     */
    private SimpleStringProperty bgColor = new SimpleStringProperty(EYE_COLOR);
    /**
     * 背景图
     */
    private SimpleStringProperty bgImage = new SimpleStringProperty("");
    /**
     * 字体大小
     */
    private SimpleDoubleProperty fontSize = new SimpleDoubleProperty(18);
    /**
     * 字体
     */
    private SimpleStringProperty fontFamily = new SimpleStringProperty("仿宋");
    /**
     * 正文对齐方式
     */
    private SimpleStringProperty align = new SimpleStringProperty("LEFT");
    /**
     * 显示头部
     */
    private boolean header;
    /**
     * 页面宽度
     */
    private SimpleDoubleProperty pageWidth = new SimpleDoubleProperty(100);
    /**
     * 行间距
     */
    private SimpleDoubleProperty lineSpacing = new SimpleDoubleProperty(0);
    /**
     * 舞台宽度
     */
    private SimpleDoubleProperty stageWidth = new SimpleDoubleProperty(800);
    /**
     * 舞台高度
     */
    private SimpleDoubleProperty stageHeight = new SimpleDoubleProperty(720);
}
