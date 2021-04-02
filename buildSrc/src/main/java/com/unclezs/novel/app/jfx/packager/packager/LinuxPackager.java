package com.unclezs.novel.app.jfx.packager.packager;

import com.unclezs.novel.app.jfx.packager.action.linux.GenerateDeb;
import com.unclezs.novel.app.jfx.packager.action.linux.GenerateRpm;
import com.unclezs.novel.app.jfx.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import com.unclezs.novel.app.jfx.packager.util.VelocityUtils;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Packager for GNU/Linux
 */
public class LinuxPackager extends AbstractPackager {

  public LinuxPackager() {
    super();
    installerGenerators.add(new GenerateDeb());
    installerGenerators.add(new GenerateRpm());
  }

  @Override
  public void doInit() {
  }

  @Override
  protected void doCreateAppStructure() throws Exception {
    // sets common folders
    this.executableDestinationFolder = appFolder;
    this.jarFileDestinationFolder = appFolder;
    this.jreDestinationFolder = new File(appFolder, jreDirName);
    this.resourcesDestinationFolder = appFolder;
  }

  /**
   * Creates a GNU/Linux app folder with native executable
   */
  @Override
  public void doCreateApp() throws Exception {

    Logger.infoIndent("Creating GNU/Linux executable ...");

    // sets executable file
    this.executable = new File(appFolder, name);

    // process classpath
    if (classpath != null) {
      classpathList = Arrays.asList(classpath.split("[:;]"));
      if (!isUseResourcesAsWorkingDir()) {
        classpathList = classpathList.stream()
            .map(cp -> new File(cp).isAbsolute() ? cp : "$SCRIPTPATH/" + cp)
            .collect(Collectors.toList());
      }
      classpath = StringUtils.join(classpathList, ":");
    }

    // generates startup.sh script to boot java app
    File startupFile = new File(assetsFolder, "startup.sh");
    VelocityUtils.render("linux/startup.sh.vm", startupFile, this);
    Logger.info("Startup script generated in " + startupFile.getAbsolutePath());

    // concats linux startup.sh script + generated jar in executable (binary)
    FileUtils.concat(executable, startupFile, jarFile);

    // sets execution permissions
    executable.setExecutable(true, false);

    Logger.infoUnIndent("GNU/Linux executable created in " + executable.getAbsolutePath() + "!");
  }

}
