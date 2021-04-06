package com.unclezs.novel.app.packager.subtask;

import com.unclezs.novel.app.packager.model.Platform;
import com.unclezs.novel.app.packager.packager.MacPackager;
import java.io.File;
import java.util.UUID;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Compression;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;

/**
 * 创建压缩包 zip 、 tar
 *
 * @author blog.unclezs.com
 * @date 2021/04/03 13:32
 */
public class CreateCompressedPackage extends BaseSubTask {

  /**
   * 是否为Zip包
   */
  private final boolean zip;

  public CreateCompressedPackage(boolean zip) {
    super(String.format("创建%s压缩包", zip ? "ZIP" : "TAR"));
    this.zip = zip;
  }

  @Override
  protected File run() throws Exception {
    Platform platform = packager.getPlatform();
    File appFolder = packager.getAppFolder();
    File executable = packager.getExecutable();
    String jreDirectoryName = packager.getJreDirName();

    File tarFile = new File(packager.getOutputDir(), String.format("%s-%s-%s.%s", packager.getName(), packager.getVersion(), platform, zip ? "zip" : "tar.gz"));
    AbstractArchiveTask task;
    if (zip) {
      task = project.getTasks().create("createZip_" + UUID.randomUUID(), Zip.class);
    } else {
      task = project.getTasks().create("createTar_" + UUID.randomUUID(), Tar.class);
      ((Tar) task).setCompression(Compression.GZIP);
    }
    task.setProperty("archiveFileName", tarFile.getName());
    task.setProperty("destinationDirectory", packager.getOutputDir());
    switch (platform) {
      case win:
        task.from(appFolder.getParentFile(), copySpec -> copySpec.include(appFolder.getName() + "/**"));
        break;
      case linux:
        task.from(appFolder.getParentFile(), copySpec -> {
          copySpec.include(appFolder.getName() + "/**");
          copySpec.exclude(appFolder.getName() + "/" + executable.getName());
          copySpec.exclude(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
        });
        task.from(appFolder.getParentFile(), copySpec -> {
          copySpec.include(appFolder.getName() + "/" + executable.getName());
          copySpec.include(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
          copySpec.setFileMode(0755);
        });
        break;
      case mac:
        MacPackager macPackager = (MacPackager) packager;
        File appFile = macPackager.getAppFile();
        task.from(appFolder, copySpec -> {
          copySpec.include(appFile.getName() + "/**");
          copySpec.exclude(appFile.getName() + "/Contents/MacOS/" + executable.getName());
          copySpec.exclude(appFile.getName() + "/Contents/MacOS/".concat(macPackager.getMacConfig().getStartScriptName()));
          copySpec.exclude(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");

        });
        task.from(appFolder, copySpec -> {
          copySpec.include(appFile.getName() + "/Contents/MacOS/" + executable.getName());
          copySpec.include(appFile.getName() + "/Contents/MacOS/".concat(macPackager.getMacConfig().getStartScriptName()));
          copySpec.include(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
          copySpec.setFileMode(0755);
        });
        break;
      default:
    }
    task.getActions().forEach(action -> action.execute(task));
    return tarFile;
  }
}
