package com.unclezs.novel.app.jfx.plugin.packager.action.windows;

import com.unclezs.novel.app.jfx.plugin.packager.action.ArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.WindowsPackager;
import com.unclezs.novel.app.jfx.plugin.packager.util.CommandUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.XMLUtils;
import java.io.File;

/**
 * Creates a MSI file including all app folder's content only for Windows so app could be easily
 * distributed
 */
public class GenerateMsm extends ArtifactGenerator {

  public GenerateMsm() {
    super("MSI merge module");
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.getWinConfig().isGenerateMsm() && !packager.getWinConfig().isGenerateMsi();
  }

  @Override
  protected File doApply(Packager packager) throws Exception {
    WindowsPackager windowsPackager = (WindowsPackager) packager;

    if (windowsPackager.getMsmFile() != null) {
      return windowsPackager.getMsmFile();
    }

    File assetsFolder = windowsPackager.getAssetsFolder();
    String name = windowsPackager.getName();
    File outputDirectory = windowsPackager.getOutputDirectory();
    String version = windowsPackager.getVersion();

    // generates WXS file from velocity template
    File wxsFile = new File(assetsFolder, name + ".msm.wxs");
    VelocityUtils.render("windows/msm.wxs.vm", wxsFile, windowsPackager);
    Logger.info("WXS file generated in " + wxsFile + "!");

    // pretiffy wxs
    XMLUtils.prettify(wxsFile);

    // candle wxs file
    Logger.info("Compiling file " + wxsFile);
    File wixobjFile = new File(assetsFolder, name + ".msm.wixobj");
    CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
    Logger.info("WIXOBJ file generated in " + wixobjFile + "!");

    // lighting wxs file
    Logger.info("Linking file " + wixobjFile);
    File msmFile = new File(outputDirectory, name + "_" + version + ".msm");
    CommandUtils.execute("light", "-spdb", "-out", msmFile, wixobjFile);

    // setup file
    if (!msmFile.exists()) {
      throw new Exception("MSI installer file generation failed!");
    }

    windowsPackager.setMsmFile(msmFile);

    return msmFile;
  }

}
