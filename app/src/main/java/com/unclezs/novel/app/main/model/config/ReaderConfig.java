package com.unclezs.novel.app.main.model.config;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.Font;
import lombok.Data;

/**
 * 阅读器配置
 *
 * @author blog.unclezs.com
 * @since 2021/5/8 11:28
 */
@Data
public class ReaderConfig {

  /**
   * 主题名称
   */
  private ObjectProperty<String> themeName = new SimpleObjectProperty<>("green");
  /**
   * 背景图
   */
  private ObjectProperty<String> bgImage = new SimpleObjectProperty<>("");
  /**
   * 字体大小
   */
  private ObjectProperty<Number> fontSize = new SimpleObjectProperty<>(18D);
  /**
   * 字体
   */
  private ObjectProperty<String> fontFamily = new SimpleObjectProperty<>(Font.getDefault().getFamily());
  /**
   * 正文对齐方式
   */
  private ObjectProperty<String> align = new SimpleObjectProperty<>("LEFT");
  /**
   * 简体繁体互转
   */
  private ObjectProperty<Integer> simpleTraditional = new SimpleObjectProperty<>(0);
  /**
   * TTS小说朗读引擎
   */
  private ObjectProperty<Integer> speaker = new SimpleObjectProperty<>(0);
  /**
   * TTS朗读速度
   */
  private ObjectProperty<Double> speed = new SimpleObjectProperty<>(1D);
  /**
   * 显示头部
   */
  private boolean showHeader = true;
  /**
   * 窗口置顶
   */
  private boolean windowTop;
  /**
   * 显示shadow
   */
  private boolean showShadow = true;
  /**
   * 翻页动画
   */
  private ObjectProperty<Boolean> flipAnimation = new SimpleObjectProperty<>(true);
  /**
   * 页面宽度
   */
  private ObjectProperty<Number> pageWidth = new SimpleObjectProperty<>(0.9D);
  /**
   * 行间距
   */
  private ObjectProperty<Number> lineSpacing = new SimpleObjectProperty<>(0D);
  /**
   * 舞台宽度
   */
  private ObjectProperty<Double> stageWidth = new SimpleObjectProperty<>(800D);
  /**
   * 舞台高度
   */
  private ObjectProperty<Double> stageHeight = new SimpleObjectProperty<>(720D);
}
