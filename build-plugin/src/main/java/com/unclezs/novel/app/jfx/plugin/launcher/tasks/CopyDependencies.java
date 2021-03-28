package com.unclezs.novel.app.jfx.plugin.launcher.tasks;

import static com.unclezs.novel.app.jfx.plugin.launcher.LauncherPlugin.GROUP;

import com.unclezs.novel.app.jfx.plugin.packager.model.LauncherConfig;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.tasks.Jar;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 10:41
 */
public class CopyDependencies extends DefaultTask {

  public CopyDependencies() {
    setGroup(GROUP);
    setDescription("拷贝依赖Jar");
  }

  @TaskAction
  public void copy() {
    Project project = getProject();
    LauncherConfig options = project.getExtensions().getByType(LauncherConfig.class);
    File workDir = options.getWorkDir();
    //noinspection ResultOfMethodCallIgnored
    workDir.delete();
    // 拷贝依赖
    project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration()
      .getResolvedArtifacts().forEach(artifact -> {
      project.copy(c -> {
        c.from(artifact.getFile());
        c.into(workDir);
        if (artifact.getClassifier() != null) {
          c.rename(closure -> String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(),
            artifact.getExtension()));
        } else {
          c.rename(closure -> String.format("%s.%s", artifact.getName(), artifact.getExtension()));
        }
      });
    });
    // 拷贝项目jar包
    project.copy(c -> {
      c.from(((Jar) project.getTasks().getByName("jar")).getArchiveFile());
      c.into(workDir);
    });

  }
}
