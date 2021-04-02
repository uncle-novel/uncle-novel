package com.unclezs.novel.app.jfx.packager.task;

import com.unclezs.novel.app.jfx.packager.PackagePlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 11:49
 */
public class CreateManifestTask extends DefaultTask {

  public CreateManifestTask() {
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("生成manifest");
  }

  @TaskAction
  public void createManifest() {
    Upgrade upgrade = new Upgrade(getProject());
    upgrade.createManifest();
  }
}
