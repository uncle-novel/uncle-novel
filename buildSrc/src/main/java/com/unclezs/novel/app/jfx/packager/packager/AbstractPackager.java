package com.unclezs.novel.app.jfx.packager.packager;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.action.ArtifactGenerator;
import com.unclezs.novel.app.jfx.packager.action.CopyFxDependencies;
import com.unclezs.novel.app.jfx.packager.action.CreateJre;
import com.unclezs.novel.app.jfx.packager.action.CreateLauncher;
import com.unclezs.novel.app.jfx.packager.action.CreateRunnableJar;
import com.unclezs.novel.app.jfx.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import com.unclezs.novel.app.jfx.packager.util.VelocityUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 核心打包基类
 *
 * @author blog.unclezs.com
 * @date 2021/3/28 23:58
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPackager extends PackagerExtension {

  public static final String BACKSLASH = "\\";
  public static final String SLASH = "/";
  private static final String DEFAULT_ORGANIZATION_NAME = "Unclezs";
  /**
   * artifact generators
   */
  protected List<ArtifactGenerator> installerGenerators = new ArrayList<>();
  /**
   * 应用程序输出文件夹为 outputDir/name
   */
  protected File appFolder;
  /**
   * 临时静态资源目录：outputDir/"assets"
   */
  protected File assetsFolder;
  /**
   * 内置属性，创建应用架构时生成
   */
  protected File executableDestinationFolder;
  protected File jarFileDestinationFolder;
  protected File jreDestinationFolder;
  protected File resourcesDestinationFolder;
  /**
   * 创建app时候生成
   */
  protected File jarFile;
  protected File libsFolder;
  protected File executable;
  /**
   * 创建Jre时候除了jmods的其他模块 如openjfx
   */
  protected List<String> extModulePath = new ArrayList<>();
  /**
   * 已处理的类路径列表
   */
  protected List<String> classpathList = new ArrayList<>();

  public AbstractPackager() {
    Logger.info("使用打包器：".concat(getClass().getName()));
  }

  /**
   * 创建应用程序
   *
   * @throws Exception 创建失败
   */
  public void createApp() throws Exception {
    Logger.infoIndent("开始创建可执行程序 ...");
    // 数据校验及初始化
    init();
    // 创建App目录架构
    createAppStructure();
    // 处理资源文件资源
    resolveResources();
    // 将openJfx依赖项复制到依赖文件夹
    new CopyFxDependencies().apply();
    libsFolder = new File(jarFileDestinationFolder, libsFolderName);
    if (userLauncher()) {
      jarFile = new CreateLauncher().apply();
    } else {
      jarFile = new CreateRunnableJar().apply();
    }
    // 嵌入Jre
    new CreateJre().apply();
    doCreateApp();
    Logger.infoUnIndent("App created in " + appFolder.getAbsolutePath() + "!");
  }

  public void generateInstallers() {
    List<File> installers = new ArrayList<>();
    if (!generateInstaller) {
      Logger.info("Installer generation is disabled by 'generateInstaller' property!");
      return;
    }
    if (!platform.isCurrentPlatform()) {
      Logger.warn("Installers cannot be generated due to the target platform (" + platform
        + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
      return;
    }
    Logger.infoIndent("Generating installers ...");
    init();
    // creates folder for intermmediate assets if it doesn't exist
    assetsFolder = FileUtils.mkdir(outputDir, "assets");
    // invokes installer producers
    for (ArtifactGenerator generator : installerGenerators) {
      try {
        Logger.infoIndent("Generating " + generator.getArtifactName() + "...");
        File artifact = generator.apply(this);
        if (artifact != null) {
          addIgnoreNull(installers, artifact);
          Logger.infoUnIndent(generator.getArtifactName() + " generated in " + artifact + "!");
        } else {
          Logger.warnUnIndent(generator.getArtifactName() + " NOT generated!!!");
        }
      } catch (Exception e) {
        Logger.errorUnIndent(generator.getArtifactName() + " generation failed due to: " + e.getMessage(), e);
      }
    }
    Logger.infoUnIndent("Installers generated! " + installers);
  }

  /**
   * 初始化数据及数据校验
   */
  private void init() {
    Logger.infoIndent("初始化打包器，加载默认值...");
    Assert.notBlank(mainClass, "'mainClass' 是必填项");
    // 初始化当前平台
    if (platform == null || platform == Platform.auto) {
      platform = Platform.getCurrentPlatform();
    }
    // 设置Velocity自定义模板目录
    VelocityUtils.setAssetsDir(assetsDir);
    // 默认值初始化
    displayName = StrUtil.blankToDefault(displayName, name);
    description = StrUtil.blankToDefault(description, displayName);
    organizationName = StrUtil.blankToDefault(organizationName, DEFAULT_ORGANIZATION_NAME);
    organizationUrl = StrUtil.blankToDefault(organizationUrl, StrUtil.EMPTY);
    jdkPath = ObjectUtil.defaultIfNull(jdkPath, new File(System.getProperty("java.home")));
    Assert.isTrue(jdkPath.exists(), "JDK 路径不存在 {}", jdkPath);
    Assert.isFalse(name.contains(SLASH), "name 不能包含斜杠 {}", name);
    Assert.isFalse(name.contains(SLASH), "name 不能包含反斜杠 {}", name);
    // 初始化平台相关配置
    doInit();
    // 打印打包程序配置信息
    Logger.info(this.toString());
    Logger.infoUnIndent("打包器初始化完成！");
  }

  /**
   * 创建目录架构
   */
  private void createAppStructure() throws Exception {
    Logger.infoIndent("开始创建目录架构 ...");
    if (!outputDir.exists()) {
      FileUtil.mkdir(outputDir);
    }
    // 创建应用程序输出文件夹
    appFolder = new File(outputDir, name);
    FileUtil.del(appFolder);
    appFolder = FileUtil.mkdir(FileUtil.file(outputDir, name));
    Logger.info("应用程序文件输出位置: {}", appFolder.getAbsolutePath());
    // 创建临时资源文件夹
    assetsFolder = FileUtils.mkdir(outputDir, "assets");
    Logger.info("临时资源文件夹位置: {}", assetsFolder.getAbsolutePath());
    // 创建平台相关的目录架构
    doCreateAppStructure();
    Logger.infoUnIndent("App的目录架构创建完成");
  }

  /**
   * 处理打包相关的资源文件
   */
  public void resolveResources() {
    Logger.infoIndent("开始检索资源 ...");
    // 查找图标
    File iconFile = platform.getPlatformConfig().getIconFile();
    // 如果没有设置，则使用默认图标
    if (!FileUtil.exist(iconFile)) {
      iconFile = new File(assetsFolder, iconFile.getName());
      FileUtil.copy(String.format("/%s/default-icon.%s", platform, platform.getIconType()), iconFile.getAbsolutePath(), true);
    }
    Logger.info("使用图标: {}", iconFile.getAbsolutePath());
    // 额外资源处理
    if (additionalResources != null) {
      additionalResources.add(platform.getPlatformConfig().getIconFile());
      Logger.info("找到的额外资源：{}", additionalResources);
      // 拷贝资源
      additionalResources.forEach(resource -> {
        if (resource.exists()) {
          FileUtil.copy(resource, resourcesDestinationFolder, true);
        } else {
          Logger.warn("额外资源不存在: {}", resource);
        }
      });
    }
    Logger.infoUnIndent("资源检索完毕!");
  }

  /**
   * Bundling app folder in tarball and/or zip
   *
   * @return Generated bundles
   * @throws Exception Process failed
   */
  public List<File> createBundles() throws Exception {
    List<File> bundles = new ArrayList<>();
    Logger.infoIndent("Creating bundles ...");
    if (createZip) {
      File zip = Context.createZip(this);
      Logger.info("Zip created: " + zip);
      bundles.add(zip);
    }
    if (createTar) {
      File tarball = Context.createTar(this);
      Logger.info("Tarball created: " + tarball);
      bundles.add(tarball);
    }
    Logger.infoUnIndent("Bundles created!");
    return bundles;
  }

  /**
   * 初始化平台相关配置
   */
  public abstract void doInit();

  /**
   * 创建平台相关目录架构
   *
   * @throws Exception 失败
   */
  protected abstract void doCreateAppStructure() throws Exception;

  /**
   * 创建平台相关可执行程序
   *
   * @throws Exception 失败
   */
  public abstract void doCreateApp() throws Exception;
}
