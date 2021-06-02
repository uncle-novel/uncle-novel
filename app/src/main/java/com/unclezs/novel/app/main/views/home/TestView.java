package com.unclezs.novel.app.main.views.home;

import com.jfoenix.controls.JFXToggleButton;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.FluentTask;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyCombination;
import com.unclezs.novel.app.framework.support.hotkey.KeyRecorder;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/test.fxml")
@EqualsAndHashCode(callSuper = true)
public class TestView extends SidebarView<StackPane> {

  private final KeyRecorder recorder = new KeyRecorder();
  public Button warn;
  public Button info;
  public Button success;
  public Button error;
  public Button input;
  public Button confirm;
  public Button none;
  public Button loading;
  public Text text;
  public Button clear;
  public Button showFalse;
  public Button window;
  public Button cancelWindow;
  public Button global;
  public Button cancelGlobal;
  public VBox box;
  public JFXToggleButton debug;
  /**
   * 吐司
   */
  public Button toastSuccess;
  public Button toastError;
  public Button toastWarn;
  public Button toastInfo;
  HotKeyCombination combination;

  @Override
  public void onCreated() {
    initDialog();
    initLoading();
    initToast();
  }

  private void initLoading() {
    loading.setOnMouseClicked(e -> {
      new FluentTask<String>() {
        @Override
        protected String call() throws InterruptedException {
          Thread.sleep(2000);
          return "success";
        }
      }.onSuccess(System.out::println).start();
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

  void initToast() {
    toastInfo.setOnMouseClicked(e -> Toast.info("我是提示信息吐司"));
    toastError.setOnMouseClicked(e -> Toast.error("我是错误吐司"));
    toastSuccess.setOnMouseClicked(e -> Toast.success("我是成功吐司"));
    toastWarn.setOnMouseClicked(e -> Toast.warn("我是警告吐司"));
  }

}
