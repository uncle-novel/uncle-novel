package com.unclezs.gui.utils;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author uncle
 * @date 2020/4/30 22:39
 */
@SuppressWarnings("unchecked")
public class ToolTipUtil {

    /**
     * 设置显示时间 为立即显示
     */
    public static void init() {
        Class tipClass = Tooltip.class;
        try {
            Field f = tipClass.getDeclaredField("BEHAVIOR");
            f.setAccessible(true);
            Class behavior = Class.forName("javafx.scene.control.Tooltip$TooltipBehavior");
            Constructor constructor =
                behavior.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            f.set(behavior, constructor.newInstance(new Duration(0), new Duration(5000), new Duration(100), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
