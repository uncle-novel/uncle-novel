package com.unclezs.novel.app.packager.packager;

import com.unclezs.novel.app.packager.PackagePlugin;
import com.unclezs.novel.app.packager.model.LauncherConfig;
import com.unclezs.novel.app.packager.model.LinuxConfig;
import com.unclezs.novel.app.packager.model.MacConfig;
import com.unclezs.novel.app.packager.model.Platform;
import com.unclezs.novel.app.packager.model.WindowsConfig;
import groovy.lang.Closure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gradle.api.Project;

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

  /**
   * App信息
   */
  protected String name;
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
  /**
   * 生成安装包
   */
  protected Boolean generateInstaller = false;
  /**
   * 是否需要管理员权限启动
   */
  protected Boolean administratorRequired = false;
  /**
   * 自定义的Jre和Jdk路径
   */
  protected File jrePath;
  protected File jdkPath;
  /**
   * 额外的资源，目标位置（相对app的位置）/源文件
   */
  protected Map<String, File> resources = new HashMap<>();
  /**
   * 打包Jre时的模块配置，生成Jre的时候会根据这个创建
   */
  protected Set<String> modules = new HashSet<>();
  protected Set<String> additionalModules = new HashSet<>();
  protected Set<File> additionalModulePaths = new HashSet<>();
  protected Platform platform = Platform.auto;
  protected List<String> vmArgs = new ArrayList<>();
  protected File runnableJar;
  protected Boolean copyDependencies = true;
  protected String jreDirName = "jre";
  protected boolean useResourcesAsWorkingDir = true;
  protected File assetsDir;
  /**
   * JVM 启动参数配置文件名称
   */
  protected String launcherVmOptionsFilePath = "conf";
  protected String launcherVmOptionsFileName = "launcher.vmoptions";
  /**
   * JVM 启动参数配置文件
   */
  protected File launcherVmOptionsFile;
  /**
   * 依赖的文件夹名
   */
  protected String libsFolderName = "lib";
  /**
   * jar运行时候的额外的classpath ;或: 分割 （相对与运行目录）
   */
  protected Set<String> classpath = new HashSet<>();
  protected String jreMinVersion;
  /**
   * 打包完成后是否自动创建压缩包
   */
  protected Boolean createTar = false;
  protected Boolean createZip = false;
  /**
   * 平台配置
   */
  protected WindowsConfig winConfig = new WindowsConfig();
  protected LinuxConfig linuxConfig = new LinuxConfig();
  protected MacConfig macConfig = new MacConfig();
  /**
   * 启动器配置（自动更新）
   */
  protected LauncherConfig launcher;
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
    return launcher != null && Boolean.TRUE.equals(launcher.getEnabled());
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

  public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
    winConfig = new WindowsConfig();
    project.configure(winConfig, closure);
    return winConfig;
  }

  public LauncherConfig launcher(Closure<LauncherConfig> closure) {
    launcher = new LauncherConfig(project.getProject());
    project.configure(launcher, closure);
    return launcher;
  }
}
