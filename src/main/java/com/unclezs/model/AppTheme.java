package com.unclezs.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 主题色
 *
 * @author uncle
 * @date 2020/4/27 21:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTheme {
    /**
     * 背景颜色
     */
    private SimpleStringProperty bgColor = new SimpleStringProperty("#FFF");
    /**
     * 背景图片
     */
    private SimpleStringProperty bgImage = new SimpleStringProperty();

    /**
     * 背景透明度
     */
    private SimpleDoubleProperty opacity = new SimpleDoubleProperty(0);
    /**
     * 窗口顶部颜色
     */
    private SimpleStringProperty headerColor = new SimpleStringProperty("#393D49");
    /**
     * 字体颜色
     */
    private SimpleStringProperty fontColor = new SimpleStringProperty("");
    private SimpleBooleanProperty isBgImage = new SimpleBooleanProperty(false);
}
