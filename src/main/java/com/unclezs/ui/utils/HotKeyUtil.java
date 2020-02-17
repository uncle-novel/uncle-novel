package com.unclezs.ui.utils;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.LogManager;

/**
 * 全局唤醒热键Alt+U
 * Created by Uncle
 * 2019.07.31.
 */
public class HotKeyUtil {
    private static boolean ALT_PRESSED = false;//alt键按下
    private static boolean U_PRESSED = false;//u键按下
    private static boolean IS_RESPONSE = false;//是否已经响应

    public static void bindListener() {
        LogManager.getLogManager().reset();
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    switch (e.getKeyCode()) {
                        case 22:
                            U_PRESSED = true;
                            break;
                        case 56:
                            ALT_PRESSED = true;
                            break;
                    }
                    if (ALT_PRESSED && U_PRESSED && !IS_RESPONSE) {//arl+U组合键一次按下只响应一次
                        Platform.runLater(() -> {
                            try {
                                if (DataManager.currentStage.isShowing()) {
                                    TrayUtil.tray();
                                } else {
                                    DataManager.currentStage.show();
                                }
                            } catch (Exception ee) {

                            }
                        });
                        IS_RESPONSE = true;
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    switch (e.getKeyCode()) {
                        case 22:
                            U_PRESSED = false;
                            break;
                        case 56:
                            ALT_PRESSED = false;
                            break;
                    }
                    IS_RESPONSE = false;
                }
            });
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}