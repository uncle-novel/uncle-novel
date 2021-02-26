package com.uncles.novel.app.jfx.framework.lifecycle;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 16:02
 */
public interface LifeCycle {
    void onCreated();

    void onHidden();

    void onShow();

    void onDestroy();
}
