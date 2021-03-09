package com.uncles.novel.app.jfx.framework.hotkey.listener;

import com.tulskiy.keymaster.common.HotKey;
import com.uncles.novel.app.jfx.framework.hotkey.HotKeyCombination;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/03/09 11:02
 */
public interface HotKeyListener extends com.tulskiy.keymaster.common.HotKeyListener {
    /**
     * 全局热键触发时回调，
     *
     * @param hotKey 热键
     */
    @Override
    default void onHotKey(HotKey hotKey) {
    }

    /**
     * 窗口热键触发时回调
     *
     * @param hotKey 热键
     */
    default void onWindowHotKey(HotKeyCombination hotKey) {

    }
}
