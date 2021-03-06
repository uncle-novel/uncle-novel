package com.uncles.novel.app.jfx.framework.ui.components.sidebar;


import com.uncles.novel.app.jfx.framework.ui.view.BaseView;
import com.uncles.novel.app.jfx.framework.util.FxmlLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 仅仅标记 为左侧导航栏的视图
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 14:12
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SidebarNavigationView extends BaseView {

    public static final String DEFAULT_STYLE_CLASS = "sidebar-nav-content";

    /**
     * 页面显示之前 处理数据
     *
     * @param bundle 页面跳转数据
     */
    public void onShow(NavigateBundle bundle) {

    }

    /**
     * 页面隐藏触发
     */
    public void onHidden() {

    }

    public void navigate(Class<? extends SidebarNavigationView> target, NavigateBundle bundle) {
        bundle.setFrom(getClass().getName());
        Node view = getView();
        SidebarNavigationPane sidebarNavigationPane = (SidebarNavigationPane) view.getParent();
        sidebarNavigationPane.navigateTo(target, bundle);
    }

    /**
     * load view . must inherit SidebarNavigationView
     *
     * @return Node
     */
    public static SidebarNavigationView loadView(Class<? extends SidebarNavigationView> viewClass) {
        SidebarNavigationView navigationView = FxmlLoader.load(viewClass);
        // 自适应宽度
        Node view = navigationView.getView();
        HBox.setHgrow(view, Priority.ALWAYS);
        view.getStyleClass().add(DEFAULT_STYLE_CLASS);
        return navigationView;
    }
}
