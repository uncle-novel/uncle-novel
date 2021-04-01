package com.unclezs.novel.app.jfx.packager.packager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.packager.action.mac.GenerateDmg;
import com.unclezs.novel.app.jfx.packager.action.mac.GeneratePkg;
import com.unclezs.novel.app.jfx.packager.util.CommandUtils;
import com.unclezs.novel.app.jfx.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import com.unclezs.novel.app.jfx.packager.util.VelocityUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Packager for Mac OS X
 */
public class MacPackager extends Packager {

  public static final String START_SCRIPT_NAME = "universalJavaApplicationStub";
  private File appFile;
  private File contentsFolder;
  private File resourcesFolder;
  private File javaFolder;
  private File macOsFolder;

  public MacPackager() {
    super();
    installerGenerators.add(new GenerateDmg());
    installerGenerators.add(new GeneratePkg());
  }

  public File getAppFile() {
    return appFile;
  }

  @Override
  public void doInit() {

    this.macConfig.setDefaults(this);

    // FIX useResourcesAsWorkingDir=false doesn't work fine on Mac OS (option disabled)
    if (!this.isUseResourcesAsWorkingDir()) {
      this.useResourcesAsWorkingDir = true;
      Logger.warn(
        "'useResourcesAsWorkingDir' property disabled on Mac OS (useResourcesAsWorkingDir is always true)");
    }

  }

  @Override
  protected void doCreateAppStructure() throws Exception {

    // initializes the references to the app structure folders
    this.appFile = new File(appFolder, name + ".app");
    this.contentsFolder = new File(appFile, "Contents");
    this.resourcesFolder = new File(contentsFolder, "Resources");
    this.javaFolder = new File(resourcesFolder, macConfig.getCustomAppFolder());
    this.macOsFolder = new File(contentsFolder, "MacOS");

    // makes dirs

    FileUtils.mkdir(this.appFile);
    Logger.info("App file folder created: " + appFile.getAbsolutePath());

    FileUtils.mkdir(this.contentsFolder);
    Logger.info("Contents folder created: " + contentsFolder.getAbsolutePath());

    FileUtils.mkdir(this.resourcesFolder);
    Logger.info("Resources folder created: " + resourcesFolder.getAbsolutePath());

    FileUtils.mkdir(this.javaFolder);
    Logger.info("Java folder created: " + javaFolder.getAbsolutePath());

    FileUtils.mkdir(this.macOsFolder);
    Logger.info("MacOS folder created: " + macOsFolder.getAbsolutePath());

    // sets common folders
    this.executableDestinationFolder = macOsFolder;
    this.jarFileDestinationFolder = javaFolder;
    this.jreDestinationFolder = new File(contentsFolder,
      "PlugIns/" + jreDirectoryName + "/Contents/Home");
    this.resourcesDestinationFolder = resourcesFolder;

  }

  /**
   * Creates a native MacOS app bundle
   */
  @Override
  public File doCreateApp() throws Exception {
    // 将文件复制到Java文件夹
    FileUtils.copyFileToFolder(jarFile, javaFolder);
    createStartScript();
    // 拷贝Vm参数文件
    if (launcherVmOptionsFile == null) {
      FileUtils.copyResourceToFile("/vm.options", new File(resourcesFolder, launcherVmOptionsFileName), true);
    } else {
      FileUtil.copy(launcherVmOptionsFile, new File(resourcesFolder, launcherVmOptionsFileName), true);
    }
    createClasspath();
    // 创建 info.plist
    File infoPlistFile = new File(contentsFolder, "Info.plist");
    VelocityUtils.render("mac/Info.plist.vm", infoPlistFile, this);
    Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());
    // codesign app folder
    if (Platform.mac.isCurrentPlatform()) {
      codesign(this.macConfig.getDeveloperId(), this.macConfig.getEntitlements(), this.appFile);
    } else {
      Logger.warn("Generated app could not be signed due to current platform is " + Platform
        .getCurrentPlatform());
    }
    return appFile;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void createStartScript() throws Exception {
    // 管理员权限启动，使用Root身份启动
    if (this.administratorRequired) {
      this.executable = new File(macOsFolder, "startup");
      VelocityUtils.render("mac/startup.vm", executable, this);
      executable.setExecutable(true, false);
      Logger.info("Startup script file created in " + executable.getAbsolutePath());
    } else {
      // sets startup file
      this.executable = new File(macOsFolder, macConfig.getStartScriptName());
      Logger.info("Using " + executable.getAbsolutePath() + " as startup script");
    }
    // 将universalJavaApplicationStub启动文件复制到启动java应用程序
    File appStubFile = new File(macOsFolder, macConfig.getStartScriptName());
    FileUtils.copyResourceToFile("/mac/".concat(START_SCRIPT_NAME), appStubFile, true);
    FileUtils.processFileContent(appStubFile, content -> {
      content = content.replace("/Contents/Resources/Java", "/Contents/Resources/".concat(macConfig.getCustomAppFolder()));
      content = content.replaceAll("\\$\\{info.name\\}", this.name);
      return content;
    });
    appStubFile.setExecutable(true, false);
  }

  private void createClasspath() {
    StringBuilder classPath = new StringBuilder(classpath);
    // 生成ClassPath
    if (StrUtil.isNotBlank(macConfig.getCustomAppFolder())) {
      classPath.append(":").append(macConfig.getCustomAppFolder()).append("/").append(jarFile.getName());
    } else {
      classPath.append(":").append(jarFile.getName());
    }
    if (StrUtil.isBlank(classPath)) {
      classPath.deleteCharAt(0);
    }
    classpathList = CollectionUtil.toList(classPath.toString().split("[:;]"));
    if (!isUseResourcesAsWorkingDir()) {
      classpathList = classpathList.stream()
        .map(cp -> new File(cp).isAbsolute()
          ? cp : "$ResourcesFolder/" + cp)
        .collect(Collectors.toList());
    }
    // 启动器classpath
    if (userLauncher()) {
      classpathList.addAll(launcher.getClasspath());
    }
    classpath = StrUtil.join(":", classpathList.toArray(new Object[0]));
  }

  private void codesign(String developerId, File entitlements, File appFile)
    throws IOException, CommandLineException {
    List<Object> codesignArgs = new ArrayList<>();
    codesignArgs.add("--force");
    codesignArgs.add("--deep");
    if (entitlements == null) {
      Logger.warn("Entitlements file not specified");
    } else if (!entitlements.exists()) {
      Logger.warn("Entitlements file doesn't exist: " + entitlements);
    } else {
      codesignArgs.add("--entitlements");
      codesignArgs.add(entitlements);
    }
    codesignArgs.add("--sign");
    codesignArgs.add(developerId);
    codesignArgs.add(appFile);
    CommandUtils.execute("codesign", codesignArgs.toArray(new Object[0]));
  }

}
