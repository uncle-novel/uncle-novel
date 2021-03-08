package com.uncles.novel.app.jfx.framework.hotkey;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.swing.KeyStroke;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 全局唤醒热键Alt+U
 * https://github.com/tulskiy/jkeymaster
 *
 * @author uncle
 * @date 2019.07.31
 */
@Slf4j
@UtilityClass
public class HotKeyManager {
    private static final Map<String, HotKeyListener> KEYMAP = new HashMap<>(1);
    private static final Provider PROVIDER = Provider.getCurrentProvider(true);
    private static final Set<HotKeyCombination> HOT_KEY_COMBINATIONS = new HashSet<>(16);


    static {
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
     * 清空全局热键
     */
    public void clear() {
        PROVIDER.reset();
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
     * 注册热键
     *
     * @param keyStroke 热键
     * @param listener  回调
     */
    public void register(KeyStroke keyStroke, HotKeyListener listener) {
        PROVIDER.register(keyStroke, listener);
    }

    /**
     * 取消注册热键
     *
     * @param keyStroke 热键
     */
    public void unregister(KeyStroke keyStroke) {
        PROVIDER.unregister(keyStroke);
    }

    public void register(HotKeyCombination combination, boolean global) {

    }
}
