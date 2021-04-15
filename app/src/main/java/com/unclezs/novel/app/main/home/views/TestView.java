package com.unclezs.novel.app.main.home.views;

import cn.hutool.core.thread.ThreadUtil;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/test.fxml")
@EqualsAndHashCode(callSuper = true)
public class TestView extends SidebarView<StackPane> {

  public Button warn;
  public Button info;
  public Button success;
  public Button error;
  public Button input;
  public Button confirm;
  public Button none;
  public Button loading;

  @Override
  public void onCreated() {
    initDialog();
    initLoading();
  }

  private void initLoading() {
    loading.setOnMouseClicked(e -> {
      TaskFactory.createLoadingTask(task -> {
        ThreadUtil.sleep(5000);
        if (task.isCancelled()) {
          return null;
        }
        return null;
      }).onSuccess(System.out::println).start();
    });
  }

  void initDialog() {
    none.setOnMouseClicked(e -> ModalBox.none().title("更新内容").message("123\nabc\nsdassd").cancel("了解了").show());
    warn.setOnMouseClicked(e -> ModalBox.warn().message("这是在警告你？").show());
    info.setOnMouseClicked(e -> ModalBox.info().message("这是在警告你？").show());
    success.setOnMouseClicked(e -> ModalBox.success().message("这是在警告你？").show());
    error.setOnMouseClicked(e -> ModalBox.error().message("这是在警告你？").show());
    confirm.setOnMouseClicked(e -> ModalBox.confirm(System.out::println).message("这是在警告你？").show());
    input.setOnMouseClicked(e -> ModalBox.input(System.out::println).title("请输入要修改的昵称").show());
  }
}
