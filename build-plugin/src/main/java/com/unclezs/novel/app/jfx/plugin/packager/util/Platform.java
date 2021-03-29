package com.unclezs.novel.app.jfx.plugin.packager.util;

import cn.hutool.system.SystemUtil;
import com.unclezs.novel.app.jfx.plugin.packager.packager.LinuxPackager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.MacPackager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.WindowsPackager;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 18:46
 */
public enum Platform {
  /**
   * linux
   */
  linux(new LinuxPackager()),
  /**
   * mac
   */
  mac(new MacPackager()),
  /**
   * windows
   */
  windows(new WindowsPackager()),
  /**
   * 自动识别
   */
  auto(null);

  private final Packager packager;

  Platform(Packager packager) {
    if (packager == null) {
      packager = getCurrentPlatform().getPackager();
    }
    this.packager = packager;
    this.packager.setPlatform(this);
  }

  /**
   * 获取当前操作系统
   *
   * @return 当前操作系统
   */
  public static Platform getCurrentPlatform() {
    if (SystemUtil.getOsInfo().isWindows()) {
      return windows;
    }
    if (SystemUtil.getOsInfo().isLinux()) {
      return linux;
    }
    if (SystemUtil.getOsInfo().isMac()) {
      return mac;
    }
    throw new RuntimeException("不支持的操作系统");
  }

  /**
   * 是否为当前平台
   *
   * @return true 是
   */
  public boolean isCurrentPlatform() {
    if (this == auto) {
      return true;
    }
    return this == getCurrentPlatform();
  }

  /**
   * 获取平台的打包器
   *
   * @return 打包器
   */
  public Packager getPackager() {
    return packager;
  }
}
