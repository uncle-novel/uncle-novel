package com.unclezs.ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author unclezs.com
 * @date 2019.07.03 14:07
 */
public class AlertUtil {
    public static Alert getAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(DataManager.mainStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.getDialogPane().setStyle("-fx-graphic: url('images/1.png');");
        alert.setTitle(title);
        return alert;
    }

    //设置时间
    public static Tooltip setTipTime(Tooltip tooltip) {
        try {
            Class tipClass = tooltip.getClass();
            Field f = tipClass.getDeclaredField("BEHAVIOR");
            f.setAccessible(true);
            Class behavior = Class.forName("javafx.scene.control.Tooltip$TooltipBehavior");
            Constructor constructor = behavior.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            f.set(behavior, constructor.newInstance(new Duration(100), new Duration(5000), new Duration(100), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tooltip;
    }
}
