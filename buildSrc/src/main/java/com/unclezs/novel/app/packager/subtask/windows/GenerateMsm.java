package com.unclezs.novel.app.packager.subtask.windows;

import cn.hutool.core.util.CharsetUtil;
import com.unclezs.novel.app.packager.packager.WindowsPackager;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import java.io.File;

/**
 * 创建Msm
 *
 * @author blog.unclezs.com
 * @since 2021/4/6 16:09
 */
public class GenerateMsm extends BaseSubTask {

  public GenerateMsm() {
    super("创建MSM文件");
  }

  @Override
  public boolean enabled() {
    return packager.getWinConfig().getGenerateMsm() && packager.getWinConfig().getGenerateMsi();
  }


  @Override
  protected Object run() throws Exception {
    WindowsPackager windowsPackager = (WindowsPackager) packager;

    if (windowsPackager.getMsmFile() != null) {
      return windowsPackager.getMsmFile();
    }

    File assetsFolder = windowsPackager.getAssetsFolder();
    String name = windowsPackager.getName();
    File outputDirectory = windowsPackager.getOutputDir();
    String version = windowsPackager.getVersion();

    // 生成WXS文件
    File wxsFile = new File(assetsFolder, name + ".msm.wxs");
    VelocityUtils.render("packager/windows/msm.wxs.vm", wxsFile, windowsPackager, CharsetUtil.CHARSET_GBK);
    Logger.info("WXS file generated in " + wxsFile + "!");

    // prettify wxs
//    XmlUtils.prettify(wxsFile, CharsetUtil.GBK);

    // candle wxs file
    Logger.info("Compiling file " + wxsFile);
    File wixobjFile = new File(assetsFolder, name + ".msm.wixobj");
    ExecUtils.exec("candle", "-out", wixobjFile, wxsFile);
    Logger.info("WIXOBJ file generated in " + wixobjFile + "!");

    // lighting wxs file
    Logger.info("Linking file " + wixobjFile);
    File msmFile = new File(outputDirectory, name + "_" + version + ".msm");
    ExecUtils.exec("light", "-spdb", "-out", msmFile, wixobjFile);

    // setup file
    if (!msmFile.exists()) {
      throw new Exception("MSI installer file generation failed!");
    }

    windowsPackager.setMsmFile(msmFile);

    return msmFile;
  }

}
