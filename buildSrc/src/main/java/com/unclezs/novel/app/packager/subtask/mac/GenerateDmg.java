package com.unclezs.novel.app.packager.subtask.mac;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.unclezs.novel.app.packager.model.MacConfig;
import com.unclezs.novel.app.packager.packager.MacPackager;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * Creates a DMG image file including all app folder's content only for MacOS so app could be easily distributed
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:13
 */
public class GenerateDmg extends BaseSubTask {

  public GenerateDmg() {
    super("DMG image");
  }

  @Override
  public boolean enabled() {
    return packager.getMacConfig().isGenerateDmg();
  }

  @Override
  protected Object run() throws Exception {
    MacPackager macPackager = (MacPackager) packager;

    File appFolder = macPackager.getAppFolder();
    File assetsFolder = macPackager.getAssetsFolder();
    String name = macPackager.getName();
    File outputDirectory = macPackager.getOutputDir();
    File iconFile = macPackager.getPlatform().getPlatformConfig().getIconFile();
    String version = macPackager.getVersion();
    MacConfig macConfig = macPackager.getMacConfig();

    // sets volume name if blank
    String volumeName = defaultIfBlank(macConfig.getVolumeName(), name);

    // removes whitespaces from volume name
    if (StringUtils.containsWhitespace(volumeName)) {
      volumeName = volumeName.replaceAll(" ", "");
      Logger.warn("Whitespaces has been removed from volume name: " + volumeName);
    }

    // final dmg file
    File dmgFile = new File(outputDirectory, name + "_" + version + ".dmg");

    // temp dmg file
    File tempDmgFile = new File(assetsFolder, dmgFile.getName());

    // mount dir
    File mountFolder = new File("/Volumes/" + volumeName);

    // copies background file
    Logger.info("Copying background image");
    File backgroundFolder = FileUtil.mkdir(FileUtil.file(appFolder, ".background"));
    File backgroundFile = new File(backgroundFolder, "background.png");
    if (macConfig.getBackgroundImage() != null) {
      FileUtils.copyFileToFile(macConfig.getBackgroundImage(), backgroundFile);
    } else {
      FileUtils.copyResourceToFile("/packager/mac/background.png", backgroundFile);
    }

    // copies volume icon
    Logger.info("Copying icon file: " + iconFile.getAbsolutePath());
    File volumeIcon = (macConfig.getVolumeIcon() != null) ? macConfig.getVolumeIcon() : iconFile;
    FileUtils.copyFileToFile(volumeIcon, new File(appFolder, ".VolumeIcon.icns"));

    // creates image
    Logger.info("Creating image: " + tempDmgFile.getAbsolutePath());
    ExecUtils.create("hdiutil").add("create")
      .add("-srcfolder", appFolder)
      .add("-volname", volumeName)
      .add("-ov -fs HFS+ -format UDRW")
      .add(tempDmgFile)
      .exec();
//    execute("hdiutil", "create", "-srcfolder", appFolder, "-volname", volumeName, "-ov", "-fs",
//        "HFS+", "-format", "UDRW", tempDmgFile);

    if (mountFolder.exists()) {
      Logger.info("Unmounting volume: " + mountFolder);
      ExecUtils.create("hdiutil")
        .add("detach", mountFolder)
        .exec();
    }

    // mounts image
    Logger.info("Mounting image: " + tempDmgFile.getAbsolutePath());
    String result = ExecUtils.exec("hdiutil", "attach", "-readwrite", "-noverify", "-noautoopen",
      tempDmgFile);
    String deviceName = Arrays.stream(result.split("\n"))
      .filter(s -> s.contains(mountFolder.getAbsolutePath()))
      .map(StringUtils::normalizeSpace)
      .map(s -> s.split(" ")[0])
      .findFirst().orElse(null);
    Logger.info("- Device name: " + deviceName);

    // pause to prevent occasional "Can't get disk" (-1728) issues
    // https://github.com/seltzered/create-dmg/commit/5fe7802917bb85b40c0630b026d33e421db914ea
    ThreadUtil.sleep(2000L);

    // creates a symlink to Applications folder
    Logger.info("Creating Applications link");
    File targetFolder = new File("/Applications");
    File linkFile = new File(mountFolder, "Applications");
    FileUtils.createSymlink(linkFile, targetFolder);

    // renders applescript
    Logger.info("Rendering DMG customization applescript ... ");
    File applescriptFile = new File(assetsFolder, "customize-dmg.applescript");
    VelocityUtils.render("/packager/mac/customize-dmg.applescript.vm", applescriptFile, macPackager);
    Logger.info("Applescript rendered in " + applescriptFile.getAbsolutePath() + "!");

    // runs applescript
    Logger.info("Running applescript");
    ExecUtils.create("/usr/bin/osascript").add(applescriptFile).add(volumeName).exec();

    // makes sure it's not world writeable and user readable
    Logger.info("Fixing permissions...");
    ExecUtils.create("chmod").add("-Rf u+r,go-w").add(mountFolder).exec();

    // makes the top window open itself on mount:
    Logger.info("Blessing ...");
    ExecUtils.create("bless")
      .add("--folder", mountFolder)
      .add("--openfolder", mountFolder)
      .exec();

    // tells the volume that it has a special file attribute
    ExecUtils.create("SetFile")
      .add("-a")
      .add("C", mountFolder)
      .exec();

    // unmounts
    Logger.info("Unmounting volume: " + mountFolder);
    ExecUtils.create("hdiutil")
      .add("detach", mountFolder)
      .exec();

    // compress image
    Logger.info("Compressing disk image...");
    ExecUtils.create("hdiutil")
      .add("convert", tempDmgFile)
      .add("-ov -format UDZO -imagekey zlib-level=9")
      .add("-o", dmgFile)
      .exec();
    FileUtils.del(tempDmgFile);

    // checks if dmg file was created
    if (!dmgFile.exists()) {
      throw new Exception(name + " generation failed!");
    }

    return dmgFile;
  }
}
