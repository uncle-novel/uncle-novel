package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import java.io.File;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common packagers' settings
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagerSettings {

  protected File outputDirectory;
  protected File licenseFile;
  protected File iconFile;
  protected Boolean generateInstaller;
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
  protected Boolean createTarball;
  protected Boolean createZipball;
  protected Map<String, String> extra;
  protected boolean useResourcesAsWorkingDir;
  protected File assetsDir;
  protected String classpath;
  protected String jreMinVersion;
  protected Manifest manifest;
  protected List<File> additionalModulePaths;
}
