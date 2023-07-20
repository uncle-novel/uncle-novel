package com.unclezs.novel.app.packager.subtask.windows;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.Logger;
import com.unclezs.novel.app.packager.util.VelocityUtils;

import java.io.File;

/**
 * 使用exe4j创建exe文件
 *
 * @author blog.unclezs.com
 * @since 2021/4/5 23:19
 */
public class CreateExe extends BaseSubTask {

  public CreateExe() {
    super("创建EXE");
  }

  @Override
  protected Object run() throws Exception {
    File config = new File(packager.getAssetsFolder(), packager.getName().concat(".exe4j"));
    VelocityUtils.render("/packager/windows/exe4j.vm", config, packager);
    File projectDir = packager.getProject().getProjectDir();
    String exe4jc = FileUtil.file(projectDir.getParentFile(), "/packager/exe4j9/bin/exe4jc.exe").getAbsolutePath();
    if (!FileUtil.exist(exe4jc)) {
      exe4jc = "exe4jc";
    }
    Logger.info("使用 exe4jc 创建 exe 文件: {}", exe4jc);
    ExecUtils.create(exe4jc).add(config).exec();
    return new File(packager.getAppFolder(), packager.getDisplayName().concat(".exe"));
  }
}
