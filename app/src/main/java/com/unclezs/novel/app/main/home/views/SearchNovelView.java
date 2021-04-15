package com.unclezs.novel.app.main.home.views;

import com.jfoenix.controls.JFXToggleButton;
import com.tulskiy.keymaster.common.HotKey;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyCombination;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyListener;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.support.hotkey.KeyRecorder;
import com.unclezs.novel.app.main.home.HomeView;
import com.unclezs.novel.app.main.reader.ReaderView;
import com.unclezs.novel.app.main.util.DebugUtils;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.EqualsAndHashCode;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search_novel.fxml")
@EqualsAndHashCode(callSuper = true)
public class SearchNovelView extends SidebarView<StackPane> {

  private final KeyRecorder recorder = new KeyRecorder();
  public Text text;
  public Button clear;
  public Button showFalse;
  public Button window;
  public Button cancelWindow;
  public Button global;
  public Button cancelGlobal;
  public VBox box;
  public JFXToggleButton debug;
  HotKeyCombination combination;


  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    if (bundle.getData() != null) {
      text.setText("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
    }
  }

  @Override
  public void onCreated() {
    debug.selectedProperty().addListener(e -> {
      if (debug.isSelected()) {
        DebugUtils.debug();
      } else {
        DebugUtils.init();
      }
    });
    StackPane view = getRoot();
    showFalse.setOnMouseClicked(e -> box.getChildren().addAll());
    clear.setOnMouseClicked(e -> {
      HomeView homeView = AppContext.getView(HomeView.class);
      homeView.getApp().navigate(ReaderView.class);
    });
    window.setOnMouseClicked(e -> {
      if (combination != null) {
        combination.setListener(new HotKeyListener() {
          @Override
          public void onWindowHotKey(HotKeyCombination hotKey) {
            System.out.println(hotKey.getText());
          }
        });
        HotKeyManager.register(combination);
      }
    });
    cancelWindow.setOnMouseClicked(event -> HotKeyManager.unregister(text.getText()));
    global.setOnMouseClicked(e -> {
      if (combination != null) {
        combination.setListener(new HotKeyListener() {
          @Override
          public void onHotKey(HotKey hotKey) {
            System.out.println(hotKey);
          }
        });
        HotKeyManager.registerGlobal(combination);
      }
    });
    cancelGlobal.setOnMouseClicked(e -> HotKeyManager.unregisterGlobal(text.getText()));
    HotKeyManager.bindWindowHotKeyListener(AppContext.getStage());
    view.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (recorder.record(e)) {
        combination = recorder.getCombination();
      } else {
        combination = null;
      }
      text.setText(recorder.getKeyText());
    });

  }
}
