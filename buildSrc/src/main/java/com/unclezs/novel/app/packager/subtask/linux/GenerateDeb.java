package com.unclezs.novel.app.packager.subtask.linux;

import com.netflix.gradle.plugins.deb.Deb;
import com.unclezs.novel.app.packager.Context;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;

import java.io.File;
import java.util.UUID;

/**
 * Creates a DEB package file including all app folder's content only for GNU/Linux so app could be
 * easily distributed on Gradle context
 */
public class GenerateDeb extends BaseSubTask {

  public GenerateDeb() {
    super("DEB package");
  }

  @Override
  public boolean enabled() {
    return packager.getLinuxConfig().isGenerateDeb();
  }

  @Override
  protected File run() {
    File assetsFolder = packager.getAssetsFolder();
    String name = packager.getName();
    String description = packager.getDescription();
    File appFolder = packager.getAppFolder();
    File outputDirectory = packager.getOutputDir();
    String version = packager.getVersion();
    boolean bundleJre = packager.getBundleJre();
    String jreDirectoryName = packager.getJreDirName();
    File executable = packager.getExecutable();
    String organizationName = packager.getOrganizationName();
    String organizationEmail = packager.getOrganizationEmail();

    // generates desktop file from velocity template
    File desktopFile = new File(assetsFolder, name + ".desktop");
    VelocityUtils.render("linux/desktop.vm", desktopFile, packager);
    Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());

    // generated deb file
    File debFile = new File(outputDirectory, name + "_" + version + ".deb");

    Deb debTask = createDebTask();
    debTask.setProperty("archiveFileName", debFile.getName());
    debTask.setProperty("destinationDirectory", outputDirectory);
    debTask.setPackageName("uncle-novel");
    debTask.setPackageDescription(description);
    debTask.setPackager(organizationName);
    debTask.setUploaders(organizationName);
    debTask.setMaintainer(organizationName + (organizationEmail != null ? " <" + organizationEmail + ">" : ""));
    debTask.setPriority("optional");
    debTask.setArchStr("amd64");
    debTask.setDistribution("development");
    debTask.setRelease("1");

    // installation destination
    debTask.into("/opt/" + name);

    // includes app folder files, except executable file and jre/bin/java
    debTask.from("build/assets/" + name + ".desktop", c -> {
      c.into(name);
    });

    // executable
    debTask.from(appFolder.getParentFile(), c -> {
      c.include(appFolder.getName() + "/" + executable.getName());
      c.setFileMode(0755);
    });

    // java binary file
    if (bundleJre) {
      debTask.from(appFolder.getParentFile(), c -> {
        c.include(appFolder.getName() + "/" + jreDirectoryName + "/bin/java");
        c.setFileMode(0755);
      });
    }

    // desktop file
    debTask.from(desktopFile.getParentFile().getAbsolutePath(), c -> {
      c.into("/usr/share/applications");
    });

    // symbolic link in /usr/local/bin to app binary
    debTask.link("/usr/local/bin/" + name, "/opt/" + name + "/" + name, 0777);

    // runs deb task
    debTask.getActions().forEach(action -> action.execute(debTask));

    return debFile;

  }

  private Deb createDebTask() {
    return Context.project.getTasks()
      .create("createDeb_" + UUID.randomUUID(), Deb.class);
  }

}
