package com.unclezs.gui.utils;

import javafx.scene.paint.Color;

/**
 * @author uncle
 * @date 2020/5/12 19:30
 */
public class ColorUtil {

    /**
     * 字体颜色 rgb
     *
     * @return /
     */
    public static String getFontRgbColor(String color) {
        return getFontRgbColor(Color.valueOf(color));
    }

    /**
     * 字体颜色 rgb 黑底白字
     *
     * @return /
     */
    public static String getFontRgbColor(Color color) {
        return isWhiteFontColor(color) ? "rgba(255, 255, 255, 0.87)" : "rgba(0, 0, 0, 0.87)";
    }

    /**
     * 是否为白色字体
     *
     * @return /
     */
    public static Boolean isWhiteFontColor(Color color) {
        return color.grayscale().getRed() < 0.5;
    }

    /**
     * 是否为白色字体
     *
     * @return /
     */
    public static Boolean isWhiteFontColor(String colorHex) {
        return isWhiteFontColor(Color.valueOf(colorHex));
    }
}
