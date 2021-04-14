/**
 * Copyright (c) 2013, 2020 ControlsFX All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: * Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution. * Neither the name of ControlsFX, any associated website, nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.unclezs.novel.app.framework.components.icon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * 字体图标
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public class Icon extends Label {

  public static final String FONT_FAMILY = "iconfont";
  private static final String[] DEFAULT_CSS_CLASSES = {"icon", "font-icon"};
  private static final Map<String, Character> ICONS = new HashMap<>(16);

  static {
    for (IconFont value : IconFont.values()) {
      register(value.name().toLowerCase(), value.getUnicode());
    }
  }

  private final ObjectProperty<Object> value = new SimpleObjectProperty<>();
  /**
   * 指定微调器节点的半径，默认情况下将其设置为-1 (USE_COMPUTED_SIZE)
   */
  private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
    StyleableProperties.SIZE, Icon.this, "size", Region.USE_COMPUTED_SIZE) {
    @Override
    public void invalidated() {
      setFontSize(super.getValue());
    }
  };


  /**
   * 空构造函数（由FXML使用）
   */
  public Icon() {
    getStyleClass().setAll(DEFAULT_CSS_CLASSES);
    setTextAlignment(TextAlignment.LEFT);
    setAlignment(Pos.CENTER_LEFT);
    setFontFamily(FONT_FAMILY);
    value.addListener(x -> updateIcon());
    fontProperty().addListener(x -> updateIcon());
  }

  /**
   * 创建一个新的图标 图标的名字、图标的Unicode字符
   *
   * @param value 图标的Unicode字符
   */
  public Icon(Object value) {
    this();
    this.setValue(value);
  }

  /**
   * 注册别名
   *
   * @param name        图标名称
   * @param iconUnicode unicode
   */
  public static void register(String name, char iconUnicode) {
    ICONS.put(name, iconUnicode);
  }

  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return StyleableProperties.CHILD_STYLEABLES;
  }

  /**
   * 获取此Icon的字体系列
   */
  public String getFontFamily() {
    return getFont().getFamily();
  }

  /**
   * 设置此图标的字体系列 字体大小重置为默认图标字体大小
   */
  public void setFontFamily(String family) {
    if (!getFontFamily().equals(family)) {
      setFont(Font.font(family, getFontSize()));
    }
  }

  /**
   * 获取此图标的字体大小
   */
  public double getFontSize() {
    return getFont().getSize();
  }

  /**
   * 设置此图标的字体大小
   */
  public void setFontSize(double size) {
    Font newFont = Font.font(getFontFamily(), size);
    setFont(newFont);
  }

  /**
   * 设置此图标的颜色
   */
  public void setColor(Color color) {
    setTextFill(color);
  }

  public Object getValue() {
    return value.get();
  }

  /**
   * 设置要显示的图标。
   *
   * @param iconValue unicode character
   */
  public void setValue(Object iconValue) {
    value.set(iconValue);
  }

  /**
   * 这将使用正确的unicode值更新文本，以便显示所需的图标。
   */
  private void updateIcon() {
    Object iconValue = getValue();
    if (iconValue != null) {
      if (iconValue instanceof IconFont) {
        setTextUnicode(((IconFont) iconValue).getUnicode());
      } else if (iconValue instanceof Character) {
        setTextUnicode((Character) iconValue);
      } else {
        String iconName = getValue().toString();
        Character iconUnicode = ICONS.get(iconName.toLowerCase());
        if (iconUnicode == null) {
          setText(iconName);
        } else {
          setTextUnicode(iconUnicode);
        }
      }
    }
  }

  /**
   * 将给定的char设置为文本
   *
   * @param unicode 图标unicode
   */
  private void setTextUnicode(char unicode) {
    setText(String.valueOf(unicode));
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return getClassCssMetaData();
  }

  private static class StyleableProperties {

    private static final CssMetaData<Icon, Number> SIZE = new CssMetaData<>("-fx-icon-size",
      SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
      @Override
      public boolean isSettable(Icon control) {
        return control.size == null || !control.size.isBound();
      }

      @Override
      public StyleableDoubleProperty getStyleableProperty(Icon control) {
        return control.size;
      }
    };
    private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

    static {
      final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
        Labeled.getClassCssMetaData());
      Collections.addAll(styleables, SIZE);
      CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }
}
