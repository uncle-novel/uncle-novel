package com.unclezs.novel.app.jfx.plugin.packager.action.windows;

import static com.unclezs.novel.app.jfx.plugin.packager.util.CommandUtils.execute;

import com.unclezs.novel.app.jfx.plugin.packager.action.WindowsArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.WindowsPackager;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.XMLUtils;
import java.io.File;


/**
 * Creates a MSI file including all app folder's content only for Windows so app could be easily
 * distributed
 */
public class GenerateMsi extends WindowsArtifactGenerator {

  public GenerateMsi() {
    super("MSI installer");
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.getWinConfig().isGenerateMsi();
  }

  @Override
  protected File doApply(Packager packager) throws Exception {
    WindowsPackager windowsPackager = (WindowsPackager) packager;

    File msmFile = new GenerateMsm().doApply(windowsPackager);
    Logger.info("MSM file generated in " + msmFile);

    File assetsFolder = windowsPackager.getAssetsFolder();
    String name = windowsPackager.getName();
    File outputDirectory = windowsPackager.getOutputDirectory();
    String version = windowsPackager.getVersion();

    // generates WXS file from velocity template
    File wxsFile = new File(assetsFolder, name + ".wxs");
    VelocityUtils.render("windows/wxs.vm", wxsFile, windowsPackager);
    Logger.info("WXS file generated in " + wxsFile + "!");

    // pretiffy wxs
    XMLUtils.prettify(wxsFile);

    // candle wxs file
    Logger.info("Compiling file " + wxsFile);
    File wixobjFile = new File(assetsFolder, name + ".wixobj");
    execute("candle", "-out", wixobjFile, wxsFile);
    Logger.info("WIXOBJ file generated in " + wixobjFile + "!");

    // lighting wxs file
    Logger.info("Linking file " + wixobjFile);
    File msiFile = new File(outputDirectory, name + "_" + version + ".msi");
    execute("light", "-spdb", "-out", msiFile, wixobjFile);

    // setup file
    if (!msiFile.exists()) {
      throw new Exception("MSI installer file generation failed!");
    }

    // sign installer
    sign(msiFile, windowsPackager);

    return msiFile;
  }

}
