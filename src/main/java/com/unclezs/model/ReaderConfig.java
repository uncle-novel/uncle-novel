package com.unclezs.model;

/*
 *阅读器配置
 *@author unclezs.com
 *@date 2019.06.24 09:19
 */
public class ReaderConfig {
    private String bgColor;//背景颜色
    private double fontSize;//字体大小
    private String fontStyle;//字体大小
    private double pageWidth;//页面宽度
    private double stageWidth;//舞台宽度
    private double stageHeight;//舞台高度
    private String fontColor;//字体颜色

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public double getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(double pageWidth) {
        this.pageWidth = pageWidth;
    }

    public double getStageWidth() {
        return stageWidth;
    }

    public void setStageWidth(double stageWidth) {
        this.stageWidth = stageWidth;
    }

    public double getStageHeight() {
        return stageHeight;
    }

    public void setStageHeight(double stageHeight) {
        this.stageHeight = stageHeight;
    }

    @Override
    public String toString() {
        return "ReaderConfig{" +
                "bgColor='" + bgColor + '\'' +
                ", fontSize=" + fontSize +
                ", fontStyle='" + fontStyle + '\'' +
                ", pageWidth=" + pageWidth +
                ", stageWidth=" + stageWidth +
                ", stageHeight=" + stageHeight +
                '}';
    }
}
