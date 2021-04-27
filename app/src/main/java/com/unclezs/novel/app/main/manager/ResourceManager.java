package com.unclezs.novel.app.main.manager;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import lombok.experimental.UtilityClass;

/**
 * 资源管理器
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 0:35
 */
@UtilityClass
public class ResourceManager {

  private static final String WORK_DIR = FileUtil.normalize(new File(".").getAbsolutePath());
  /**
   * 配置文件文件夹
   */
  public static final File CONF_DIR = FileUtil.file(WORK_DIR, "conf");
  /**
   * 插件文件夹
   */
  public static final File PLUGINS_DIR = FileUtil.file(WORK_DIR, "plugins");
  /**
   * 执行文件文件夹
   */
  public static final File BIN_DIR = FileUtil.file(WORK_DIR, "bin");

  /**
   * 获取配置文件目录下的文件
   *
   * @param path 文件路径
   * @return 文件
   */
  public static File confFile(String path) {
    return FileUtil.file(CONF_DIR, path);
  }

  /**
   * 获取配置文件目录下的文件
   *
   * @param path 文件路径
   * @return 文件
   */
  public static String readConfFile(String path) {
    return FileUtil.readUtf8String(FileUtil.file(CONF_DIR, path));
  }

  /**
   * 获取配置文件目录下的文件
   *
   * @param path 文件路径
   */
  public static void saveConfFile(String path, String content) {
    FileUtil.writeUtf8String(content, FileUtil.file(CONF_DIR, path));
  }

  public static File binFile(String path) {
    return FileUtil.file(BIN_DIR, path);
  }

  public static File pluginsFile(String path) {
    return FileUtil.file(PLUGINS_DIR, path);
  }
}
