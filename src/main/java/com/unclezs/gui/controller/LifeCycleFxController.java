package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;

/**
 * 带生命周期的控制器  需要配和ContentUtil进行切换
 *
 * @author uncle
 * @date 2020/4/24 23:34
 */
public interface LifeCycleFxController {
    /**
     * 每次显示
     *
     * @param data 携带数据
     */
    default void onShow(Dict data) {
    }

    /**
     * 每次被隐藏
     */
    default void onHidden() {
    }

    /**
     * 首次加载
     */
    void initialize();

    /**
     * 程序销毁时候
     */
    default void onDestroyed() {
    }
}
