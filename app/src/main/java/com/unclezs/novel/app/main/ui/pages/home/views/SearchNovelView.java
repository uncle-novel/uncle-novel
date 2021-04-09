package com.unclezs.novel.app.main.ui.pages.home.views;

import cn.hutool.core.util.RuntimeUtil;
import com.jfoenix.controls.JFXToggleButton;
import com.tulskiy.keymaster.common.HotKey;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.SvgIcon;
import com.unclezs.novel.app.framework.components.sidebar.NavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigationView;
import com.unclezs.novel.app.framework.hotkey.HotKeyCombination;
import com.unclezs.novel.app.framework.hotkey.HotKeyListener;
import com.unclezs.novel.app.framework.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.hotkey.KeyRecorder;
import com.unclezs.novel.app.main.ui.app.App;
import com.unclezs.novel.app.main.util.DebugUtils;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search_novel.fxml")
public class SearchNovelView extends SidebarNavigationView {

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
  public void onShow(NavigateBundle bundle) {
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
    StackPane view = getView();
    showFalse.setOnMouseClicked(
      e -> box.getChildren().addAll(new SvgIcon("_close"), new SvgIcon("_minimize")));
    clear.setOnMouseClicked(e -> {
      String home = System.getProperty("app.java.home");
      RuntimeUtil.exec(String.format("%s/runtime/bin/java", home), "-jar", "");
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
    HotKeyManager.bindWindowHotKeyListener(App.app.getStage());
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
