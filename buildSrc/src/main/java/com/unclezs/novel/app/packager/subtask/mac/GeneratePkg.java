package com.unclezs.novel.app.packager.subtask.mac;

import com.unclezs.novel.app.packager.packager.MacPackager;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.ExecUtils;
import java.io.File;

/**
 * 创建一个PKG安装程序文件，其中包含所有应用程序文件夹的内容，仅适用于MacOS，这样应用程序就可以轻松分发
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
public class GeneratePkg extends BaseSubTask {

  public GeneratePkg() {
    super("PKG installer");
  }

  @Override
  public boolean enabled() {
    return packager.getMacConfig().isGeneratePkg();
  }

  @Override
  protected File run() throws Exception {
    MacPackager macPackager = (MacPackager) packager;

    File appFile = macPackager.getAppFile();
    String name = macPackager.getName();
    File outputDirectory = macPackager.getOutputDir();
    String version = macPackager.getVersion();

    File pkgFile = new File(outputDirectory, name + "_" + version + ".pkg");

    // invokes pkgbuild command
    ExecUtils.create("pkgbuild")
      .add("--install-location", "/Applications")
      .add("--component",appFile)
      .add(pkgFile)
      .exec();
    // checks if pkg file was created
    if (!pkgFile.exists()) {
      throw new Exception(name + " generation failed!");
    }

    return pkgFile;
  }

}
