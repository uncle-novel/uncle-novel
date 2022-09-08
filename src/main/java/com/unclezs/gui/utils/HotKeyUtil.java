package com.unclezs.gui.utils;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

/**
 * 全局唤醒热键Alt+U
 * https://github.com/tulskiy/jkeymaster
 *
 * @author uncle
 * @date 2019.07.31
 */
@Slf4j
@UtilityClass
public class HotKeyUtil {
    //todo 增加自定义快捷键
    private static final Map<String, HotKeyListener> KEYMAP = new HashMap<>(1);
    private static final Provider PROVIDER = Provider.getCurrentProvider(false);

    static {
        KEYMAP.put("alt U", new BossKeyListener());
    }

    /**
     * 初始化热键
     */
    public void init() {
        PROVIDER.reset();
        for (String keyText : KEYMAP.keySet()) {
            register(keyText, KEYMAP.get(keyText));
        }
    }

    /**
     * 停用热键
     */
    public void unbind() {
        PROVIDER.reset();
        PROVIDER.stop();
    }

    /**
     * 注册热键
     *
     * @param keyText  热键
     * @param listener 回调
     */
    public void register(String keyText, HotKeyListener listener) {
        PROVIDER.register(KeyStroke.getKeyStroke(keyText), listener);
    }

    /**
     * 老板键
     */
    static class BossKeyListener implements HotKeyListener {

        @Override
        public void onHotKey(HotKey hotKey) {
            Platform.runLater(() -> {
                try {
                    if (DataManager.currentStage.isShowing()) {
                        TrayUtil.tray();
                    } else {
                        DataManager.currentStage.show();
                    }
                } catch (Exception e) {
                    log.error("热键唤醒/隐藏窗口显示失败", e);
                }
            });
        }
    }
}
