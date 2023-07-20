package com.unclezs.novel.app.packager.model;

import com.unclezs.novel.app.packager.PackagePlugin;
import groovy.lang.Closure;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 打包的一些通用设置
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Data
@NoArgsConstructor
public class PackagerExtension {

  protected Platform platform = Platform.auto;
  /**
   * App信息
   */
  protected String name;
  protected String packageName;
  protected String displayName;
  protected String version;
  protected String description;
  protected String url;
  protected String organizationName;
  protected String organizationUrl;
  protected String organizationEmail;
  /**
   * 打包配置
   */
  protected File outputDir;
  protected String mainModule;
  protected String mainClass;
  protected Boolean bundleJre = true;
  protected Boolean bundleFxJre = true;
  protected Boolean customizedJre = true;
  protected String jreDirName = "jre";
  protected String jreMinVersion;
  /**
   * 自定义的Jre和Jdk路径
   */
  protected File jrePath;
  protected File jdkPath;
  /**
   * 自定义jfx的jmods路径，用于生成jre，不指定则使用项目依赖，注意版本对齐
   */
  protected File jfxPath;
  /**
   * 写死的启动参数
   */
  protected List<String> vmArgs = new ArrayList<>();
  /**
   * JVM 启动参数配置文件
   */
  protected File vmOptionsFile;
  /**
   * JVM 配置文件保存的目录（相对与app目录）
   */
  protected String vmOptionsFilePath = "conf/launcher.vmoptions";
  /**
   * 生成安装包
   */
  protected Boolean generateInstaller = false;
  /**
   * 是否需要管理员权限启动
   */
  protected Boolean administratorRequired = false;
  /**
   * 额外的资源，目标位置（相对app的位置）/源文件
   */
  protected Map<String, File> resources = new HashMap<>();
  /**
   * 打包Jre时的模块配置，生成Jre的时候会根据这个创建
   * <p>
   * 指定模块创建jre
   */
  protected Set<String> modules = new HashSet<>();
  /**
   * 额外的模块与路径
   */
  protected Set<String> additionalModules = new HashSet<>();
  protected Set<File> additionalModulePaths = new HashSet<>();
  protected boolean useResourcesAsWorkingDir = true;
  /**
   * 自定义模板位置
   */
  protected File assetsDir;
  /**
   * 依赖的文件夹名
   */
  protected String libsFolderPath = "lib";
  /**
   * jar运行时候的额外的classpath或module path ;或: 分割 （相对于运行目录）
   */
  protected Set<String> classpath = new HashSet<>();
  /**
   * 打包完成后是否自动创建压缩包
   */
  protected Boolean createTar = true;
  protected Boolean createZip = true;
  /**
   * 平台配置
   */
  protected WinConfig winConfig = new WinConfig();
  protected LinuxConfig linuxConfig = new LinuxConfig();
  protected MacConfig macConfig = new MacConfig();
  /**
   * 启动器配置（自动更新）
   */
  protected LauncherConfig launcher;
  protected Boolean enabledLauncher = true;
  /**
   * 是否为64位，如果是x86的则需要指定jre为x86版本
   */
  private Boolean x64 = true;
  /**
   * CPU 架构，如 arm64, amd64
   */
  private String arch = "";
  private Project project;

  public PackagerExtension(Project project) {
    this.project = project;
    this.version = project.getVersion().toString();
    this.assetsDir = new File(project.getProjectDir(), "assets");
    this.description = project.getDescription();
    this.name = project.getName();
    this.outputDir = new File(project.getBuildDir(), PackagePlugin.EXTENSION_NAME);
  }

  /**
   * 是否启用launcher
   *
   * @return true 是
   */
  public boolean userLauncher() {
    return launcher != null && Boolean.TRUE.equals(enabledLauncher);
  }

  public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
    linuxConfig = new LinuxConfig();
    project.configure(linuxConfig, closure);
    return linuxConfig;
  }

  public MacConfig macConfig(Closure<MacConfig> closure) {
    macConfig = new MacConfig();
    project.configure(macConfig, closure);
    return macConfig;
  }

  public WinConfig winConfig(Closure<WinConfig> closure) {
    winConfig = new WinConfig();
    project.configure(winConfig, closure);
    return winConfig;
  }

  public LauncherConfig launcher(Closure<LauncherConfig> closure) {
    launcher = new LauncherConfig(project.getProject());
    project.configure(launcher, closure);
    return launcher;
  }
}
