package com.unclezs.novel.app.jfx.packager.packager;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.packager.PackagePlugin;
import com.unclezs.novel.app.jfx.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.packager.model.Manifest;
import com.unclezs.novel.app.jfx.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import groovy.lang.Closure;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

  private Project project;
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
  protected String organizationEmail = StrUtil.EMPTY;
  /**
   * 打包配置
   */
  protected File outputDir;
  protected String mainClass;
  protected Boolean bundleJre = true;
  protected Boolean bundleFxJre = true;
  protected Boolean customizedJre = true;
  protected boolean generateInstaller = false;
  protected Boolean administratorRequired = false;
  /**
   * 自定义的Jre和Jdk路径
   */
  protected File jrePath;
  protected File jdkPath;
  /**
   * 额外的资源，自动拷贝到app目录
   */
  protected List<File> additionalResources = new ArrayList<>();
  /**
   * 打包Jre时的模块配置，生成Jre的时候会根据这个创建
   */
  protected List<String> modules = new ArrayList<>();
  protected List<String> additionalModules = new ArrayList<>();
  protected List<File> additionalModulePaths = new ArrayList<>();

  protected Platform platform = Platform.auto;
  protected List<String> vmArgs = new ArrayList<>();
  protected File runnableJar;
  protected Manifest manifest = new Manifest();
  protected Boolean copyDependencies = true;
  protected String jreDirName = "jre";
  protected boolean useResourcesAsWorkingDir = true;
  protected File assetsDir;
  /**
   * JVM 启动参数配置文件名称
   */
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
   * jar运行时候的classpath
   */
  protected String classpath = StrUtil.EMPTY;
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
    return launcher != null;
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

  public Manifest manifest(Closure<Manifest> closure) {
    manifest = new Manifest();
    project.configure(manifest, closure);
    return manifest;
  }

  public LauncherConfig launcher(Closure<LauncherConfig> closure) {
    launcher = new LauncherConfig(project.getProject());
    project.configure(launcher, closure);
    return launcher;
  }
}
