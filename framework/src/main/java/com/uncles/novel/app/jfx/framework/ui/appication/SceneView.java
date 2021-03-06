package com.uncles.novel.app.jfx.framework.ui.appication;

import com.uncles.novel.app.jfx.framework.ui.components.decorator.StageDecorator;
import com.uncles.novel.app.jfx.framework.ui.view.BaseView;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景View个标记 {@link com.uncles.novel.app.jfx.framework.ui.appication.SsaApplication}
 *
 * @author blog.unclezs.com
 * @since 2021/03/04 12:03
 */
public abstract class SceneView extends BaseView implements StageDecorator.ActionHandler {
    @Getter
    @Setter
    private Parent view;

    /**
     * 场景显示时候触发(场景view切换) 窗口隐藏不会被调用
     *
     * @param bundle 携带的数据
     */
    public void onShow(SceneViewNavigateBundle bundle) {

    }

    /**
     * 被隐藏(场景view切换) 窗口隐藏不会被调用
     */
    public void onHidden() {

    }

    /**
     * 场景view被销毁时调用
     */
    public void onDestroy() {

    }

}
