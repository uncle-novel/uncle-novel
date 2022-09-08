package com.unclezs.gui.utils;

import javafx.application.Platform;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * 托盘工具
 *
 * @author uncle
 * @date 2019.07.30.
 */
@Slf4j
@UtilityClass
public class TrayUtil {
    private static final SystemTray TRAY;
    private static TrayIcon trayIcon;

    static {
        TRAY = SystemTray.getSystemTray();
        //设置右键菜单
        try {
            InputStream stream = TrayUtil.class.getResourceAsStream("/images/logo/tray.png");
            if (stream == null) {
                throw new RuntimeException("tray icon load failed.");
            }
            BufferedImage image = ImageIO.read(stream);
            trayIcon = new TrayIcon(image, "Uncle小说", null);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        Platform.runLater(() -> DataManager.currentStage.show());
                    }
                }
            });
            MenuItem exit = new MenuItem("exit");
            exit.addActionListener(e -> {
                Platform.exit();
                System.exit(0);
            });
            PopupMenu popupMenu = new PopupMenu();
            MenuItem show = new MenuItem("show");
            show.addActionListener(e -> {
                Platform.runLater(() -> {
                    Platform.setImplicitExit(true);
                    DataManager.currentStage.setIconified(false);
                    DataManager.currentStage.show();
                });
            });
            popupMenu.add(show);
            popupMenu.add(exit);
            trayIcon.setPopupMenu(popupMenu);
            TRAY.add(trayIcon);
        } catch (Exception e) {
            log.error("托盘初始化失败", e);
            e.printStackTrace();
        }
    }

    public void init() {
        //init
    }

    public static void tray() {
        Platform.setImplicitExit(false);
        DataManager.currentStage.setIconified(false);
        DataManager.currentStage.hide();
    }
}
