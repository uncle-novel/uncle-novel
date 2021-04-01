package com.unclezs.novel.app.jfx.packager.util;

import cn.hutool.system.SystemUtil;
import com.unclezs.novel.app.jfx.packager.model.PlatformConfig;
import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;
import com.unclezs.novel.app.jfx.packager.packager.LinuxPackager;
import com.unclezs.novel.app.jfx.packager.packager.MacPackager;
import com.unclezs.novel.app.jfx.packager.packager.PackagerExtension;
import com.unclezs.novel.app.jfx.packager.packager.WindowsPackager;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 18:46
 */
@Getter
public enum Platform {
  /**
   * linux
   */
  linux,
  /**
   * mac
   */
  mac,
  /**
   * windows
   */
  windows,
  /**
   * 自动识别
   */
  auto;

  /**
   * 平台图标类型
   */
  private String iconType;
  /**
   * 打包器
   */
  private AbstractPackager packager;
  /**
   * 平台配置
   */
  private PlatformConfig platformConfig;

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
   * 创建平台打包器
   *
   * @return 平台相关的打包器
   */
  public AbstractPackager createPackager(PackagerExtension extension) {
    switch (this) {
      case mac:
        iconType = "icns";
        packager = new MacPackager();
        platformConfig = extension.getMacConfig();
        packager.setPlatform(mac);
        break;
      case windows:
        iconType = "ico";
        packager = new WindowsPackager();
        packager.setPlatform(windows);
        platformConfig = extension.getWinConfig();
        break;
      case linux:
        iconType = "png";
        packager = new LinuxPackager();
        packager.setPlatform(linux);
        platformConfig = extension.getLinuxConfig();
        break;
      default:
        packager = getCurrentPlatform().getPackager();
    }
    return packager;
  }

  /**
   * 获取平台相关配置
   *
   * @param <T> 类型
   * @return 配置
   */
  @SuppressWarnings("unchecked")
  public <T extends PlatformConfig> T getPlatformConfig() {
    return (T) platformConfig;
  }
}
