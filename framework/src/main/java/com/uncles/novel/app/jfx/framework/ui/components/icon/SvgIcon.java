package com.uncles.novel.app.jfx.framework.ui.components.icon;

import com.uncles.novel.app.jfx.framework.util.StrUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.Getter;
import lombok.Setter;

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
 *   path前带_则为名字
 *   new SvgIcon("_name")
 *   new SvgIcon("path")
 * </pre>
 *
 * @author blog.unclezs.com
 * @since 2021/02/26 12:05
 */
@Getter
@Setter
public class SvgIcon extends StackPane {
    private static final Map<String, String> SVG_PATHS = new HashMap<>(16);
    private static final String[] DEFAULT_STYLE_CLASSES = {"icon", "svg-icon"};
    /**
     * svg名字前缀
     */
    private static final char PATH_NAME_PREFIX = '_';
    private static final int DEFAULT_PREF_SIZE = 13;
    private double widthHeightRatio = 1;
    private Pane icon;
    private String path;
    /**
     * 指定微调器节点的半径，默认情况下将其设置为-1 (USE_COMPUTED_SIZE)
     */
    private DoubleProperty size;
    /**
     * 图标颜色css
     */
    private ObjectProperty<Paint> fill;

    static {
        // 注册默认的图标
        register("clothes", "M772.8 96v64l163.2 161.6-91.2 91.2c-12.8-11.2-27.2-16-43.2-16-36.8 0-65.6 28.8-65.6 65.6V800c0 35.2-28.8 64-64 64H352c-35.2 0-64-28.8-64-64V462.4c0-36.8-28.8-65.6-65.6-65.6-16 0-32 6.4-43.2 16l-91.2-91.2L249.6 160h40l1.6 1.6C336 228.8 420.8 272 512 272c91.2 0 176-41.6 220.8-110.4 0-1.6 1.6-1.6 1.6-1.6h38.4V96M291.2 96H256c-22.4 0-38.4 6.4-49.6 19.2L43.2 276.8c-25.6 25.6-25.6 65.6 0 89.6l94.4 94.4c11.2 11.2 27.2 17.6 41.6 17.6s30.4-6.4 41.6-17.6h1.6c1.6 0 1.6 0 1.6 1.6V800c0 70.4 57.6 128 128 128h320c70.4 0 128-57.6 128-128V462.4c0-1.6 0-1.6 1.6-1.6h1.6c11.2 11.2 27.2 17.6 41.6 17.6 16 0 30.4-6.4 41.6-17.6l94.4-94.4c25.6-25.6 25.6-65.6 0-89.6L819.2 115.2c-12.8-12.8-28.8-19.2-46.4-19.2h-40c-22.4 0-41.6 11.2-54.4 30.4-33.6 49.6-96 81.6-168 81.6s-134.4-33.6-168-81.6C332.8 107.2 312 96 291.2 96z");
        register("theme", "M929.7 232.9L810.3 101.6c-20.4-22.4-47.9-34.8-77.3-34.8s-56.9 12.4-77.3 34.8l-47.8 52.5c-7.7 8.5-14.2 18.2-19.1 28.8H436.4c-5-10.6-11.4-20.4-19.1-28.8l-47.8-52.5c-20.4-22.4-47.9-34.8-77.3-34.8s-56.9 12.4-77.3 34.8L95.4 232.9c-40.6 44.6-40.6 117.2 0 161.8l47.8 52.5c19.1 20.9 44.4 33 71 34.6v362c0 63.8 48.4 115.8 108 115.8H703c59.5 0 108-51.9 108-115.8v-362c26.5-1.6 51.9-13.6 71-34.6l47.8-52.5c40.5-44.6 40.5-117.2-0.1-161.8z m-41.3 124.3l-47.8 52.5c-13.2 14.5-31.6 19.9-49.4 14.5-8.5-2.6-17.6-1-24.8 4.3-7.1 5.2-11.3 13.6-11.3 22.4v392.9c0 33.1-23.4 60.1-52.2 60.1H322.2c-28.8 0-52.2-27-52.2-60.1v-393c0-8.8-4.2-17.1-11.3-22.4-4.9-3.6-10.7-5.5-16.6-5.5-2.7 0-5.5 0.4-8.2 1.2-17.8 5.4-36.2 0-49.4-14.5l-47.8-52.5c-21.5-23.6-21.5-63.4 0-86.9L256.2 139c9.7-10.7 22.5-16.5 36-16.5s26.2 5.9 35.9 16.5l47.8 52.5c6.5 7.1 11.3 16.2 13.9 26.2 3.2 12.3 14.3 20.9 27 20.9h191.4c12.7 0 23.9-8.6 27-20.9 2.6-10 7.4-19.1 13.9-26.2l47.8-52.5c9.7-10.7 22.5-16.5 36-16.5s26.2 5.9 35.9 16.5l119.5 131.2c21.5 23.6 21.5 63.5 0.1 87z");
        register("bookshelf", "M792 64H344v896h448c61.825 0 112-50.176 112-112V176c0-61.824-50.12-112-112-112z m-0.448 358.569c0.449 24.976-1.847 32.703-11.872 32.703-4.369 0.728-11.424-1.96-21.672-10.807-15.399-10.92-26.151-20.44-35.448-26.376-8.289-7.169-21.336-6.328-29.512 0-10.752 6.048-26.152 19.376-35 26.376-10.92 9.631-18.256 10.807-20.832 10.807-10.471 0-13.16-9.072-12.768-32.703L624 149.737c0-25.816 10.416-29.736 21-29.736h126c12.824 0 21 5.88 21 29.736l-0.448 272.832zM120 176v672c0 61.824 50.176 112 112 112h56V64h-56c-61.824 0-112 50.176-112 112z");
        register("bookshelves", "M216.391303 407.890487h695.01671c11.803967 0 23.596861-1.306631 35.434048-2.624335a40.804521 40.804521 0 0 0 32.787568-30.15216c3.930965-15.734932-2.624334-31.480937-15.734932-40.66057a153.451575 153.451575 0 0 1-70.84595-125.89053c1.306631-53.771168 30.163234-103.600298 78.685733-129.821494 14.395082-7.873003 23.596861-23.607935 20.972527-40.66057a38.93316 38.93316 0 0 0-32.787568-32.776495C944.184507 1.362295 928.449575 0.055665 911.408013 0.055665H216.391303C100.975964-2.56867 3.930965 87.921031 0 204.631927c3.930965 115.393193 100.975964 205.882893 216.391303 203.25856z m0-329.149089h638.621207c-53.760095 74.743695-56.384429 175.71966-5.248668 251.781059H216.391303c-72.130434 2.624334-133.763532-53.771168-137.70557-125.89053A132.744803 132.744803 0 0 1 216.391303 78.741398z m557.322213 414.390121H78.67466c-15.734932 0-31.469864 1.306631-47.204796 5.248668-15.734932 2.613261-27.538899 15.690639-30.163233 31.469864s5.248668 31.469864 18.359266 40.69379a153.57338 153.57338 0 0 1 78.685733 129.788275c-1.317704 51.146834-27.538899 99.658261-70.812731 127.208233a38.556674 38.556674 0 0 0 15.734932 70.768438c11.803967 1.317704 23.607935 2.624334 36.718533 2.624334h693.721152c14.395082 0 26.221196-7.861929 34.094198-19.665896a41.812177 41.812177 0 0 0 0-39.342867 39.14355 39.14355 0 0 0-34.094198-19.67697h-634.712389c23.607935-36.707459 36.718532-78.67466 36.718533-121.948492a219.248173 219.248173 0 0 0-41.956128-129.755055h638.63228c72.130434-2.624334 133.763532 53.771168 137.694497 125.890529-3.930965 73.425992-65.564062 129.788275-139.001127 125.846237h-114.053343a39.342867 39.342867 0 0 0 0 78.685734h115.359973c116.710896 3.98633 212.471412-87.865366 216.38023-203.258559-3.930965-115.404266-99.658261-207.200597-215.062526-204.576263z m0 0");
        register("music", "M952.36 691.9V189.74c0-24.74-10.88-48.03-29.79-63.84-18.7-15.7-44.03-22.62-68.1-18.26L409.7 186.12c-39.93 7.01-68.92 41.54-68.92 82.1v364.15c-3.41-1.99-6.88-3.91-10.51-5.64-41.62-19.91-93.21-21.12-141.56-3.3C99.77 656.21 48.13 744.12 73.6 819.38c10.97 32.5 35.17 59.06 68.13 74.82 22.05 10.55 46.9 15.83 72.58 15.83 22.78 0 46.22-4.17 68.93-12.53 81.69-30.11 131.72-106.64 119.67-177.19V431.99l487.34-83.66v260.51c-5.18-3.26-10.53-6.36-16.2-9.07-46.54-22.25-104.25-23.51-158.38-3.43-99.56 36.97-157.49 135.71-129.14 220.09 12.21 36.42 39.25 66.19 76.12 83.81 24.57 11.75 52.23 17.64 80.84 17.64 25.6 0 51.96-4.73 77.49-14.21 96.37-35.75 153.48-129.31 131.38-211.77zM261.75 839.21c-32.66 12.03-66.63 11.68-93.23-1.05-12.52-5.99-28.94-17.55-36.08-38.66-14.53-42.95 21.07-96.89 77.74-117.77 15.87-5.85 32.06-8.77 47.55-8.77 16.42 0 32.05 3.28 45.72 9.82 12.5 5.98 28.9 17.54 36.04 38.69 0.54 1.6 0.88 3.25 1.28 4.88v0.09h0.02c10.4 42.03-24.49 92.67-79.04 112.77zM402.9 368.96V268.23c0-10.33 7.37-19.13 17.56-20.92l444.91-78.51c6.23-1.1 12.47 0.67 17.29 4.72 4.81 4.01 7.57 9.94 7.57 16.23v95.55L402.9 368.96z m396.45 476.49c-38.38 14.26-78.44 13.8-109.92-1.24-15.25-7.29-35.27-21.46-44.01-47.53-17.79-52.94 23.43-116.68 91.86-142.09 18.66-6.93 37.71-10.38 55.97-10.38 19.34 0 37.81 3.88 53.99 11.62 14.79 7.08 33.88 20.85 42.99 45.53v1.38h0.67c0.08 0.24 0.23 0.4 0.31 0.65 17.83 52.92-23.39 116.65-91.86 142.06z");
        register("network", "M512 1024C229.239467 1024 0 794.8288 0 512 0 229.1712 229.239467 0 512 0 794.760533 0 1024 229.1712 1024 512 1024 794.8288 794.760533 1024 512 1024L512 1024ZM512 63.965867C264.6016 63.965867 64.034133 264.533333 64.034133 512 64.034133 759.466667 264.6016 960.034133 512 960.034133 759.466667 960.034133 960.034133 759.466667 960.034133 512 960.034133 264.533333 759.466667 63.965867 512 63.965867L512 63.965867ZM768 512C768 370.619733 653.380267 256 512 256 370.619733 256 256 370.619733 256 512L192.034133 512C192.034133 335.2576 335.2576 192.034133 512 192.034133 688.7424 192.034133 832.034133 335.2576 832.034133 512L768 512 768 512ZM479.982933 640C480.324267 640 480.529067 640.136533 480.802133 640.136533L569.480533 431.172267C576.3072 415.061333 596.855467 405.777067 615.424 410.487467 633.992533 415.197867 643.4816 431.991467 636.654933 448.170667L544.290133 665.736533C563.541333 683.281067 576.034133 707.925333 576.034133 735.982933 576.034133 789.026133 533.026133 831.965867 479.982933 831.965867 427.008 831.965867 384 789.026133 384 735.982933 384 683.008 427.008 640 479.982933 640L479.982933 640ZM479.982933 768.068267C497.664 768.068267 512 753.732267 512 736.0512 512 718.370133 497.664 704.1024 479.982933 704.1024 462.301867 704.1024 448.034133 718.370133 448.034133 736.0512 448.034133 753.732267 462.301867 768.068267 479.982933 768.068267L479.982933 768.068267Z");
        register("custom","M827.733333 512l157.866667-157.866667c42.666667-42.666667 42.666667-110.933333 0-149.333333L819.2 38.4c-38.4-38.4-110.933333-38.4-149.333333 0L512 196.266667 354.133333 38.4C315.733333 0 243.2 0 204.8 38.4L38.4 204.8c-42.666667 42.666667-42.666667 110.933333 0 149.333333L196.266667 512 128 576c-12.8 12.8-21.333333 25.6-25.6 42.666667L8.533333 878.933333c-12.8 34.133333-4.266667 68.266667 12.8 93.866667 21.333333 25.6 51.2 42.666667 85.333334 42.666667 12.8 0 21.333333 0 34.133333-4.266667l260.266667-93.866667c17.066667-4.266667 29.866667-12.8 42.666666-25.6l64-64 157.866667 157.866667c21.333333 21.333333 46.933333 29.866667 76.8 29.866667s55.466667-12.8 76.8-29.866667l166.4-166.4c42.666667-42.666667 42.666667-110.933333 0-149.333333L827.733333 512z m-81.066666-422.4c4.266667 0 12.8 4.266667 17.066666 4.266667l166.4 166.4c8.533333 8.533333 8.533333 21.333333 0 29.866666l-59.733333 59.733334-196.266667-196.266667 59.733334-59.733333s4.266667-4.266667 12.8-4.266667zM200.533333 396.8l76.8-76.8c12.8-12.8 12.8-34.133333 0-46.933333s-34.133333-12.8-46.933333 0L153.6 349.866667 98.133333 294.4c-8.533333-8.533333-8.533333-21.333333 0-29.866667l166.4-166.4c4.266667-4.266667 8.533333-4.266667 17.066667-4.266666s12.8 4.266667 17.066667 4.266666L452.266667 256 256 452.266667 200.533333 396.8z m187.733334 439.466667s-4.266667 4.266667-8.533334 4.266666l-260.266666 93.866667c-8.533333 4.266667-17.066667 0-21.333334-8.533333-4.266667-4.266667-4.266667-8.533333-4.266666-17.066667l93.866666-260.266667c0-4.266667 4.266667-4.266667 4.266667-8.533333l93.866667-93.866667 256-256 85.333333-85.333333 196.266667 196.266667-85.333334 85.333333-256 256-93.866666 93.866667z m537.6-76.8l-166.4 166.4c-8.533333 8.533333-21.333333 8.533333-29.866667 0l-55.466667-55.466667 76.8-76.8c12.8-12.8 12.8-34.133333 0-46.933333s-34.133333-12.8-46.933333 0l-76.8 76.8-55.466667-55.466667 196.266667-196.266667 157.866667 157.866667c8.533333 8.533333 8.533333 21.333333 0 29.866667z");
    }

