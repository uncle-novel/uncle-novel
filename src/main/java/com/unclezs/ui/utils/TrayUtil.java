package com.unclezs.ui.utils;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * 托盘工具
 * Created by Uncle
 * 2019.07.30.
 */
public class TrayUtil {
    private static SystemTray tray;
    private static MenuItem exit;
    private static MenuItem open;
    private static TrayIcon trayIcon;

    static {
        tray = SystemTray.getSystemTray();
        //设置右键菜单
        try {
            exit = new MenuItem("Exit");
            open = new MenuItem("Open");
            BufferedImage image = ImageIO.read(TrayUtil.class.getResourceAsStream("/images/图标/U圆.png"));
            PopupMenu pm = new PopupMenu();
            pm.add(exit);
            pm.add(open);
            trayIcon = new TrayIcon(image, "Uncle小说", pm);
            trayIcon.setImageAutoSize(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tray() throws Exception {
        DataManager.currentStage.hide();
        DataManager.currentStage.setIconified(false);
        Platform.setImplicitExit(false);
        //右键菜单监听
        exit.addActionListener(e -> {
            //点击右键菜单退出程序
            System.exit(0);
        });
        open.addActionListener(e -> {
            Platform.runLater(() -> {
                tray.remove(trayIcon);
                DataManager.currentStage.show();
            });
        });
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Platform.runLater(() -> {
                        DataManager.currentStage.show();
                        tray.remove(trayIcon);
                    });
                }
            }
        });
        DataManager.currentStage.setOnShown(e -> {
            tray.remove(trayIcon);
        });
        tray.add(trayIcon);

    }
}