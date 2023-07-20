package com.unclezs.novel.app.packager.subtask.windows;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import com.unclezs.novel.app.packager.packager.WindowsPackager;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import java.io.File;

/**
 * 通过inno setup创建安装包
 *
 * @author blog.unclezs.com
 * @since 2021/4/6 16:11
 */
public class GenerateSetup extends WinSubTask {

  public GenerateSetup() {
    super("生成安装程序");
  }

  @Override
  protected boolean enabled() {
    return packager.getWinConfig().getGenerateSetup();
  }

  @Override
  protected File run() throws Exception {
    // 初始化安装语言
    if (CollectionUtil.isEmpty(packager.getWinConfig().getSetupLanguages())) {
      packager.getWinConfig().getSetupLanguages().put("English", "compiler:Default.isl");
    }
    File assetsFolder = packager.getAssetsFolder();
    String name = packager.getName();
    File outputDirectory = packager.getOutputDir();
    String version = packager.getVersion();

    String fileName = String.format("%s_%s_Setup_%s", name, version, Boolean.TRUE.equals(packager.getX64()) ? "" : "x86");
    // 拷贝文件到静态资源目录
    FileUtils.copyFileToFolder(packager.getWinConfig().getIconFile(), assetsFolder);
    // 生成模板文件，使用GBK编码渲染，防止中文引起失败
    File issFile = new File(assetsFolder, name.concat(".iss"));
    VelocityUtils.render("packager/windows/iss.vm", issFile, packager, CharsetUtil.CHARSET_GBK);
    // 使用inno setup命令行编译器生成Windows安装程序
    ExecUtils.exec("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F".concat(fileName), issFile);
    // 安装文件
    File setupFile = new File(outputDirectory, fileName.concat(".exe"));
    if (!setupFile.exists()) {
      throw new Exception("windows安装包生成失败!");
    }
    // sign installer
    sign(setupFile, (WindowsPackager) packager);
    return setupFile;
  }

}
