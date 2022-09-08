package com.unclezs.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 解析小说配置文件
 *
 * @author unclezs.com
 * @date 2019.07.02 23:37
 */
@Data
public class AnalysisConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 章节头
     */
    private SimpleStringProperty chapterHead = new SimpleStringProperty();
    /**
     * 章节尾
     */
    private SimpleStringProperty chapterTail = new SimpleStringProperty();
    /**
     * 正文头
     */
    private SimpleStringProperty contentHead = new SimpleStringProperty();
    /**
     * 正文尾
     */
    private SimpleStringProperty contentTail = new SimpleStringProperty();
    private SimpleStringProperty cookies = new SimpleStringProperty();
    /**
     * 浏览器标识
     */
    private SimpleStringProperty userAgent = new SimpleStringProperty();
    /**
     * 广告字符
     */
    private SimpleStringProperty adStr = new SimpleStringProperty();
    private SimpleIntegerProperty rule = new SimpleIntegerProperty(3);
    /**
     * 章节是否过滤
     */
    private SimpleBooleanProperty chapterFilter = new SimpleBooleanProperty(true);
    /**
     * 章节是否排序
     */
    private SimpleBooleanProperty chapterSort = new SimpleBooleanProperty(false);
    /**
     * ncr转中文
     */
    private SimpleBooleanProperty ncrToZh = new SimpleBooleanProperty(false);
    /**
     * 繁体转简体
     */
    private SimpleBooleanProperty traToSimple = new SimpleBooleanProperty(false);
    /**
     * 启用动态网页爬取
     */
    private SimpleBooleanProperty startDynamic = new SimpleBooleanProperty(false);

}
