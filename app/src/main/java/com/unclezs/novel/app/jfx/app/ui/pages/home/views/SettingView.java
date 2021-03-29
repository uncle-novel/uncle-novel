package com.unclezs.novel.app.jfx.app.ui.pages.home.views;

import cn.hutool.core.thread.ThreadUtil;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import com.unclezs.jfx.launcher.FxUtils;
import com.unclezs.novel.app.jfx.app.ui.app.App;
import com.unclezs.novel.app.jfx.framework.annotation.FxView;
import com.unclezs.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.unclezs.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationView;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/03/05 17:26
 */
@Slf4j
@FxView(fxml = "/layout/home/views/setting/setting.fxml")
public class SettingView extends SidebarNavigationView {

  @Override
  public void onCreated() {
    System.out.println("123");
  }

  @Override
  public void onShow(NavigateBundle bundle) {
    ThreadUtil.execute(() -> {
      ThreadUtil.sleep(1000);
      if (App.manifest != null && App.manifest.isNewVersion()) {
        FxUtils.runFx(() -> {
          JFXAlert<String> alert = new JFXAlert<>(App.app.getStage());
          alert.initModality(Modality.APPLICATION_MODAL);
          JFXDialogLayout layout = new JFXDialogLayout();
          StringBuilder sb = new StringBuilder();
          for (String s : App.manifest.getChangeLog()) {
            sb.append(s).append("\n");
          }
          layout.setBody(new Label(sb.toString()));
          alert.setContent(layout);
          alert.show();
        });
      }
    });
  }
}
