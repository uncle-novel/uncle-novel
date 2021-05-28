package com.unclezs.novel.app.framework.components;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import lombok.Setter;

/**
 * loading组件
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 21:31
 */
public class Loading extends JFXAlert<Object> {

  private static final Image LOADING_IMAGE = new Image(ResourceUtils.externalForm("images/loading.gif"));
  @Setter
  private Runnable onCancel;

  public Loading(Window window) {
    super(window);
  }

  public Loading() {
    super(AppContext.getInstance().getPrimaryStage());
    setOverlayClose(false);
    JFXButton cancel = new JFXButton("取消");
    cancel.setOnAction(e -> {
      if (onCancel != null) {
        onCancel.run();
      }
      hideWithAnimation();
    });
    setContent(NodeHelper.addClass(new VBox(new ImageView(LOADING_IMAGE), cancel), "loading"));
  }
}
