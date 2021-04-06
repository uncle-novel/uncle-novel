package com.unclezs.novel.app.packager.task;

import com.unclezs.novel.app.packager.PackagePlugin;
import java.io.File;
import lombok.Getter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 11:49
 */
@Getter
public class UpgradeTask extends DefaultTask {

  @Optional
  @InputFile
  private File outDir;

  @Input
  @Optional
  private Boolean autoDeploy = true;

  public UpgradeTask() {
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("生成manifest");
  }

  @TaskAction
  public void createManifest() {
    if (outDir == null) {
      outDir = new File(getProject().getBuildDir(), "upgrade");
    }
    Upgrade upgrade = new Upgrade(getProject(), outDir);
    upgrade.createLocal();
    if (autoDeploy) {
      upgrade.deploy();
    }
  }
}
