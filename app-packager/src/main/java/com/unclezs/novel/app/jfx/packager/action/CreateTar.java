package com.unclezs.novel.app.jfx.packager.action;

import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.packager.MacPackager;
import com.unclezs.novel.app.jfx.packager.packager.Packager;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import java.io.File;
import java.util.UUID;
import org.gradle.api.tasks.bundling.Compression;
import org.gradle.api.tasks.bundling.Tar;

/**
 * Creates tarball (tar.gz file) on Gradle context
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @date 2021/03/24 11:26
 */
public class CreateTar extends ArtifactGenerator {

  public CreateTar() {
    super("Tar");
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.getCreateTar();
  }

  @Override
  protected File doApply(Packager packager) throws Exception {

    String name = packager.getName();
    String version = packager.getVersion();
    Platform platform = packager.getPlatform();
    File outputDirectory = packager.getOutputDirectory();
    File appFolder = packager.getAppFolder();
    File executable = packager.getExecutable();
    String jreDirectoryName = packager.getJreDirectoryName();

    File tarFile = new File(outputDirectory, name + "-" + version + "-" + platform + ".tar.gz");

    Tar tarTask = createTarTask();
    tarTask.setProperty("archiveFileName", tarFile.getName());
    tarTask.setProperty("destinationDirectory", outputDirectory);
    tarTask.setCompression(Compression.GZIP);

    // if zipball is for windows platform
    if (Platform.windows.equals(platform)) {
      tarTask.from(appFolder.getParentFile(), copySpec -> {
        copySpec.include(appFolder.getName() + "/**");
      });
    }
    // if zipball is for linux platform
    else if (Platform.linux.equals(platform)) {
      tarTask.from(appFolder.getParentFile(), copySpec -> {
        copySpec.include(appFolder.getName() + "/**");
        copySpec.exclude(appFolder.getName() + "/" + executable.getName());
        copySpec.exclude(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
      });
      tarTask.from(appFolder.getParentFile(), copySpec -> {
        copySpec.include(appFolder.getName() + "/" + executable.getName());
        copySpec.include(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
        copySpec.setFileMode(0755);
      });

    }

    // if zip is for macos platform
    else if (Platform.mac.equals(platform)) {

      MacPackager macPackager = (MacPackager) packager;
      File appFile = macPackager.getAppFile();

      tarTask.from(appFolder, copySpec -> {
        copySpec.include(appFile.getName() + "/**");
        copySpec.exclude(appFile.getName() + "/Contents/MacOS/" + executable.getName());
        copySpec.exclude(appFile.getName() + "/Contents/MacOS/".concat(macPackager.getMacConfig().getStartScriptName()));
        copySpec.exclude(
            appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");

      });
      tarTask.from(appFolder, copySpec -> {
        copySpec.include(appFile.getName() + "/Contents/MacOS/" + executable.getName());
        copySpec.include(appFile.getName() + "/Contents/MacOS/".concat(macPackager.getMacConfig().getStartScriptName()));
        copySpec.include(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
        copySpec.setFileMode(0755);
      });

    }

    tarTask.getActions().forEach(action -> action.execute(tarTask));

    return tarFile;
  }

  private Tar createTarTask() {
    return Context.getProject().getTasks()
        .create("createTar_" + UUID.randomUUID(), Tar.class);
  }

}