    public SvgIcon(String path) {
        this(path, Color.BLACK);
    }

    /**
     * 为指定的svg内容和颜色构造SVGGlyph节点
     */
    public SvgIcon(String path, Paint fill) {
        getStyleClass().setAll(DEFAULT_STYLE_CLASSES);
        setAlignment(Pos.CENTER_LEFT);
        this.icon = new Pane();
        this.setFill(fill);
        setPath(path);
        this.icon.setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
        this.getChildren().setAll(icon);
    }

    /**
     * 获取Svg的path 处理名字
     *
     * @return svg path
     */
    public String getPath() {
        if (StrUtils.isNotBlank(path) && path.charAt(0) == PATH_NAME_PREFIX) {
            return SVG_PATHS.getOrDefault(path.substring(1), path);
        }
        return path;
    }

    /**
     * 设置svg的path
     *
     * @param path svg path
     */
    public void setPath(String path) {
        this.path = path;
        if (getPath() != null && !getPath().isEmpty()) {
            SVGPath shape = new SVGPath();
            shape.setContent(getPath());
            this.icon.setShape(shape);
            setFill(getFill().get());
        }
    }

    /**
     * svg颜色属性
     */
    public void setFill(Paint fill) {
        fillProperty().set(fill);
    }

    public ObjectProperty<Paint> fillProperty() {
        if (fill == null) {
            this.fill = new SimpleStyleableObjectProperty<>(StyleableProperties.TEXT_FILL, SvgIcon.this, "-fx-text-fill", Color.BLACK) {
                @Override
                protected void invalidated() {
                    icon.setBackground(new Background(new BackgroundFill(fillProperty().get() == null ? Color.BLACK : getValue(), null, null)));
                }
            };
        }
        return fill;
    }

