package com.unclezs.novel.app.jfx.plugin.packager.packager;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.action.ArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.action.BundleJre;
import com.unclezs.novel.app.jfx.plugin.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.IconUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 核心打包基类
 *
 * @author blog.unclezs.com
 * @date 2021/3/28 23:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Packager extends PackagerSetting {

  public static final String BACKSLASH = "\\";
  public static final String SLASH = "/";
  private static final String DEFAULT_ORGANIZATION_NAME = "ACME";
  private final BundleJre generateJre = new BundleJre();
  /**
   * artifact generators
   */
  protected List<ArtifactGenerator> installerGenerators = new ArrayList<>();
  /**
   * internal generic properties (setted in "createAppStructure/createApp")
   */
  protected File appFolder;
  protected File assetsFolder;
  protected File executable;
  protected File jarFile;
  protected File libsFolder;

  /**
   * internal specific properties (setted in "doCreateAppStructure")
   */
  protected File executableDestinationFolder;
  protected File jarFileDestinationFolder;
  protected File jreDestinationFolder;
  protected File resourcesDestinationFolder;
  /**
   * 已处理的类路径列表
   */
  protected List<String> classpathList = new ArrayList<>();

  public Packager() {
    super();
    Logger.info("Using packager " + this.getClass().getName());
  }

  private void init() {
    Logger.infoIndent("Initializing packager ...");
    if (StrUtil.isBlank(mainClass)) {
      throw new RuntimeException("'mainClass' cannot be null");
    }
    // 设置自定义模板目录
    VelocityUtils.setAssetsDir(assetsDir);
    // 如果未指定，则使用名称作为displayName
    displayName = StrUtil.blankToDefault(displayName, name);
    // 如果未指定，则使用displayName作为描述
    description = StrUtil.blankToDefault(description, displayName);
    // 如果未指定，请使用ACME作为organizationName
    organizationName = StrUtil.blankToDefault(organizationName, DEFAULT_ORGANIZATION_NAME);
    // 如果未指定，则使用空字符串作为organizationUrl
    organizationUrl = StrUtil.blankToDefault(organizationUrl, StrUtil.EMPTY);
    // determines target platform if not specified
    if (platform == null || platform == Platform.auto) {
      platform = Platform.getCurrentPlatform();
    }
    // 如果未指定，则默认设置jdkPath
    if (jdkPath == null) {
      jdkPath = new File(System.getProperty("java.home"));
    }
    if (!jdkPath.exists()) {
      throw new RuntimeException("JDK path doesn't exist: " + jdkPath);
    }
    // 检查名称是否有效
    if (name.contains(SLASH)) {
      throw new IllegalArgumentException(name.concat("不能包含斜杠"));
    }
    if (name.contains(BACKSLASH)) {
      throw new IllegalArgumentException(name.concat("不能包含反斜杠"));
    }
    // 初始化安装语言
    if (platform == Platform.windows && CollectionUtil.isEmpty(winConfig.getSetupLanguages())) {
      winConfig.setSetupLanguages(new LinkedHashMap<>(3));
      winConfig.getSetupLanguages().put("english", "compiler:Default.isl");
      winConfig.getSetupLanguages().put("spanish", "compiler:Languages\\Spanish.isl");
    }
    doInit();
    // 删除不必要的平台特定配置
    switch (platform) {
      case linux:
        macConfig = null;
        winConfig = null;
        break;
      case mac:
        winConfig = null;
        linuxConfig = null;
        break;
      case windows:
        linuxConfig = null;
        macConfig = null;
        break;
      default:
    }
    // 打印打包程序配置信息
    Logger.info(this.toString());
    Logger.infoUnindent("Packager initialized!");
  }

  public void resolveResources() throws Exception {

    Logger.infoIndent("Resolving resources ...");

    // locates icon file
    iconFile = resolveIcon(iconFile, name, assetsFolder);

    // adds to additional resources
    if (additionalResources != null) {
      if (licenseFile != null) {
        additionalResources.add(licenseFile);
      }
      additionalResources.add(iconFile);
      Logger.info("Effective additional resources " + additionalResources);
    }

    Logger.infoUnindent("Resources resolved!");

  }

  /**
   * Copy a list of resources to a folder
   *
   * @param resources   List of files and folders to be copied
   * @param destination Destination folder. All specified resources will be copied here
   */
  protected void copyAdditionalResources(List<File> resources, File destination) {

    Logger.infoIndent("Copying additional resources");

    resources.forEach(r -> {
      if (!r.exists()) {
        Logger.warn("Additional resource " + r + " doesn't exist");
        return;
      }
      try {
        if (r.isDirectory()) {
          FileUtils.copyFolderToFolder(r, destination);
        } else if (r.isFile()) {
          FileUtils.copyFileToFolder(r, destination);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Logger.infoUnindent("All additional resources copied!");

  }

  /**
   * Locates assets or default icon file if the specified one doesn't exist or isn't specified
   *
   * @param iconFile     Specified icon file
   * @param name         Name
   * @param assetsFolder Assets folder
   * @return Resolved icon file
   * @throws Exception Process failed
   */
  protected File resolveIcon(File iconFile, String name, File assetsFolder) throws Exception {

    // search for specific icons
    switch (platform) {
      case linux:
        iconFile = FileUtils.exists(linuxConfig.getPngFile()) ? linuxConfig.getPngFile() : null;
        break;
      case mac:
        iconFile = FileUtils.exists(macConfig.getIcnsFile()) ? macConfig.getIcnsFile() : null;
        break;
      case windows:
        iconFile = FileUtils.exists(winConfig.getIcoFile()) ? winConfig.getIcoFile() : null;
        break;
      default:
    }

    String iconExtension = IconUtils.getIconFileExtensionByPlatform(platform);
    if (iconFile != null) {
      iconFile = FileUtil.copy(iconFile, new File(assetsFolder, name + iconExtension), true);
    } else {
      // if not specific icon specified for target platform, search for an icon in "${assetsDir}" folder
      iconFile = new File(assetsDir, name + iconExtension);
    }

    // if there's no icon yet, uses default one
    if (!iconFile.exists()) {
      iconFile = new File(assetsFolder, iconFile.getName());
      FileUtils.copyResourceToFile("/" + platform + "/default-icon" + iconExtension, iconFile);
    }

    Logger.info("Icon file resolved: " + iconFile.getAbsolutePath());

    return iconFile;
  }

  /**
   * Bundling app folder in tarball and/or zipball
   *
   * @return Generated bundles
   * @throws Exception Process failed
   */
  public List<File> createBundles() throws Exception {
    List<File> bundles = new ArrayList<>();
    Logger.infoIndent("Creating bundles ...");
    if (createZip) {
      File zipball = Context.createZip(this);
      Logger.info("Zipball created: " + zipball);
      bundles.add(zipball);
    }
    if (createTar) {
      File tarball = Context.createTar(this);
      Logger.info("Tarball created: " + tarball);
      bundles.add(tarball);
    }
    Logger.infoUnindent("Bundles created!");
    return bundles;
  }

  private void createAppStructure() throws Exception {
    Logger.infoIndent("Creating app structure ...");

    // creates output directory if it doesn't exist
    if (!outputDirectory.exists()) {
      //noinspection ResultOfMethodCallIgnored
      outputDirectory.mkdirs();
    }

    // creates app destination folder
    appFolder = new File(outputDirectory, name);
    if (appFolder.exists()) {
      FileUtil.del(appFolder);
      Logger.info("Old app folder removed " + appFolder.getAbsolutePath());
    }
    appFolder = FileUtils.mkdir(outputDirectory, name);
    Logger.info("App folder created: " + appFolder.getAbsolutePath());

    // creates folder for intermediate assets
    assetsFolder = FileUtils.mkdir(outputDirectory, "assets");
    Logger.info("Assets folder created: " + assetsFolder.getAbsolutePath());

    // create the rest of the structure
    doCreateAppStructure();

    Logger.infoUnindent("App structure created!");

  }

  public File createApp() throws Exception {
    Logger.infoIndent("Creating app ...");
    init();
    // creates app folders structure
    createAppStructure();
    // resolve resources
    resolveResources();
    // copies additional resources
    copyAdditionalResources(additionalResources, resourcesDestinationFolder);
    // copies all dependencies to Java folder
    Logger.infoIndent("Copying all dependencies ...");
    libsFolder = copyDependencies ? Context.copyDependencies(this) : null;
    Logger.infoUnindent("Dependencies copied to " + libsFolder + "!");
    // creates a runnable jar file
    if (runnableJar != null && runnableJar.exists()) {
      Logger.info("Using runnable JAR: " + runnableJar);
      jarFile = runnableJar;
    } else {
      Logger.infoIndent("Creating runnable JAR...");
      jarFile = Context.createRunnableJar(this);
      Logger.infoUnindent("Runnable jar created in " + jarFile + "!");
    }
    // embeds a JRE if is required
    generateJre.apply(this);
    File appFile = doCreateApp();
    Logger.infoUnindent("App created in " + appFolder.getAbsolutePath() + "!");
    return appFile;
  }

  public List<File> generateInstallers() throws Exception {
    List<File> installers = new ArrayList<>();

    if (!generateInstaller) {
      Logger.warn("Installer generation is disabled by 'generateInstaller' property!");
      return installers;
    }
    if (!platform.isCurrentPlatform()) {
      Logger.warn("Installers cannot be generated due to the target platform (" + platform
        + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
      return installers;
    }

    Logger.infoIndent("Generating installers ...");

    init();

    // creates folder for intermmediate assets if it doesn't exist
    assetsFolder = FileUtils.mkdir(outputDirectory, "assets");

    // invokes installer producers

    for (ArtifactGenerator generator : installerGenerators) {
      try {
        Logger.infoIndent("Generating " + generator.getArtifactName() + "...");
        File artifact = generator.apply(this);
        if (artifact != null) {
          addIgnoreNull(installers, artifact);
          Logger.infoUnindent(generator.getArtifactName() + " generated in " + artifact + "!");
        } else {
          Logger.warnUnindent(generator.getArtifactName() + " NOT generated!!!");
        }

      } catch (Exception e) {
        Logger.errorUnindent(
          generator.getArtifactName() + " generation failed due to: " + e.getMessage(), e);
      }
    }

    Logger.infoUnindent("Installers generated! " + installers);

    return installers;
  }

  protected abstract void doCreateAppStructure() throws Exception;

  public abstract File doCreateApp() throws Exception;

  /**
   * 初始化平台相关配置
   */
  public abstract void doInit();
}
