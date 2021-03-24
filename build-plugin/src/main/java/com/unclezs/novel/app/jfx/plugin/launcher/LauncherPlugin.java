package com.unclezs.novel.app.jfx.plugin.launcher;

import com.unclezs.novel.app.jfx.plugin.launcher.tasks.CopyDependencies;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 10:45
 */
public class LauncherPlugin implements Plugin<Project> {

  public static final String GROUP = "launcher";

  @Override
  public void apply(Project project) {
    project.getExtensions().create("launcher", LauncherExtension.class, project);
    project.getTasks().create("copyDependencies", CopyDependencies.class);
  }
}
