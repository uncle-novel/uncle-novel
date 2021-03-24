package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import org.apache.commons.lang3.SystemUtils;

/**
 * Packager factory
 */
public class PackagerFactory {

  public static Packager createPackager(Platform platform) throws Exception {
    if (platform == Platform.auto || platform == null) {
      platform = Platform.getCurrentPlatform();
    }
    Packager packager;
    switch (platform) {
      case mac:
        packager = new MacPackager();
        break;
      case linux:
        packager = new LinuxPackager();
        break;
      case windows:
        packager = new WindowsPackager();
        break;
      default:
        throw new Exception(
            "Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION
                + " " + SystemUtils.OS_ARCH);
    }
    packager.setPlatform(platform);
    return packager;
  }

}
