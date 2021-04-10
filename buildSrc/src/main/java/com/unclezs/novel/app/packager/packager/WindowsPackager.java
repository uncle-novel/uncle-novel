package com.unclezs.novel.app.packager.packager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.subtask.windows.CreateExe;
import com.unclezs.novel.app.packager.subtask.windows.GenerateMsi;
import com.unclezs.novel.app.packager.subtask.windows.GenerateMsm;
import com.unclezs.novel.app.packager.subtask.windows.GenerateSetup;
import com.unclezs.novel.app.packager.util.Logger;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Packager for Windows
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @date 2021/03/24 0:06
 */
@Setter
@Getter
public class WindowsPackager extends AbstractPackager {

  private File msmFile;

  @Override
  public void doInit() {
    this.winConfig.setInternalName(ObjectUtil.defaultIfNull(this.winConfig.getInternalName(), getName()));
    this.winConfig.setCompanyName(ObjectUtil.defaultIfNull(this.winConfig.getCompanyName(), getOrganizationName()));
    this.winConfig.setCopyright(ObjectUtil.defaultIfNull(this.winConfig.getCopyright(), getOrganizationName()));
    this.winConfig.setProductVersion(ObjectUtil.defaultIfNull(this.winConfig.getProductVersion(), getVersion()));
    this.winConfig.setFileVersion(ObjectUtil.defaultIfNull(this.winConfig.getFileVersion(), getVersion()));
    this.winConfig.setFileDescription(ObjectUtil.defaultIfNull(this.winConfig.getFileDescription(), getDescription()));
  }

  @Override
  protected void doCreateAppStructure() {
    this.executableDestinationFolder = appFolder;
    this.jarFileDestinationFolder = appFolder;
    this.jreDestinationFolder = new File(appFolder, jreDirName);
    this.resourcesDestinationFolder = appFolder;
  }

  /**
   * 使用本地可执行文件创建Windows应用程序文件结构
   */
  @Override
  public void doCreateApp() {
    Logger.infoIndent("开始创建EXE ...");
    // 拷贝可执行Jar到依赖目录
    if (Boolean.FALSE.equals(winConfig.getWrapJar())) {
      FileUtil.copy(jarFile, libsFolder, true);
    }
    classpath.add(jarFile.getAbsolutePath());
    // 生成classpath
    if (CollUtil.isNotEmpty(classpath) && !isUseResourcesAsWorkingDir()) {
      classpath = classpath.stream()
        .map(cp -> new File(cp).isAbsolute() ? cp : "%EXE4J_EXEDIR%/" + cp)
        .collect(Collectors.toSet());
    }
    Logger.info("windows classpath: {}", classpath);
    // 调用 exe4jc 生成 exe
    executable = new CreateExe().apply();
    Logger.infoUnIndent("创建EXE成功!");
  }


  @Override
  public List<BaseSubTask> getInstallerTasks() {
    return Arrays.asList(new GenerateMsm(), new GenerateMsi(), new GenerateSetup());
  }
}
