package com.unclezs.novel.app.jfx.framework.util;


import javafx.scene.Node;
import javafx.scene.layout.Region;


/**
 * 视图组件创建工具
 *
 * @author blog.unclezs.com
 * @since 2021/03/02 14:41
 */
public class ViewUtils {

    /**
     * 给view设置css class
     *
     * @param node       节点
     * @param classNames 类名列表
     */
    public static <T extends Node> T addClass(T node, String... classNames) {
        if (classNames != null) {
            node.getStyleClass().addAll(classNames);
        }
        return node;
    }

    /**
     * 给view设置css class
     *
     * @param styleSheets class样式路径
     * @param region      节点
     * @param classNames  类名列表
     */
    @SuppressWarnings("unused")
    public static <T extends Region> T addStyleSheetAndClass(T region, String styleSheets, String... classNames) {
        if (styleSheets != null) {
            region.getStylesheets().add(styleSheets);
        }
        addClass(region, classNames);
        return region;
    }

}
