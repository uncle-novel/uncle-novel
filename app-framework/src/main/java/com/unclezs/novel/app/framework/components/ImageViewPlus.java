package com.unclezs.novel.app.framework.components;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.image.ImageView;

/**
 * 支持css指定fit-width,fit-height
 *
 * @author blog.unclezs.com
 * @date 2021/03/03 0:14
 */
public class ImageViewPlus extends ImageView {

  /**
   * 宽
   */
  private DoubleProperty fitWidth;
  /**
   * 高
   */
  private DoubleProperty fitHeight;

  /**
   * @return The CssMetaData associated with this class, which may include the CssMetaData of its
   * superclasses.
   * @since JavaFX 8.0
   */
  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return StyleableProperties.STYLEABLES;
  }

  public DoubleProperty fitToWidthProperty() {
    if (this.fitWidth == null) {
      this.fitWidth = new SimpleStyleableDoubleProperty(StyleableProperties.FIT_WIDTH, this, "-fx-fit-width") {
        @Override
        protected void invalidated() {
          setFitWidth(getValue());
        }
      };
    }
    return fitWidth;
  }

  public DoubleProperty fitToHeightProperty() {
    if (this.fitHeight == null) {
      this.fitHeight = new SimpleStyleableDoubleProperty(StyleableProperties.FIT_WIDTH, this, "-fx-fit-height") {
        @Override
        protected void invalidated() {
          setFitHeight(getValue());
        }
      };
    }
    return fitHeight;
  }

  /**
   * {@inheritDoc}
   *
   * @return the CssMetaData associated with this class, which may include the CssMetaData of its
   * super classes.
   * @since JavaFX 8.0
   */
  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  private static class StyleableProperties {

    private static final CssMetaData<ImageViewPlus, Number> FIT_WIDTH =
      new CssMetaData<>("-fx-fit-width", SizeConverter.getInstance()) {
        @Override
        public boolean isSettable(ImageViewPlus n) {
          return n.fitWidth == null || !n.fitWidth.isBound();
        }

        @Override
        @SuppressWarnings("unchecked")
        public StyleableProperty<Number> getStyleableProperty(ImageViewPlus n) {
          return (StyleableProperty<Number>) n.fitToWidthProperty();
        }
      };
    private static final CssMetaData<ImageViewPlus, Number> FIT_HEIGHT =
      new CssMetaData<>("-fx-fit-height", SizeConverter.getInstance()) {
        @Override
        public boolean isSettable(ImageViewPlus n) {
          return n.fitHeight == null || !n.fitHeight.isBound();
        }

        @Override
        @SuppressWarnings("unchecked")
        public StyleableProperty<Number> getStyleableProperty(ImageViewPlus n) {
          return (StyleableProperty<Number>) n.fitToHeightProperty();
        }
      };
    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
      final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
        ImageView.getClassCssMetaData());
      Collections.addAll(styleables, FIT_HEIGHT, FIT_WIDTH);
      STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }
}