    /**
     * 将svg调整为一定的宽度和高度
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(double width, double height) {
        this.icon.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        this.icon.setPrefSize(width, height);
        this.icon.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
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

    /**
     * 获取size
     *
     * @return size
     */
    public double getSize() {
        return sizeProperty().get();
    }

    /**
     * 设置图标大小
     *
     * @param size 图标大小
     */
    public void setSize(double size) {
        this.sizeProperty().set(size);
    }

    /**
     * size 懒加载
     *
     * @return size
     */
    public DoubleProperty sizeProperty() {
        if (this.size == null) {
            size = new SimpleStyleableDoubleProperty(StyleableProperties.SIZE, SvgIcon.this, "size", Region.USE_COMPUTED_SIZE) {
                @Override
                public void invalidated() {
                    setSize(getValue(), getValue());
                }
            };
        }
        return size;
    }

    /**
     * css属性
     * <p>
     * -fx-icon-size 图标大小
     * <p>
     * -fx-text-fill 图标颜色
     */
    private static class StyleableProperties {
        private static final CssMetaData<SvgIcon, Number> SIZE = new CssMetaData<>("-fx-icon-size", SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
            @Override
            public boolean isSettable(SvgIcon control) {
                return control.size == null || !control.size.isBound();
            }

            @Override
            public StyleableDoubleProperty getStyleableProperty(SvgIcon control) {
                return (StyleableDoubleProperty) control.sizeProperty();
            }
        };
        private static final CssMetaData<SvgIcon, Paint> TEXT_FILL = new CssMetaData<>("-fx-text-fill", PaintConverter.getPaintConverter()) {
            @Override
            public boolean isSettable(SvgIcon control) {
                return control.fill == null || !control.fill.isBound();
            }

            @Override
            @SuppressWarnings("unchecked")
            public StyleableProperty<Paint> getStyleableProperty(SvgIcon control) {
                return (StyleableProperty<Paint>) control.fillProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Pane.getClassCssMetaData());
            Collections.addAll(styleables, SIZE, TEXT_FILL);
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
}
