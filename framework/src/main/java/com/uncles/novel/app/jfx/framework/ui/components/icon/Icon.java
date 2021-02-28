/**
 * Copyright (c) 2013, 2020 ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.uncles.novel.app.jfx.framework.ui.components.icon;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

/**
 * 字体图标
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public class Icon extends Label {
    public final static String DEFAULT_CSS_CLASS = "icon";
    private final ObjectProperty<Object> icon = new SimpleObjectProperty<>();
    private static final Map<String, Character> ICONS = new HashMap<>(16);

    /**
     * 空构造函数（由FXML使用）
     */
    public Icon() {
        getStyleClass().add(DEFAULT_CSS_CLASS);
        icon.addListener(x -> updateIcon());
        fontProperty().addListener(x -> updateIcon());
        setFontFamily("FontAwesome");
    }

    /**
     * 创建一个新的图标
     *
     * @param unicode 图标的Unicode字符
     */
    public Icon(char unicode) {
        this();
        setTextUnicode(unicode);
    }

    /**
     * 创建一个新的图标
     *
     * @param iconName 图标的名字
     */
    public Icon(String iconName) {
        this();
        setIcon(iconName);
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

    /**
     * 设置此图标的字体系列
     * 字体大小重置为默认图标字体大小
     */
    public void setFontFamily(String family) {
        if (!getFontFamily().equals(family)) {
            setFont(Font.font(family, getFontSize()));
        }
    }

    /**
     * 获取此Icon的字体系列
     */
    public String getFontFamily() {
        return getFont().getFamily();
    }

    /**
     * 设置此图标的字体大小
     */
    public void setFontSize(double size) {
        Font newFont = Font.font(getFontFamily(), size);
        setFont(newFont);
    }

    /**
     * 获取此图标的字体大小
     */
    public double getFontSize() {
        return getFont().getSize();
    }

    /**
     * 设置此图标的颜色
     */
    public void setColor(Color color) {
        setTextFill(color);
    }

    /**
     * 图标Unicode字符。
     */
    public ObjectProperty<Object> iconProperty() {
        return icon;
    }

    /**
     * 设置要显示的图标。
     *
     * @param iconValue unicode character
     */
    public void setIcon(Object iconValue) {
        icon.set(iconValue);
    }

    public Object getIcon() {
        return icon.get();
    }

    /**
     * 这将使用正确的unicode值更新文本，以便显示所需的图标。
     */
    private void updateIcon() {
        Object iconValue = getIcon();
        if (iconValue != null) {
            if (iconValue instanceof Character) {
                setTextUnicode((Character) iconValue);
            } else {
                String iconName = getIcon().toString();
                Character iconUnicode = ICONS.get(iconName);
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
}
