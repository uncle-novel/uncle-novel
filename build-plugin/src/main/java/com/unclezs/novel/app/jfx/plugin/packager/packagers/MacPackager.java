package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.plugin.packager.util.CommandUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Packager for Mac OS X
 */
public class MacPackager extends Packager {

  private File appFile;
  private File contentsFolder;
  private File resourcesFolder;
  private File javaFolder;
  private File macOSFolder;

  public MacPackager() {
    super();
    installerGenerators.addAll(Context.getContext().getMacInstallerGenerators());
  }

  public File getAppFile() {
    return appFile;
  }

  @Override
  public void doInit() throws Exception {

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
    this.javaFolder = new File(resourcesFolder, this.macConfig.isRelocateJar() ? "Java" : "");
    this.macOSFolder = new File(contentsFolder, "MacOS");

    // makes dirs

    FileUtils.mkdir(this.appFile);
    Logger.info("App file folder created: " + appFile.getAbsolutePath());

    FileUtils.mkdir(this.contentsFolder);
    Logger.info("Contents folder created: " + contentsFolder.getAbsolutePath());

    FileUtils.mkdir(this.resourcesFolder);
    Logger.info("Resources folder created: " + resourcesFolder.getAbsolutePath());

    FileUtils.mkdir(this.javaFolder);
    Logger.info("Java folder created: " + javaFolder.getAbsolutePath());

    FileUtils.mkdir(this.macOSFolder);
    Logger.info("MacOS folder created: " + macOSFolder.getAbsolutePath());

    // sets common folders
    this.executableDestinationFolder = macOSFolder;
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

    // copies jarfile to Java folder
    FileUtils.copyFileToFolder(jarFile, javaFolder);

    if (this.administratorRequired) {
      // sets startup file
      this.executable = new File(macOSFolder, "startup");
      // creates startup file to boot java app
      VelocityUtils.render("mac/startup.vm", executable, this);
      //noinspection ResultOfMethodCallIgnored
      executable.setExecutable(true, false);
      Logger.info("Startup script file created in " + executable.getAbsolutePath());

    } else {
      // sets startup file
      this.executable = new File(macOSFolder, "universalJavaApplicationStub");
      Logger.info("Using " + executable.getAbsolutePath() + " as startup script");
    }
    // copies universalJavaApplicationStub startup file to boot java app
    File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
    FileUtils.copyResourceToFile("/mac/universalJavaApplicationStub", appStubFile, true);
    FileUtils.processFileContent(appStubFile, content -> {
      if (!macConfig.isRelocateJar()) {
        content = content.replaceAll("/Contents/Resources/Java", "/Contents/Resources");
      }
      content = content.replaceAll("\\$\\{info.name\\}", this.name);
      return content;
    });
    //noinspection ResultOfMethodCallIgnored
    appStubFile.setExecutable(true, false);
    // process classpath
    classpath = (this.macConfig.isRelocateJar() ? "Java/" : "") + this.jarFile.getName() + (
        classpath != null ? ":" + classpath : "");
    classpathList = Arrays.asList(classpath.split("[:;]"));
    if (!isUseResourcesAsWorkingDir()) {
      classpathList = classpathList.stream()
          .map(cp -> new File(cp).isAbsolute() ? cp : "$ResourcesFolder/" + cp)
          .collect(Collectors.toList());
    }
    classpath = StrUtil.join(":", classpathList.toArray(new Object[0]));
    // creates and write the Info.plist file
    File infoPlistFile = new File(contentsFolder, "Info.plist");
    VelocityUtils.render("mac/Info.plist.vm", infoPlistFile, this);
    Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());
    // codesigns app folder
    if (Platform.mac.isCurrentPlatform()) {
      codesign(this.macConfig.getDeveloperId(), this.macConfig.getEntitlements(), this.appFile);
    } else {
      Logger.warn("Generated app could not be signed due to current platform is " + Platform
          .getCurrentPlatform());
    }

    return appFile;
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
