package com.unclezs.novel.app.jfx.plugin.packager.packager;

import com.unclezs.novel.app.jfx.plugin.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打包的一些通用设置
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagerSetting {

  protected File outputDirectory;
  protected File licenseFile;
  protected File iconFile;
  protected boolean generateInstaller;
  protected String mainClass;
  protected String name;
  protected String displayName;
  protected String version;
  protected String description;
  protected String url;
  protected Boolean administratorRequired;
  protected String organizationName;
  protected String organizationUrl;
  protected String organizationEmail;
  protected Boolean bundleJre;
  protected Boolean customizedJre;
  protected File jrePath;
  protected File jdkPath;
  protected List<File> additionalResources;
  protected List<String> modules;
  protected List<String> additionalModules;
  protected Platform platform;
  protected String envPath;
  protected List<String> vmArgs;
  protected File runnableJar;
  protected Boolean copyDependencies;
  protected String jreDirectoryName;
  protected WindowsConfig winConfig;
  protected LinuxConfig linuxConfig;
  protected MacConfig macConfig;
  protected Boolean createTar;
  protected Boolean createZip;
  protected Map<String, String> extra;
  protected boolean useResourcesAsWorkingDir;
  protected File assetsDir;
  protected String classpath;
  protected String jreMinVersion;
  protected Manifest manifest;
  protected List<File> additionalModulePaths;
  protected LauncherConfig launcher;
  /**
   * 依赖的文件夹名
   */
  protected String libsFolderName = "lib";

  /**
   * 是否启用launcher
   *
   * @return true 是
   */
  public boolean userLauncher() {
    return launcher != null;
  }
}
