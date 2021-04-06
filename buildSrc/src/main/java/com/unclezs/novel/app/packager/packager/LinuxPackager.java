package com.unclezs.novel.app.packager.packager;

import cn.hutool.core.collection.CollectionUtil;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.subtask.linux.GenerateDeb;
import com.unclezs.novel.app.packager.subtask.linux.GenerateRpm;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Packager for GNU/Linux
 */
public class LinuxPackager extends AbstractPackager {

  @Override
  public List<BaseSubTask> getInstallerTasks() {
    return Arrays.asList(new GenerateDeb(), new GenerateRpm());
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
    // 生成启动脚本
    this.executable = new File(appFolder, name);
    // 生成classpath
    if (CollectionUtil.isNotEmpty(classpath) && !isUseResourcesAsWorkingDir()) {
      classpath = classpath.stream()
        .map(cp -> new File(cp).isAbsolute() ? cp : "$SCRIPTPATH/" + cp)
        .collect(Collectors.toSet());
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
