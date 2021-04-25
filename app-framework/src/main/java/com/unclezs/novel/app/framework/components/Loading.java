package com.unclezs.novel.app.framework.components;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.NodeHelper;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * loading组件
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 21:31
 */
public class Loading extends JFXAlert<Object> {

  public Loading(Window window) {
    super(window);
  }

  public Loading() {
    super(AppContext.getInstance().getPrimaryStage());
    setOverlayClose(false);
    JFXButton cancel = new JFXButton("取消");
    cancel.setOnAction(e -> hideWithAnimation());
    setContent(NodeHelper.addClass(new VBox(new JFXSpinner(), cancel), "loading"));
  }
}
