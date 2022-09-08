package com.unclezs.gui.utils;


import cn.hutool.core.lang.Dict;
import com.unclezs.gui.animation.RightTransition;
import com.unclezs.gui.app.App;
import com.unclezs.gui.controller.LifeCycleFxController;
import com.unclezs.gui.extra.FXController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 菜单内容切换显示工具
 * 生命周期 和路由数据传递
 *
 * @author unclezs.com
 * @date 2020.04.25 11:29
 */
public class ContentUtil {
    private static Map<String, Menu> caches = new HashMap<>(10);
    private static String currentPaneKey;

    /**
     * 设置内容面板
     *
     * @param controller 控制器
     */
    public static void show(Class<? extends LifeCycleFxController> controller, Node triggerBtn) {
        show(controller, Dict.create(), triggerBtn);
    }

    /**
     * 设置内容面板 不带触发按钮 不带数据
     *
     * @param controller 控制器
     */
    public static void show(Class<? extends LifeCycleFxController> controller) {
        show(controller, Dict.create());
    }

    /**
     * 设置内容面板 只带数据
     *
     * @param controller 控制器
     */
    public static void show(Class<? extends LifeCycleFxController> controller, Dict dict) {
        show(controller, dict, null);
    }

    /**
     * 设置内容面板
     *
     * @param controller 控制器
     * @param boundData  传递的数据
     * @param triggerBtn 触发显示得button或节点
     */
    public static void show(Class<? extends LifeCycleFxController> controller, Dict boundData, Node triggerBtn) {
        try {
            //解锁以前得菜单按钮
            Menu currentMenu = caches.get(currentPaneKey);
            if (currentMenu != null && currentMenu.menuBtn != null) {
                currentMenu.menuBtn.setDisable(false);
                //调用隐藏时候的生命周期方法
                currentMenu.controller.onHidden();
            }
            //开始跳转
            String key = controller.getAnnotation(FXController.class).value();
            Menu menu = init(controller);
            //禁用菜单按钮->携带数据->设置面板内容->开启渐入动画->更新当前菜单key
            if (menu.menuBtn != null) {
                menu.menuBtn.setDisable(true);
            } else if (triggerBtn != null) {
                menu.menuBtn = triggerBtn;
                menu.menuBtn.setDisable(true);
            }
            //开启动画
            RightTransition rightTransition = new RightTransition(menu.content);
            rightTransition.play();
            App.contentContainer.setCenter(menu.content);
            //调用显示时候的生命周期方法
            App.contentContainer.requestFocus();
            menu.controller.onShow(boundData);
            //更新当前的菜单key
            currentPaneKey = key;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取控制器
     *
     * @param clazz /
     * @param <T>   /
     * @return /
     * @throws IOException /
     */
    @SuppressWarnings("unchecked")
    public static <T extends LifeCycleFxController> T getController(Class<T> clazz) throws IOException {
        Menu menu = init(clazz);
        return (T) menu.controller;
    }

    /**
     * 初始化
     *
     * @param clazz /
     * @throws IOException 未找到FXML
     */
    private static Menu init(Class<? extends LifeCycleFxController> clazz) throws IOException {
        String key = clazz.getAnnotation(FXController.class).value();
        Menu menu = caches.get(key);
        if (menu == null) {
            menu = new Menu();
            FXMLLoader loader = ResourceUtil.getFxmlLoader(clazz);
            menu.content = loader.load();
            menu.controller = loader.getController();
            caches.put(key, menu);
        }
        return menu;
    }

    public static void destroy() {
        for (Map.Entry<String, Menu> entry : caches.entrySet()) {
            entry.getValue().getController().onDestroyed();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Menu {
        private Node content;
        private Node menuBtn;
        private LifeCycleFxController controller;
    }
}
