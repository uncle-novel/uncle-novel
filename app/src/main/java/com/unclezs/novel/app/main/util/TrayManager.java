package com.unclezs.novel.app.main.util;

import cn.hutool.core.util.ArrayUtil;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.App;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 托盘管理
 *
 * @author blog.unclezs.com
 * @date 2021/5/31 10:11
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
      BufferedImage image = ImageIO.read(ResourceUtils.stream("assets/logo/icon-16.png"));
      trayIcon = new TrayIcon(image, App.NAME, null);
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
      SystemTray.getSystemTray().remove(trayIcon);
    }
  }
}
