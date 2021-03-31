package com.unclezs.novel.app.jfx.packager.task;

import com.unclezs.novel.app.jfx.packager.packager.PackagerSetting;
import com.unclezs.novel.app.jfx.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.packager.model.Manifest;
import com.unclezs.novel.app.jfx.packager.model.WindowsConfig;
import groovy.lang.Closure;
import java.io.File;
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
    this.version = project.getVersion().toString();
    this.assetsDir = new File(project.getProjectDir(), "assets");
    this.description = project.getDescription();
    this.name = project.getName();
    this.outputDirectory = project.getBuildDir();
    this.winConfig = new WindowsConfig();
    this.linuxConfig = new LinuxConfig();
    this.macConfig = new MacConfig();
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
