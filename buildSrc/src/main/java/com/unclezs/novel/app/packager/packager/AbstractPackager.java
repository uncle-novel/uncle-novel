package com.unclezs.novel.app.packager.packager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.packager.model.PackagerExtension;
import com.unclezs.novel.app.packager.model.Platform;
import com.unclezs.novel.app.packager.subtask.*;
import com.unclezs.novel.app.packager.task.Upgrade;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;
import java.util.List;

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
   * 应用程序输出文件夹为 outputDir/name
   */
  protected File appFolder;
  /**
   * 静态资源目录：outputDir/"assets"
   */
  protected File assetsFolder;
  /**
   * 临时文件目录
   */
  protected File tmpDir;
  /**
   * 内置属性，创建应用架构时生成
   */
  protected File executableDestinationFolder;
  protected File jarFileDestinationFolder;
  protected File jreDestinationFolder;
  protected File resourcesDestinationFolder;
  /**
   * 创建app时候生成
   * <p>
   * 启动的jar
   */
  protected File jarFile;
  protected File libsFolder;
  protected File executable;

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
    new CopyDependencies().apply();
    libsFolder = new File(jarFileDestinationFolder, libsFolderPath);
    if (userLauncher()) {
      jarFile = new CreateLauncher().apply();
    } else {
      jarFile = new CreateRunnableJar().apply();
    }
    // 创建Jre
    new CreateJre().apply();
    doCreateApp();
    // 删除无用文件
    removeUselessFiles();
    Logger.infoUnIndent("App created in " + appFolder.getAbsolutePath() + "!");
  }

  /**
   * 生成平台的安装包
   */
  public void generateInstallers() {
    if (!generateInstaller || !FileUtil.exist(executable)) {
      return;
    }
    if (!platform.isCurrentPlatform()) {
      Logger.warn("{}操作系统不能生成{}相关安装程序，请在相应操作系统下执行", Platform.getCurrentPlatform(), platform);
      return;
    }
    Logger.infoIndent("开始生成安装包 ...");
    init();
    // 生成安装包，忽略错误
    for (BaseSubTask installerTask : getInstallerTasks()) {
      try {
        installerTask.apply();
      } catch (Exception e) {
        // ignored
      }
    }
    Logger.infoUnIndent("安装包生成完成");
  }

  /**
   * 创建zip、tar压缩包
   */
  public void createCompressedPackage() {
    Logger.infoIndent("开始创建压缩包 ...");
    if (createZip) {
      new CreateCompressedPackage(true).apply();
    }
    if (createTar) {
      new CreateCompressedPackage(false).apply();
    }
    Logger.infoUnIndent("压缩包创建完成！");
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
    tmpDir = new File(outputDir, "tmp");
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
    FileUtils.del(appFolder);
    appFolder = FileUtil.mkdir(FileUtil.file(outputDir, name));
    Logger.info("应用程序文件输出位置: {}", appFolder.getAbsolutePath());
    // 创建临时资源文件夹
    assetsFolder = FileUtil.mkdir(FileUtil.file(outputDir, "assets"));
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
    resources.put(platform.getPlatformConfig().getIconFile().getName(), platform.getPlatformConfig().getIconFile());
    Logger.info("使用图标: {}", iconFile.getAbsolutePath());
    // 启动VM参数文件
    if (FileUtil.exist(vmOptionsFile)) {
      resources.put(vmOptionsFilePath, vmOptionsFile);
    }
    // 资源处理
    Logger.info("找到的资源：{}", resources);
    // 拷贝资源
    resources.forEach((to, from) -> {
      if (from.exists()) {
        FileUtil.copy(from, new File(appFolder, to), true);
      } else {
        Logger.warn("资源不存在: {}", from);
      }
    });
    Logger.infoUnIndent("资源检索完毕!");
  }

  /**
   * 删除无用的文件
   */
  private void removeUselessFiles() {
    if (userLauncher()) {
      if (Boolean.TRUE.equals(launcher.getWithLibraries())) {
        Upgrade upgrade = new Upgrade(getProject(), appFolder);
        upgrade.setRemoveOld(false);
        upgrade.createLocal();
      }
    }
  }

  /**
   * 获取安装包生成器
   *
   * @return 安装包生成任务
   */
  public abstract List<BaseSubTask> getInstallerTasks();

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
