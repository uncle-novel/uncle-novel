package com.unclezs.novel.app.framework.components;

import com.jfoenix.controls.JFXSpinner;
import com.unclezs.novel.app.framework.util.ImageLoader;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import lombok.Setter;

/**
 * 带Loading的ImageView
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 17:48
 */
public class LoadingImageView extends StackPane {

  public static final String DEFAULT_STYLE_CLASS = "loading-image-view";
  public static final String LOADING_STYLE_CLASS = "loading";
  private final JFXSpinner loading = new JFXSpinner();
  @Setter
  private Image defaultImage;
  private ImageView imageView;

  public LoadingImageView(Image defaultImage) {
    this(defaultImage, Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
  }

  public LoadingImageView() {
    this(null);
  }

  public LoadingImageView(Image defaultImage, double width, double height) {
    this.defaultImage = defaultImage;
    setPrefSize(width, height);
    setMaxSize(width, height);
    setMinSize(width, height);
    NodeHelper.addClass(this, DEFAULT_STYLE_CLASS);
    // loading
    loading.setRadius(10);
  }

  /**
   * @param image 图片
   */
  private void setImage(Image image) {
    if (imageView == null) {
      imageView = new ImageViewPlus(image, getMaxWidth(), getMaxHeight());
    } else {
      imageView.setImage(image);
    }
    getChildren().setAll(imageView);
  }

  /**
   * 设置图片
   *
   * @param url 图片链接
   */
  public void setImage(String url) {
    getChildren().setAll(loading);
    NodeHelper.addClass(this, LOADING_STYLE_CLASS);
    ImageLoader.load(url, defaultImage, image -> {
      setImage(image);
      getStyleClass().remove(LOADING_STYLE_CLASS);
    });
  }
}
