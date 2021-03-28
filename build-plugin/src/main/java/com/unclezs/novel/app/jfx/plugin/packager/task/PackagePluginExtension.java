package com.unclezs.novel.app.jfx.plugin.packager.task;

import com.unclezs.novel.app.jfx.plugin.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.packager.PackagerSetting;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import groovy.lang.Closure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.gradle.api.Project;

/**
 * 带默认值的配置
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:15
 */
public class PackagePluginExtension extends PackagerSetting {

  private final Project project;

  public PackagePluginExtension(Project project) {
    super();
    this.project = project;
    this.platform = Platform.auto;
    this.additionalModules = new ArrayList<>();
    this.additionalModulePaths = new ArrayList<>();
    this.additionalResources = new ArrayList<>();
    this.administratorRequired = false;
    this.assetsDir = new File(project.getProjectDir(), "assets");
    this.bundleJre = true;
    this.copyDependencies = true;
    this.createTar = false;
    this.createZip = false;
    this.customizedJre = true;
    this.description = project.getDescription();
    this.extra = new HashMap<>();
    this.generateInstaller = false;
    this.jreDirectoryName = "jre";
    this.linuxConfig = new LinuxConfig();
    this.macConfig = new MacConfig();
    this.manifest = new Manifest();
    this.modules = new ArrayList<>();
    this.name = project.getName();
    this.organizationEmail = "";
    this.useResourcesAsWorkingDir = true;
    this.vmArgs = new ArrayList<>();
    this.winConfig = new WindowsConfig();
    this.outputDirectory = project.getBuildDir();
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
