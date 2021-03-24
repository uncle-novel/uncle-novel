package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.PackagerSettings;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import groovy.lang.Closure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.gradle.api.Project;

/**
 * JavaPackager plugin extension for Gradle
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:15
 */
public class PackagePluginExtension extends PackagerSettings {

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
    this.createTarball = false;
    this.createZipball = false;
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

}
