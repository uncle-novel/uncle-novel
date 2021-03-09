package com.uncles.novel.app.jfx.ui.pages.home.views;

import com.tulskiy.keymaster.common.HotKey;
import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.hotkey.HotKeyCombination;
import com.uncles.novel.app.jfx.framework.hotkey.HotKeyManager;
import com.uncles.novel.app.jfx.framework.hotkey.KeyRecorder;
import com.uncles.novel.app.jfx.framework.hotkey.listener.HotKeyListener;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationView;
import com.uncles.novel.app.jfx.ui.app.App;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search_novel.fxml")
public class SearchNovelView extends SidebarNavigationView {
    public Text text;
    public Button clear;
    public Button showFalse;
    public Button window;
    public Button cancelWindow;
    public Button global;
    public Button cancelGlobal;
    private final KeyRecorder recorder = new KeyRecorder();
    HotKeyCombination combination;


    @Override
    public void onShow(NavigateBundle bundle) {
        if (bundle.getData() != null) {
            text.setText("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
        }
    }

    @Override
    public void onCreated() {
        StackPane view = getView();
        clear.setOnMouseClicked(e -> HotKeyManager.clearGlobal());
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
        cancelGlobal.setOnMouseClicked(e-> HotKeyManager.unregisterGlobal(text.getText()));
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
