package com.uncles.novel.app.jfx.ui.pages.home.views;

import com.uncles.novel.app.jfx.framework.annotation.FxView;
import com.uncles.novel.app.jfx.framework.hotkey.HotKeyManager;
import com.uncles.novel.app.jfx.framework.hotkey.HotKeyCombination;
import com.uncles.novel.app.jfx.framework.hotkey.KeyRecorder;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.NavigateBundle;
import com.uncles.novel.app.jfx.framework.ui.components.sidebar.SidebarNavigationView;
import com.unclezs.novel.analyzer.util.StringUtils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.KeyStroke;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 17:16
 */
@FxView(fxml = "/layout/home/views/search_novel.fxml")
public class SearchNovelView extends SidebarNavigationView {
    public Button button;
    public Text text;
    public Button clear;
    public Button showFalse;
    private List<Integer> list = new ArrayList<>();
    boolean show = false;
    Robot robot = null;
    private KeyRecorder recorder = new KeyRecorder();
    Map<String, KeyCodeCombination> keyCodeCombinationMap = new HashMap<>(16);


    @Override
    public void onShow(NavigateBundle bundle) {
        if (bundle.getData() != null) {
            text.setText("来自：" + bundle.getFrom() + "   携带数据：" + bundle.get("data"));
        }
    }

    @Override
    public void onCreated() {
        KeyCombination x = KeyCodeCombination.keyCombination(KeyCodeCombination.CONTROL_DOWN.toString() + " Q");
        System.out.println(x);
        showFalse.setOnMouseClicked(e -> show = false);
        clear.setOnMouseClicked(e -> HotKeyManager.clear());
        Platform.setImplicitExit(false);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        StackPane view = getView();
        Class<java.awt.event.KeyEvent> clazz = java.awt.event.KeyEvent.class;
        button.setOnMouseClicked(e -> {
            if (StringUtils.isNotBlank(text.getText())) {
                KeyStroke keyStroke = KeyStroke.getKeyStroke(text.getText());
                System.out.println("注册热键:" + keyStroke);
                if (keyStroke != null) {
                    HotKeyManager.register(keyStroke, hotKey -> {
                        System.out.println("监听到热键触发:" + hotKey.toString());
                    });
                }
            }
        });
        view.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (recorder.record(e)) {
                HotKeyCombination combination = recorder.getCombination();
                System.out.println("录入热键：" + combination.getText());
            }
            text.setText(recorder.getKeyText());
        });

    }
}
