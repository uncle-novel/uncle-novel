package com.unclezs.novel.app.jfx.packager.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.unclezs.novel.app.jfx.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.packager.packager.Packager;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import groovy.lang.Closure;
import java.io.File;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;

/**
 * Packaging task for Gradle
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Setter
@Getter
public class PackageTask extends AbstractPackageTask {

  @Input
  @Optional
  private Platform platform;
  @Input
  @Optional
  private List<String> additionalModules;
  @Input
  @Optional
  private List<File> additionalResources;
  @Input
  @Optional
  private Boolean administratorRequired;
  @InputDirectory
  @Optional
  private File assetsDir;
  @Input
  @Optional
  private Boolean createTar;
  @Input
  @Optional
  private Boolean createZip;
  @Input
  @Optional
  private Boolean generateInstaller;
  @InputFile
  @Optional
  private File iconFile;
  @InputDirectory
  @Optional
  private File jdkPath;
  @InputDirectory
  @Optional
  private File jrePath;
  @Input
  @Optional
  private String jreDirectoryName;
  @Input
  @Optional
  private String mainClass;
  @Input
  @Optional
  private Boolean useResourcesAsWorkingDir;
  @Input
  @Optional
  private List<String> vmArgs;
  @Input
  @Optional
  private WindowsConfig winConfig;
  @Input
  @Optional
  private LinuxConfig linuxConfig;
  @Input
  @Optional
  private MacConfig macConfig;

  @Override
  protected Packager createPackager() {
    PackagePluginExtension extension = getProject().getExtensions().getByType(PackagePluginExtension.class);
    Packager packager = ObjectUtil.defaultIfNull(platform, extension.getPlatform()).getPackager();
    BeanUtil.copyProperties(extension, packager, CopyOptions.create().ignoreNullValue());
    BeanUtil.copyProperties(this, packager, CopyOptions.create().ignoreNullValue());
    return packager;
  }

  public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
    winConfig = new WindowsConfig();
    getProject().configure(winConfig, closure);
    return winConfig;
  }

  public MacConfig macConfig(Closure<MacConfig> closure) {
    macConfig = new MacConfig();
    getProject().configure(macConfig, closure);
    return macConfig;
  }

  public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
    linuxConfig = new LinuxConfig();
    getProject().configure(linuxConfig, closure);
    return linuxConfig;
  }
}
