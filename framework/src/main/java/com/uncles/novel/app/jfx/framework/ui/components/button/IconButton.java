package com.uncles.novel.app.jfx.framework.ui.components.button;

import com.jfoenix.controls.JFXButton;
import com.uncles.novel.app.jfx.framework.ui.components.icon.Icon;
import com.uncles.novel.app.jfx.framework.ui.components.icon.SvgIcon;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableStringProperty;
import javafx.css.Styleable;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.StringConverter;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图标按钮封装
 * <p>
 * 支持svg和fontawesome
 * <p>
 * css设置图标
 * <pre>
 *     1.字体图标 {@link com.uncles.novel.app.jfx.framework.ui.components.icon.Icon}
 *     -fx-icon: "\uf010"
 *     -fx-icon: "图标名"
 *     2.矢量图标 {@link com.uncles.novel.app.jfx.framework.ui.components.icon.SvgIcon}
 *     -fx-svg-icon: "_图标名"
 *     -fx-svg-icon: "svg path"
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/02/28 1:28
 */
@Getter
@Setter
public class IconButton extends JFXButton {
    public static final String DEFAULT_STYLE_CLASS = "icon-button";
    /**
     * 字体图标
     */
    private String icon;
    /**
     * svg图标
     */
    private String svg;
    /**
     * 悬浮提示
     */
    private String tip;

    /**
     * 字体图标css
     */
    private final StyleableStringProperty styleableIcon = new SimpleStyleableStringProperty(StyleableProperties.ICON, IconButton.this, "-fx-icon", null) {
        @Override
        public void invalidated() {
            setIcon(getValue());
        }
    };

    /**
     * svg图标css
     */
    private final StyleableStringProperty styleableSvgIcon = new SimpleStyleableStringProperty(StyleableProperties.ICON, IconButton.this, "-fx-svg-icon", null) {
        @Override
        public void invalidated() {
            setSvg(getValue());
        }
    };


    public IconButton() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * 设置文字
     *
     * @param text 文字
     */
    public IconButton(String text) {
        this(text, null, null);
    }

    /**
     * 可以设置提示
     *
     * @param text 文本
     * @param tip  提示
     */
    public IconButton(String text, String tip) {
        this(text, null, tip);
    }

    /**
     * 单独图标
     *
     * @param icon 图标
     * @param tip  提示
     */
    public IconButton(Character icon, String tip) {
        this(null, icon, tip);
    }

    /**
     * 设置全部
     *
     * @param text 文字
     * @param icon 字体图标
     * @param tip  提示
     */
    public IconButton(String text, Character icon, String tip) {
        super(text);
        setIcon(icon);
        setTip(tip);
        setCursor(Cursor.HAND);
    }

    /**
     * 设置svg图标
     *
     * @param svg svg图标
     */
    public void setSvg(String svg) {
        this.svg = svg;
        if (svg != null) {
            this.setGraphic(new SvgIcon(svg));
        }
    }

    /**
     * 设置字体图标
     *
     * @param icon 字体图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
        if (icon != null) {
            this.setGraphic(new Icon(icon));
        }
    }

    /**
     * 设置字体图标
     *
     * @param icon 字体图标
     */
    public void setIcon(Icon icon) {
        if (icon != null) {
            this.setGraphic(icon);
        }
    }

    /**
     * 设置字体图标
     *
     * @param icon 字体图标
     */
    public void setIcon(Character icon) {
        if (icon != null) {
            this.setGraphic(new Icon(icon));
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

    /**
     * 自定义css属性
     * <p>
     * 1.-fx-icon指定图标
     * <p>
     * 2.-fx-svg-icon指定svg图标
     */
    private static class StyleableProperties {
        private static final CssMetaData<IconButton, String> ICON = new CssMetaData<>("-fx-icon", StringConverter.getInstance(), "") {
            @Override
            public boolean isSettable(IconButton control) {
                return control.icon == null;
            }

            @Override
            public StyleableStringProperty getStyleableProperty(IconButton control) {
                return control.styleableIcon;
            }
        };
        private static final CssMetaData<IconButton, String> SVG = new CssMetaData<>("-fx-svg-icon", StringConverter.getInstance(), "") {
            @Override
            public boolean isSettable(IconButton control) {
                return control.icon == null;
            }

            @Override
            public StyleableStringProperty getStyleableProperty(IconButton control) {
                return control.styleableSvgIcon;
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(JFXButton.getClassCssMetaData());
            Collections.addAll(styleables, ICON, SVG);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }


    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }
}
