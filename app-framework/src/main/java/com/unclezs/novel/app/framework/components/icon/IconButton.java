package com.unclezs.novel.app.framework.components.icon;

import cn.hutool.core.text.CharSequenceUtil;
import com.jfoenix.controls.JFXButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.Styleable;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.StringConverter;
import javafx.scene.Cursor;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import lombok.Getter;

/**
 * 图标按钮封装
 * <p>
 * css设置图标
 * <pre>
 *     字体图标 {@link Icon}
 *     -fx-icon: "\uf010"
 *     -fx-icon: "图标名"
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/02/28 1:28
 */
public class IconButton extends JFXButton {

  public static final String DEFAULT_STYLE_CLASS = "icon-button";
  /**
   * 悬浮提示
   */
  @Getter
  private String tip;

  /**
   * 字体图标css
   */
  private StringProperty icon;

  public IconButton() {
    this(null);
  }

  /**
   * 设置文字
   *
   * @param text 文字
   */
  public IconButton(String text) {
    this(text, CharSequenceUtil.EMPTY, null);
  }

  /**
   * 可以设置提示
   *
   * @param icon 文本
   * @param tip  提示
   */
  public IconButton(String icon, String tip) {
    this(null, icon, tip);
  }

  /**
   * 可以设置提示
   *
   * @param icon 文本
   * @param tip  提示
   */
  public IconButton(IconFont icon, String tip) {
    this(null, icon, tip);
  }

  /**
   * 设置全部
   *
   * @param text 文字
   * @param icon 字体图标
   * @param tip  提示
   */
  public IconButton(String text, IconFont icon, String tip) {
    this(text, icon.name().toLowerCase(), tip);
  }

  /**
   * 设置全部
   *
   * @param text 文字
   * @param icon 字体图标
   * @param tip  提示
   */
  public IconButton(String text, String icon, String tip) {
    super(text);
    getStyleClass().add(DEFAULT_STYLE_CLASS);
    setText(text);
    setIcon(icon);
    setTip(tip);
    setCursor(Cursor.HAND);
    setDisableVisualFocus(true);
  }

  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return StyleAbleProperties.CHILD_STYLEABLES;
  }

  /**
   * 获取字体图标
   *
   * @return 字体图标
   */
  public String getIcon() {
    return iconProperty().get();
  }

  /**
   * 设置字体图标
   *
   * @param icon 字体图标
   */
  public void setIcon(String icon) {
    this.iconProperty().set(icon);
  }

  /**
   * 设置字体图标
   *
   * @param icon 字体图标
   */
  public void setIcon(Icon icon) {
    if (icon != null) {
      this.iconProperty().set(icon.getValue().toString());
      this.setGraphic(icon);
    }
  }

  /**
   * 设置悬浮提示
   *
   * @param tip 提示
   */
  public void setTip(String tip) {
    this.tip = tip;
    if (tip != null) {
      this.setTooltip(new Tooltip(tip));
    }
  }

  public StringProperty iconProperty() {
    if (icon == null) {
      icon = new SimpleStyleableStringProperty(StyleAbleProperties.ICON, IconButton.this, "-fx-icon") {
        @Override
        public void invalidated() {
          // 设置为空则移除图标
          if (CharSequenceUtil.isBlank(getValue())) {
            setGraphic(null);
          } else {
            if (getGraphic() instanceof Icon) {
              ((Icon) getGraphic()).setValue(getValue());
            } else {
              setGraphic(new Icon(getValue()));
            }
          }
        }
      };
    }
    return icon;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return getClassCssMetaData();
  }

  /**
   * 自定义css属性
   * <p>
   * 1.-fx-icon指定图标
   * <p>
   * 2.-fx-svg-icon指定svg图标
   */
  private static class StyleAbleProperties {

    private static final CssMetaData<IconButton, String> ICON = new CssMetaData<>("-fx-icon", StringConverter.getInstance()) {
      @Override
      public boolean isSettable(IconButton control) {
        return control.icon == null || !control.icon.isBound();
      }

      @Override
      public StyleableStringProperty getStyleableProperty(IconButton control) {
        return (StyleableStringProperty) control.icon;
      }
    };
    private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

    static {
      final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Labeled.getClassCssMetaData());
      Collections.addAll(styleables, ICON);
      CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }
}
