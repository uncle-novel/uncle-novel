package com.unclezs.novel.app.jfx.packager.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.system.SystemUtil;
import com.unclezs.novel.app.jfx.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.jfx.packager.util.CommandUtils;
import com.unclezs.novel.app.jfx.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.packager.util.JavaUtils;
import com.unclezs.novel.app.jfx.packager.util.JdkUtils;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 创建定制化JRE
 *
 * @author blog.unclezs.com
 * @date 2021/4/2 22:50
 */
public class CreateJre extends BaseSubTask {

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
    File libsFolder = packager.getLibsFolder();
    boolean customizedJre = packager.getCustomizedJre();
    File jarFile = packager.getJarFile();
    List<String> requiredModules = packager.getModules();
    List<String> additionalModules = packager.getAdditionalModules();
    List<File> additionalModulePaths = packager.getAdditionalModulePaths();

    File currentJdk = new File(System.getProperty("java.home"));
    FileUtil.del(destinationFolder);

    Logger.infoIndent("开始创建Jre  当前JDK：{}", currentJdk);
    // 自定义Jre
    if (customJreFolder != null) {
      Logger.info("使用自定义Jre: {}" + customJreFolder);
      // 如果找不到“发布”文件，则修复了MacOS上JRE的路径
      if (platform.equals(Platform.mac) && !FileUtils.folderContainsFile(customJreFolder, "release")) {
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
    } else if (JavaUtils.getJavaMajorVersion() <= 8) {
      Logger.error("JDK8以上才能增量生成JRE，请指定JrePath, 当前JDK版本：{}", SystemUtil.getJavaInfo().getVersion());
      throw new RuntimeException("JDK8以上才能增量生成JRE");
    } else if (!platform.isCurrentPlatform() && jdkPath.equals(currentJdk)) {
      Logger.warn("不能创建与当前操作系统不同的Jre. 当前操作系统：{}", platform);
      bundleJre = false;
    } else {
      Logger.info("开始创建定制化JRE ...");
      // tests if specified JDK is for the same platform than target platform
      if (!JdkUtils.isValidJdk(platform, jdkPath)) {
        Logger.warn("非法JDK. 当前操作系统：{} , JDK: {}", platform, jdkPath);
        throw new RuntimeException("非法JDK");
      }
      String modules = getRequiredModules(libsFolder, customizedJre, jarFile, requiredModules, additionalModules, additionalModulePaths);
      Logger.info("使用模块信息: {}", modules);
      File modulesDir = new File(jdkPath, "jmods");
      if (!modulesDir.exists()) {
        Logger.error("jmods 文件不存在: {}", modulesDir);
        throw new RuntimeException("jmods 文件不存在: " + modulesDir);
      }
      packager.getExtModulePath().add(modulesDir.getAbsolutePath());
      String modulePath = String.format("\"%s\"", ArrayUtil.join(packager.getExtModulePath().toArray(new String[0]), platform == Platform.windows ? ";" : ":"));
      Logger.info("使用模块目录：{}", modulePath);
      File jlink = new File(currentJdk, "/bin/jlink");
      if (destinationFolder.exists()) {
        FileUtils.removeFolder(destinationFolder);
      }
      // 使用模块生成定制的jre
      CommandUtils.execute(jlink.getAbsolutePath(), "--module-path", modulePath, "--add-modules", modules,
        "--output", destinationFolder, "--no-header-files", "--no-man-pages", "--strip-debug",
        "--compress=2");
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
   * Uses jdeps command tool to determine which modules all used jar files depend on
   *
   * @param libsFolder            folder containing all needed libraries
   * @param customizedJre         if true generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.
   * @param jarFile               Runnable jar file reference
   * @param defaultModules        Additional files and folders to include in the bundled app.
   * @param additionalModules     Defines modules to customize the bundled JRE. Don't use jdeps to get module dependencies.
   * @param additionalModulePaths Defines additional module paths to customize the bundled JRE.
   * @return string containing a comma separated list with all needed modules
   * @throws Exception Process failed
   */
  protected String getRequiredModules(File libsFolder, boolean customizedJre, File jarFile,
    List<String> defaultModules, List<String> additionalModules, List<File> additionalModulePaths) throws Exception {

    Logger.infoIndent("Getting required modules ... ");

    File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

    File jarLibs = null;
    if (libsFolder != null && libsFolder.exists()) {
      jarLibs = new File(libsFolder, "*.jar");
    } else {
      Logger.warn("No dependencies found!");
    }
    List<String> modulesList;
    if (customizedJre && defaultModules != null && !defaultModules.isEmpty()) {
      modulesList =
        defaultModules
          .stream()
          .map(String::trim)
          .collect(Collectors.toList());
    } else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 13) {
      String modules =
        CommandUtils.execute(
          jdeps.getAbsolutePath(),
          "-q",
          "--multi-release", JavaUtils.getJavaMajorVersion(),
          "--ignore-missing-deps",
          "--print-module-deps",
          additionalModulePathsToParams(additionalModulePaths),
          jarLibs,
          jarFile
        );
      modulesList =
        Arrays.stream(modules.split(","))
          .map(String::trim)
          .filter(module -> !module.isEmpty())
          .collect(Collectors.toList());
    } else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 9) {
      String modules =
        CommandUtils.execute(
          jdeps.getAbsolutePath(),
          "-q",
          "--multi-release", JavaUtils.getJavaMajorVersion(),
          "--list-deps",
          additionalModulePathsToParams(additionalModulePaths),
          jarLibs,
          jarFile
        );
      modulesList =
        Arrays.stream(modules.split("\n"))
          .map(String::trim)
          .map(module -> (module.contains("/") ? module.split("/")[0] : module))
          .filter(module -> !module.isEmpty())
          .filter(module -> !module.startsWith("JDK removed internal"))
          .distinct()
          .collect(Collectors.toList());
    } else {
      modulesList = Collections.singletonList("ALL-MODULE-PATH");
    }
    modulesList.addAll(additionalModules);
    if (modulesList.isEmpty()) {
      Logger.warn(
        "It was not possible to determine the necessary modules. All modules will be included");
      modulesList.add("ALL-MODULE-PATH");
    }
    Logger.infoUnIndent("Required modules found: " + modulesList);
    return StringUtils.join(modulesList, ",");
  }

  private String[] additionalModulePathsToParams(List<File> additionalModulePaths) {
    List<String> additionalPaths = new ArrayList<>();
    if (additionalModulePaths != null) {
      additionalModulePaths.stream()
        .filter(path -> {
          if (path.exists()) {
            return true;
          }
          Logger.warn("Additional module path not found: " + path);
          return false;
        })
        .forEach(path -> {
          additionalPaths.add("--module-path");
          additionalPaths.add(path.toString());
        });
    }
    return additionalPaths.toArray(new String[0]);
  }

}
