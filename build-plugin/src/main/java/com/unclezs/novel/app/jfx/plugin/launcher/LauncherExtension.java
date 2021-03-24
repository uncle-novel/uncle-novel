package com.unclezs.novel.app.jfx.plugin.launcher;

import java.io.File;
import lombok.Data;
import org.gradle.api.Project;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 10:46
 */
@Data
public class LauncherExtension {

  private final Project project;
  private File workDir;
  private String nativeLibPath;

  public LauncherExtension(Project project) {
    this.project = project;
  }

  public File getWorkDir() {
    return workDir == null ? new File(String.format("%s/app", project.getProject().getBuildDir()))
        : workDir;
  }
}
