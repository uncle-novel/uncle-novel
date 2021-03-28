package com.unclezs.novel.app.jfx.plugin.packager.packager;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Packager for Windows
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @date 2021/03/24 0:06
 */
@Setter
@Getter
public class WindowsPackager extends Packager {

  private File manifestFile;
  private File msmFile;

  public WindowsPackager() {
  }

  @Override
  public void doInit() {
    this.winConfig.setDefaults(this);
  }

  @Override
  protected void doCreateAppStructure() {
    this.executableDestinationFolder = appFolder;
    this.jarFileDestinationFolder = appFolder;
    this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
    this.resourcesDestinationFolder = appFolder;
  }

  /**
   * Creates a Windows app file structure with native executable
   */
  @Override
  public File doCreateApp() throws Exception {
    Logger.infoIndent("Creating windows EXE ...");
    // copies JAR to app folder
    if (!winConfig.isWrapJar()) {
      FileUtil.copy(jarFile, libsFolder, true);
    }

    // generates manifest file to require administrator privileges from velocity template
    manifestFile = new File(assetsFolder, name + ".exe.manifest");
    VelocityUtils.render("windows/exe.manifest.vm", manifestFile, this);
    Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

    // sets executable file
    executable = new File(appFolder, name + ".exe");

    // process classpath
    if (classpath != null) {
      classpathList = Arrays.asList(classpath.split(";"));
      if (!isUseResourcesAsWorkingDir()) {
        classpathList = classpathList.stream()
            .map(cp -> new File(cp).isAbsolute() ? cp : "%EXEDIR%/" + cp)
            .collect(Collectors.toList());
      }
      classpath = StringUtils.join(classpathList, ";");
    }

    // invokes launch4j to generate windows executable
    executable = Context.createExe(this);

    Logger.infoUnindent("Windows EXE file created in " + executable + "!");

    return appFolder;
  }

}
