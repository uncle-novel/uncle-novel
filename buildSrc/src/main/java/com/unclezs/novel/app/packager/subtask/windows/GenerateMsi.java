package com.unclezs.novel.app.packager.subtask.windows;

import cn.hutool.core.util.CharsetUtil;
import com.unclezs.novel.app.packager.packager.WindowsPackager;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
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
    if (!packager.getWinConfig().getGenerateMsm()) {
      new GenerateMsm().apply();
    }
    File assetsFolder = packager.getAssetsFolder();
    String name = packager.getName();
    File outputDirectory = packager.getOutputDir();
    String version = packager.getVersion();

    // generates WXS file from velocity template
    File wxsFile = new File(assetsFolder, name + ".wxs");
    VelocityUtils.render("windows/wxs.vm", wxsFile, packager, CharsetUtil.systemCharset());
    Logger.info("WXS file generated in " + wxsFile + "!");

    // pretiffy wxs
//    XmlUtils.prettify(wxsFile, CharsetUtil.systemCharset().name());

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
    sign(msiFile, (WindowsPackager) packager);

    return msiFile;
  }

}
