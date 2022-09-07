package com.unclezs.novel.app.main.manager;

import cn.hutool.core.util.ArrayUtil;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.App;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * 托盘管理
 *
 * @author blog.unclezs.com
 * @since 2021/5/31 10:11
 */
@Slf4j
@UtilityClass
public class TrayManager {

  private static TrayIcon trayIcon;

  /**
   * 初始化系统托盘
   */
  public static void init() {
    try {
      BufferedImage image = ImageIO.read(ResourceUtils.stream("assets/logo/icon-48.png"));
      // 托盘菜单
      PopupMenu popupMenu = new PopupMenu();
      MenuItem show = new MenuItem("show");
      show.addActionListener(e -> Executor.runFx(App::requestShow));
      MenuItem exit = new MenuItem("exit");
      exit.addActionListener(e -> Executor.runFx(App::stopApp));
      popupMenu.add(show);
      popupMenu.add(exit);

      trayIcon = new TrayIcon(image, App.NAME, null);
      trayIcon.setPopupMenu(popupMenu);
      trayIcon.setImageAutoSize(true);
      trayIcon.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);
          if (e.getButton() == MouseEvent.BUTTON1) {
            Executor.runFx(App::requestShow);
          }
        }
      });
      // 添加到托盘
      tray();
    } catch (Exception e) {
      log.error("托盘初始化失败", e);
    }
  }

  /**
   * 隐藏到托盘
   */
  public static void tray() {
    try {
      if (trayIcon != null && !ArrayUtil.contains(SystemTray.getSystemTray().getTrayIcons(), trayIcon)) {
        SystemTray.getSystemTray().add(trayIcon);
      }
    } catch (AWTException e) {
      log.error("托盘添加失败", e);
    }
  }

  /**
   * 隐藏托盘
   */
  public static void hide() {
    if (trayIcon != null) {
      Executor.run(() -> SystemTray.getSystemTray().remove(trayIcon));
    }
  }
}
