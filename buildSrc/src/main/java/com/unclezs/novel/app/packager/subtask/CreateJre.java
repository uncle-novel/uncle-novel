package com.unclezs.novel.app.packager.subtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.system.SystemUtil;
import com.unclezs.novel.app.packager.model.Platform;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.JdkUtils;
import com.unclezs.novel.app.packager.util.Logger;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创建定制化JRE
 *
 * @author blog.unclezs.com
 * @date 2021/4/2 22:50
 */
public class CreateJre extends BaseSubTask {

  public static final String ALL_MODULE_PATH = "ALL-MODULE-PATH";
  public static final int JDK_13 = 13;
  public static final int JDK_9 = 9;
  private final File currentJdk = new File(System.getProperty("java.home"));

  public CreateJre() {
    super("创建JRE");
  }

  @Override
  public boolean enabled() {
    return packager.getBundleJre();
  }

  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  protected File run() throws Exception {
    boolean bundleJre = packager.getBundleJre();
    File customJreFolder = packager.getJrePath();
    Platform platform = packager.getPlatform();
    File destinationFolder = packager.getJreDestinationFolder();
    File jdkPath = packager.getJdkPath();

    FileUtil.del(destinationFolder);
    Logger.infoIndent("开始创建Jre  当前JDK：{}", currentJdk);
    // 自定义Jre
    if (customJreFolder != null) {
      Logger.info("使用自定义Jre: {}" + customJreFolder);
      // 如果找不到“发布”文件，则修复了MacOS上JRE的路径
      if (platform.equals(Platform.mac) && !FileUtil.exist(FileUtil.file(customJreFolder, "release"))) {
        customJreFolder = new File(customJreFolder, "Contents/Home");
        Logger.warn("修复Jre路径: " + customJreFolder);
      }
      // 检查是否指定了有效的jre
      if (!JdkUtils.isValidJre(platform, customJreFolder)) {
        Logger.error("Jre 不合法：{}", customJreFolder);
        throw new RuntimeException("Jre 不合法");
      }
      // 将JRE文件夹复制到捆绑包
      FileUtil.copy(customJreFolder, destinationFolder, true);
      // 设置jre中可执行文件的执行权限
      File binFolder = new File(destinationFolder, "bin");
      Arrays.asList(Objects.requireNonNull(binFolder.listFiles())).forEach(f -> f.setExecutable(true, false));
    } else if (JdkUtils.getJavaMajorVersion() <= 8) {
      Logger.error("JDK8以上才能增量生成JRE，请指定JrePath, 当前JDK版本：{}", SystemUtil.getJavaInfo().getVersion());
      throw new RuntimeException("JDK8以上才能增量生成JRE");
    } else if (!platform.isCurrentPlatform() && jdkPath.equals(currentJdk)) {
      Logger.warn("不能创建与当前操作系统不同的Jre. 当前操作系统：{}", platform);
      bundleJre = false;
    } else {
      Logger.info("开始创建定制化JRE ...");
      // 测试指定的JDK是否与目标平台使用相同的平台
      if (!JdkUtils.isValidJdk(platform, jdkPath)) {
        Logger.warn("非法JDK. 当前操作系统：{} , JDK: {}", platform, jdkPath);
        throw new RuntimeException("非法JDK");
      }
      // 使用模块生成定制的jre
      ExecUtils.create(new File(currentJdk, "/bin/jlink"))
        .add("--module-path", getModulePath())
        .add("--add-modules", getRequiredModules())
        .add("--output", destinationFolder)
        .add("--no-header-files --no-man-pages --strip-debug --compress=2")
        .exec();
      // 设置jre中可执行文件的执行权限
      File binFolder = new File(destinationFolder, "bin");
      Arrays.asList(Objects.requireNonNull(binFolder.listFiles())).forEach(f -> f.setExecutable(true, false));
    }
    if (bundleJre) {
      FileUtil.del(new File(destinationFolder, "legal"));
      Logger.infoUnIndent("JRE创建成功: {}", destinationFolder.getAbsolutePath());
    } else {
      Logger.infoUnIndent("跳过创建JRE");
    }
    return destinationFolder;
  }

  /**
   * 获取需要的模块
   *
   * @return 模块列表
   */
  protected String getRequiredModules() {
    if (!packager.getCustomizedJre()) {
      Logger.info("创建全量JRE，使用模块：{}", ALL_MODULE_PATH);
      return ALL_MODULE_PATH;
    }
    Logger.infoIndent("开始获取所需要的模块 ... ");
    File jdeps = new File(currentJdk, "/bin/jdeps");
    File jarLibs = null;
    if (FileUtil.exist(packager.getLibsFolder())) {
      jarLibs = new File(packager.getLibsFolder(), "*.jar");
    } else {
      Logger.warn("没有找到依赖!");
    }
    Set<String> modulesList;
    if (CollectionUtil.isNotEmpty(packager.getModules())) {
      modulesList = packager.getModules()
        .stream()
        .map(String::trim)
        .collect(Collectors.toSet());
    } else if (JdkUtils.getJavaMajorVersion() >= JDK_13) {
      String modules = ExecUtils.create(jdeps)
        .add("-q")
        .add("--multi-release", String.valueOf(JdkUtils.getJavaMajorVersion()))
        .add("--ignore-missing-deps --print-module-deps")
        .add("--module-path", getModulePath())
        .add(jarLibs).add(packager.getJarFile())
        .exec();
      modulesList = Arrays.stream(modules.split(","))
        .map(String::trim)
        .filter(module -> !module.isEmpty())
        .collect(Collectors.toSet());
    } else if (JdkUtils.getJavaMajorVersion() >= JDK_9) {
      String out = ExecUtils.create(jdeps)
        .add("-q")
        .add("--multi-release", String.valueOf(JdkUtils.getJavaMajorVersion()))
        .add("--list-deps")
        .add("--module-path", getModulePath().concat(";").concat(packager.getLibsFolder().getAbsolutePath()))
        .add(jarLibs).add(packager.getJarFile())
        .exec();
      modulesList = Arrays.stream(out.split("\n"))
        .map(String::trim)
        .map(module -> (module.contains("/") ? module.split("/")[0] : module))
        .filter(module -> !module.isEmpty() && !module.startsWith("JDK removed internal") && !module.startsWith("Picked"))
        .collect(Collectors.toSet());
    } else {
      modulesList = Collections.singleton(ALL_MODULE_PATH);
    }
    modulesList.addAll(packager.getAdditionalModules());
    if (modulesList.isEmpty()) {
      Logger.warn("未没有找到任何模块，使用全部模块");
      modulesList.add(ALL_MODULE_PATH);
    }
    Logger.infoUnIndent("最终得到使用的模块: {}", modulesList);
    return ArrayUtil.join(modulesList.toArray(new String[0]), ",");
  }

  /**
   * 获取模块路径
   *
   * @return 模块路径
   */
  private String getModulePath() {
    File modulesDir = new File(packager.getJdkPath(), "jmods");
    if (!modulesDir.exists()) {
      Logger.error("jmods 文件不存在: {}", modulesDir);
      throw new RuntimeException("jmods 文件不存在");
    }
    List<String> modulePathList = CollectionUtil.toList(modulesDir.getAbsolutePath());
    if (CollectionUtil.isNotEmpty(packager.getAdditionalModulePaths())) {
      packager.getAdditionalModulePaths().forEach(modulePath -> modulePathList.add(modulePath.getAbsolutePath()));
    }
    return ArrayUtil.join(modulePathList.toArray(new String[0]), packager.getPlatform() == Platform.win ? ";" : ":");
  }

}
