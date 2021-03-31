package com.unclezs.novel.app.jfx.packager.task;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.packager.PackagePlugin;
import com.unclezs.novel.app.jfx.packager.model.LauncherConfig;
import java.io.File;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * 部署任务
 *
 * @author blog.unclezs.com
 * @since 2021/03/29 17:12
 */
@Setter
@Getter
public class DeployAppTask extends DefaultTask {

  @Input
  private boolean autoCreate = true;
  @Input
  private boolean onlyCreate = false;

  public DeployAppTask() {
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("部署更新文件");
  }

  @TaskAction
  public void deploy() {
    Project project = getProject();
    PackagePluginExtension extension = project.getExtensions().getByType(PackagePluginExtension.class);
    if (autoCreate) {
      extension.getLauncher().setOnlyCreateDeployFile(true);
      Task packageTask = project.getTasks().getByName("package");
      packageTask.getActions().forEach(action -> action.execute(packageTask));
    }
    if (onlyCreate) {
      return;
    }
    LauncherConfig launcher = extension.getLauncher();
    File deployDir = new File(project.getBuildDir(), launcher.getDeployDir());
    File[] files = deployDir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().equals(launcher.getConfigName())) {
          FileUtil.copy(file, new File(URI.create(launcher.getConfigServerUri())), true);
        } else {
          FileUtil.copy(file, new File(URI.create(launcher.getServerUri().concat("/").concat(file.getName()))), true);
        }
      }
    }
  }
}
