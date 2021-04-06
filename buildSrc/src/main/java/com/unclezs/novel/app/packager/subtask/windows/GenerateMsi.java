package com.unclezs.novel.app.packager.subtask.windows;

import com.unclezs.novel.app.packager.packager.WindowsPackager;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import com.unclezs.novel.app.packager.util.XmlUtils;
import java.io.File;


/**
 * 创建MSI文件
 *
 * @author blog.unclezs.com
 * @date 2021/4/6 16:10
 */
public class GenerateMsi extends WinSubTask {

  public GenerateMsi() {
    super("生成MSI安装包");
  }

  @Override
  public boolean enabled() {
    return packager.getWinConfig().getGenerateMsi();
  }

  @Override
  protected File run() throws Exception {
    WindowsPackager windowsPackager = (WindowsPackager) packager;

    File msmFile = new GenerateMsm().apply();
    Logger.info("MSM file generated in " + msmFile);

    File assetsFolder = windowsPackager.getAssetsFolder();
    String name = windowsPackager.getName();
    File outputDirectory = windowsPackager.getOutputDir();
    String version = windowsPackager.getVersion();

    // generates WXS file from velocity template
    File wxsFile = new File(assetsFolder, name + ".wxs");
    VelocityUtils.render("windows/wxs.vm", wxsFile, windowsPackager);
    Logger.info("WXS file generated in " + wxsFile + "!");

    // pretiffy wxs
    XmlUtils.prettify(wxsFile);

    // candle wxs file
    Logger.info("Compiling file " + wxsFile);
    File wixobjFile = new File(assetsFolder, name + ".wixobj");
    ExecUtils.create("candle")
      .add("-out", wixobjFile)
      .add(wxsFile)
      .exec();
    Logger.info("WIXOBJ file generated in " + wixobjFile + "!");

    // lighting wxs file
    Logger.info("Linking file " + wixobjFile);
    File msiFile = new File(outputDirectory, name + "_" + version + ".msi");
    ExecUtils.exec("light", "-spdb", "-out", msiFile, wixobjFile);

    // setup file
    if (!msiFile.exists()) {
      throw new Exception("MSI installer file generation failed!");
    }

    // sign installer
    sign(msiFile, windowsPackager);

    return msiFile;
  }

}
