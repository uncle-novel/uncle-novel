package com.unclezs.novel.app.packager.subtask.linux;

import com.netflix.gradle.plugins.deb.Deb;
import com.unclezs.novel.app.packager.Context;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import org.vafer.jdeb.Console;
import org.vafer.jdeb.DataProducer;
import org.vafer.jdeb.DebMaker;
import org.vafer.jdeb.PackagingException;
import org.vafer.jdeb.ant.Data;
import org.vafer.jdeb.ant.Mapper;
import org.vafer.jdeb.mapping.PermMapper;
import org.vafer.jdeb.producers.DataProducerLink;
import org.vafer.jdeb.shaded.commons.compress.archivers.zip.UnixStat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 生成deb
 *
 * @author unclezs
 * @date 2022/05/12
 */
public class GenerateDeb extends BaseSubTask {
  private final Console console;

  public GenerateDeb() {
    super("DEB package");
    console = new Console() {

      @Override
      public void warn(String message) {
        Logger.warn(message);
      }

      @Override
      public void info(String message) {
        Logger.info(message);
      }

      @Override
      public void debug(String message) {
        Logger.info(message);
      }

    };
  }

  @Override
  public boolean enabled() {
    return packager.getLinuxConfig().isGenerateDeb();
  }

  @Override
  protected File run() throws PackagingException {
    File assetsFolder = packager.getAssetsFolder();
    String name = packager.getName();
    File appFolder = packager.getAppFolder();
    File outputDirectory = packager.getOutputDir();
    String version = packager.getVersion();
    boolean bundleJre = packager.getBundleJre();
    String jreDirectoryName = packager.getJreDirName();
    File executable = packager.getExecutable();
    File javaFile = new File(appFolder, jreDirectoryName + "/bin/java");

    // generates desktop file from velocity template
    File desktopFile = new File(assetsFolder, name + ".desktop");
    VelocityUtils.render("packager/linux/desktop.vm", desktopFile, packager);
    Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());

    // generates deb control file from velocity template
    File controlFile = new File(assetsFolder, "control");
    VelocityUtils.render("packager/linux/control.vm", controlFile, packager);
    Logger.info("Control file rendered in " + controlFile.getAbsolutePath());

    // generated deb file
    File debFile = new File(outputDirectory, name + "_" + version + ".deb");

    // create data producers collections

    List<DataProducer> conffilesProducers = new ArrayList<>();
    List<DataProducer> dataProducers = new ArrayList<>();

    // builds app folder data producer, except executable file and jre/bin/java

    Mapper appFolderMapper = new Mapper();
    appFolderMapper.setType("perm");
    appFolderMapper.setPrefix("/opt/" + name);
    appFolderMapper.setFileMode("644");

    Data appFolderData = new Data();
    appFolderData.setType("directory");
    appFolderData.setSrc(appFolder);
    appFolderData.setExcludes(executable.getName() + (bundleJre ?
      "," + jreDirectoryName + "/bin/java" + "," + jreDirectoryName + "/lib/jspawnhelper" :
      ""));
    appFolderData.addMapper(appFolderMapper);

    dataProducers.add(appFolderData);

    // builds executable data producer

    Mapper executableMapper = new Mapper();
    executableMapper.setType("perm");
    executableMapper.setPrefix("/opt/" + name);
    executableMapper.setFileMode("755");

    Data executableData = new Data();
    executableData.setType("file");
    executableData.setSrc(new File(appFolder.getAbsolutePath() + "/" + name));
    executableData.addMapper(executableMapper);

    dataProducers.add(executableData);

    // desktop file data producer

    Mapper desktopFileMapper = new Mapper();
    desktopFileMapper.setType("perm");
    desktopFileMapper.setPrefix("/usr/share/applications");

    Data desktopFileData = new Data();
    desktopFileData.setType("file");
    desktopFileData.setSrc(desktopFile);
    desktopFileData.addMapper(desktopFileMapper);

    dataProducers.add(desktopFileData);

    // java binary file data producer

    if (bundleJre) {
      Mapper javaBinaryMapper = new Mapper();
      javaBinaryMapper.setType("perm");
      javaBinaryMapper.setFileMode("755");
      javaBinaryMapper.setPrefix("/opt/" + name + "/" + jreDirectoryName + "/bin");

      Data javaBinaryData = new Data();
      javaBinaryData.setType("file");
      javaBinaryData.setSrc(javaFile);
      javaBinaryData.addMapper(javaBinaryMapper);

      dataProducers.add(javaBinaryData);

      // set correct permissions on jre/lib/jspawnhelper
      Mapper javaSpawnHelperMapper = new Mapper();
      javaSpawnHelperMapper.setType("perm");
      javaSpawnHelperMapper.setFileMode("755");
      javaSpawnHelperMapper.setPrefix("/opt/" + name + "/" + jreDirectoryName + "/lib");

      File jSpawnHelperFile = new File(appFolder, jreDirectoryName + "/lib/jspawnhelper");

      Data javaSpawnHelperData = new Data();
      javaSpawnHelperData.setType("file");
      javaSpawnHelperData.setSrc(jSpawnHelperFile);
      javaSpawnHelperData.addMapper(javaSpawnHelperMapper);

      dataProducers.add(javaSpawnHelperData);
    }

    // symbolic link in /usr/local/bin to app binary data producer

    DataProducer linkData = createLink("/usr/local/bin/" + name, "/opt/" + name + "/" + name);

    dataProducers.add(linkData);

    // builds deb file
    DebMaker debMaker = new DebMaker(console, dataProducers, conffilesProducers);
    debMaker.setDeb(debFile);
    debMaker.setControl(controlFile.getParentFile());
    debMaker.setCompression("gzip");
    debMaker.setDigest("SHA256");
    debMaker.validate();
    debMaker.makeDeb();
    return debFile;

  }

  /**
   * 创建链接
   *
   * @param name   名字
   * @param target 目标
   * @return {@link DataProducer}
   */
  private DataProducer createLink(String name, String target) {
    int linkMode = UnixStat.LINK_FLAG | Integer.parseInt("777", 8);
    org.vafer.jdeb.mapping.Mapper linkMapper = new PermMapper(0, 0, "root", "root", linkMode, linkMode, 0, null);
    return new DataProducerLink(name, target, true, null, null, new org.vafer.jdeb.mapping.Mapper[] {linkMapper});
  }

}
