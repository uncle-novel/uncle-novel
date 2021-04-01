package com.unclezs.novel.app.jfx.packager.packager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import com.unclezs.novel.app.jfx.packager.util.Platform;
import com.unclezs.novel.app.jfx.packager.util.VelocityUtils;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

  private File manifestFile;
  private File msmFile;

  public WindowsPackager() {
  }

  @Override
  public void doInit() {
    // 初始化安装语言
    if (platform == Platform.windows && CollectionUtil.isEmpty(winConfig.getSetupLanguages())) {
      winConfig.setSetupLanguages(new LinkedHashMap<>(3));
      winConfig.getSetupLanguages().put("english", "compiler:Default.isl");
      winConfig.getSetupLanguages().put("spanish", "compiler:Languages\\Spanish.isl");
    }
    this.winConfig.setTxtProductVersion(ObjectUtil.defaultIfNull(this.winConfig.getTxtProductVersion(), getVersion()));
    this.winConfig.setCompanyName(ObjectUtil.defaultIfNull(this.winConfig.getCompanyName(), getOrganizationName()));
    this.winConfig.setCopyright(ObjectUtil.defaultIfNull(this.winConfig.getCopyright(), getOrganizationName()));
    this.winConfig.setFileDescription(ObjectUtil.defaultIfNull(this.winConfig.getFileDescription(), getDescription()));
    this.winConfig.setProductName(ObjectUtil.defaultIfNull(this.winConfig.getProductName(), getName()));
    this.winConfig.setInternalName(ObjectUtil.defaultIfNull(this.winConfig.getInternalName(), getName()));
    this.winConfig.setOriginalFilename(ObjectUtil.defaultIfNull(this.winConfig.getOriginalFilename(), getName().concat(".exe")));
  }

  @Override
  protected void doCreateAppStructure() {
    this.executableDestinationFolder = appFolder;
    this.jarFileDestinationFolder = appFolder;
    this.jreDestinationFolder = new File(appFolder, jreDirName);
    this.resourcesDestinationFolder = appFolder;
  }

  /**
   * Creates a Windows app file structure with native executable
   */
  @Override
  public void doCreateApp() throws Exception {
    Logger.infoIndent("Creating windows EXE ...");
    // copies JAR to app folder
    if (!winConfig.isWrapJar()) {
      FileUtil.copy(jarFile, libsFolder, true);
    }

    // generates manifest file to require administrator privileges from velocity template
    manifestFile = new File(assetsFolder, name + ".exe.manifest");
    VelocityUtils.render("windows/exe.manifest.vm", manifestFile, this);
    Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

    // sets executable file
    executable = new File(appFolder, name + ".exe");
    // process classpath
    if (StrUtil.isNotBlank(classpath)) {
      classpathList = Arrays.asList(classpath.split(";"));
      if (!isUseResourcesAsWorkingDir()) {
        classpathList = classpathList.stream()
          .map(cp -> new File(cp).isAbsolute() ? cp : "%EXEDIR%/" + cp)
          .collect(Collectors.toList());
      }
    }
    if (userLauncher()) {
      classpathList.addAll(launcher.getClasspath());
    }
    if (!classpathList.isEmpty()) {
      classpath = StringUtils.join(classpathList, ";");
      Logger.info("windows classpath:" + classpath);
    }
    // invokes launch4j to generate windows executable
    executable = Context.createExe(this);

    Logger.infoUnindent("Windows EXE file created in " + executable + "!");
  }

}
