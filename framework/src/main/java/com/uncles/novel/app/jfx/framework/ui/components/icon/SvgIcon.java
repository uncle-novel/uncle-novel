package com.uncles.novel.app.jfx.framework.ui.components.icon;

import com.uncles.novel.app.jfx.framework.util.StrUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于显示svg图像的节点
 * <pre>
 *   <SvgIcon name="别名"></SvgIcon>
 *   <SvgIcon path="SVG PATH"></SvgIcon>
 *   path前带@则为名字
 *   new SvgIcon("@name")
 *   new SvgIcon("path")
 * </pre>
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
public class SvgIcon extends Pane {
    private static final Map<String, String> SVG_PATHS = new HashMap<>(16);
    private static final String DEFAULT_STYLE_CLASS = "svg-icon";
    private static final char PATH_NAME_PREFIX = '#';
    private static final int DEFAULT_PREF_SIZE = 64;
    private double widthHeightRatio = 1;
    private String path;
    private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>();
    /**
     * 指定微调器节点的半径，默认情况下将其设置为-1 (USE_COMPUTED_SIZE)
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(StyleableProperties.SIZE, SvgIcon.this, "size", Region.USE_COMPUTED_SIZE) {
        @Override
        public void invalidated() {
            setSizeRatio(getSize());
        }
    };

    public SvgIcon() {
        this(null);
    }

    public SvgIcon(String path) {
        this(path, Color.BLACK);
    }

    /**
     * 为指定的svg内容和颜色构造SVGGlyph节点
     */
    public SvgIcon(String path, Paint fill) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.fill.set(fill);
        this.fill.addListener((observable) -> setBackground(new Background(new BackgroundFill(getFill() == null ? Color.BLACK : getFill(), null, null))));
        shapeProperty().addListener(observable -> {
            Shape shape = getShape();
            if (getShape() != null) {
                widthHeightRatio = shape.prefWidth(-1) / shape.prefHeight(-1);
                if (getSize() != Region.USE_COMPUTED_SIZE) {
                    setSizeRatio(getSize());
                }
            }
        });
        setPath(path);
        setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
    }

    public String getPath() {
        if (StrUtils.isNotBlank(path) && path.charAt(0) == PATH_NAME_PREFIX) {
            return SVG_PATHS.getOrDefault(path.substring(1), path);
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (getPath() != null && !getPath().isEmpty()) {
            SVGPath shape = new SVGPath();
            shape.setContent(getPath());
            setShape(shape);
            setFill(getFill());
        }
    }

    /**
     * svg color property
     */
    public void setFill(Paint fill) {
        this.fill.setValue(fill);
    }

    public ObjectProperty<Paint> fillProperty() {
        return fill;
    }

    public Paint getFill() {
        return fill.getValue();
    }

    /**
     * 将svg调整为一定的宽度和高度
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(double width, double height) {
        this.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        this.setPrefSize(width, height);
        this.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
    }

    /**
     * 注册svg图标
     *
     * @param name 图标名字
     * @param path 图标path
     */
    public static void register(String name, String path) {
        SVG_PATHS.put(name, path);
    }

    /**
     * 将svg调整为该大小，同时保持width height比率
     *
     * @param size in pixel
     */
    private void setSizeRatio(double size) {
        double width = widthHeightRatio * size;
        double height = size / widthHeightRatio;
        if (width <= size) {
            setSize(width, size);
        } else {
            setSize(size, Math.min(height, size));
        }
    }

    /**
     * 将svg调整为一定的宽度，同时保持width height比率
     *
     * @param width in pixel
     */
    public void setSizeForWidth(double width) {
        double height = width / widthHeightRatio;
        setSize(width, height);
    }

    /**
     * 将svg调整为一定的宽度，同时保持width height比率
     *
     * @param height in pixel
     */
    public void setSizeForHeight(double height) {
        double width = height * widthHeightRatio;
        setSize(width, height);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    private static class StyleableProperties {
        private static final CssMetaData<SvgIcon, Number> SIZE = new CssMetaData<>("-fx-svg-size", SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
            @Override
            public boolean isSettable(SvgIcon control) {
                return control.size == null || !control.size.isBound();
            }

            @Override
            public StyleableDoubleProperty getStyleableProperty(SvgIcon control) {
                return control.size;
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Pane.getClassCssMetaData());
            Collections.addAll(styleables,
                SIZE
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return SvgIcon.StyleableProperties.CHILD_STYLEABLES;
    }

    static {
        // 注册默认的图标
        register("clothes", "M772.8 96v64l163.2 161.6-91.2 91.2c-12.8-11.2-27.2-16-43.2-16-36.8 0-65.6 28.8-65.6 65.6V800c0 35.2-28.8 64-64 64H352c-35.2 0-64-28.8-64-64V462.4c0-36.8-28.8-65.6-65.6-65.6-16 0-32 6.4-43.2 16l-91.2-91.2L249.6 160h40l1.6 1.6C336 228.8 420.8 272 512 272c91.2 0 176-41.6 220.8-110.4 0-1.6 1.6-1.6 1.6-1.6h38.4V96M291.2 96H256c-22.4 0-38.4 6.4-49.6 19.2L43.2 276.8c-25.6 25.6-25.6 65.6 0 89.6l94.4 94.4c11.2 11.2 27.2 17.6 41.6 17.6s30.4-6.4 41.6-17.6h1.6c1.6 0 1.6 0 1.6 1.6V800c0 70.4 57.6 128 128 128h320c70.4 0 128-57.6 128-128V462.4c0-1.6 0-1.6 1.6-1.6h1.6c11.2 11.2 27.2 17.6 41.6 17.6 16 0 30.4-6.4 41.6-17.6l94.4-94.4c25.6-25.6 25.6-65.6 0-89.6L819.2 115.2c-12.8-12.8-28.8-19.2-46.4-19.2h-40c-22.4 0-41.6 11.2-54.4 30.4-33.6 49.6-96 81.6-168 81.6s-134.4-33.6-168-81.6C332.8 107.2 312 96 291.2 96z");
        register("theme","M929.7 232.9L810.3 101.6c-20.4-22.4-47.9-34.8-77.3-34.8s-56.9 12.4-77.3 34.8l-47.8 52.5c-7.7 8.5-14.2 18.2-19.1 28.8H436.4c-5-10.6-11.4-20.4-19.1-28.8l-47.8-52.5c-20.4-22.4-47.9-34.8-77.3-34.8s-56.9 12.4-77.3 34.8L95.4 232.9c-40.6 44.6-40.6 117.2 0 161.8l47.8 52.5c19.1 20.9 44.4 33 71 34.6v362c0 63.8 48.4 115.8 108 115.8H703c59.5 0 108-51.9 108-115.8v-362c26.5-1.6 51.9-13.6 71-34.6l47.8-52.5c40.5-44.6 40.5-117.2-0.1-161.8z m-41.3 124.3l-47.8 52.5c-13.2 14.5-31.6 19.9-49.4 14.5-8.5-2.6-17.6-1-24.8 4.3-7.1 5.2-11.3 13.6-11.3 22.4v392.9c0 33.1-23.4 60.1-52.2 60.1H322.2c-28.8 0-52.2-27-52.2-60.1v-393c0-8.8-4.2-17.1-11.3-22.4-4.9-3.6-10.7-5.5-16.6-5.5-2.7 0-5.5 0.4-8.2 1.2-17.8 5.4-36.2 0-49.4-14.5l-47.8-52.5c-21.5-23.6-21.5-63.4 0-86.9L256.2 139c9.7-10.7 22.5-16.5 36-16.5s26.2 5.9 35.9 16.5l47.8 52.5c6.5 7.1 11.3 16.2 13.9 26.2 3.2 12.3 14.3 20.9 27 20.9h191.4c12.7 0 23.9-8.6 27-20.9 2.6-10 7.4-19.1 13.9-26.2l47.8-52.5c9.7-10.7 22.5-16.5 36-16.5s26.2 5.9 35.9 16.5l119.5 131.2c21.5 23.6 21.5 63.5 0.1 87z");
    }
}
