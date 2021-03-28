package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

/**
 * 拷贝依赖
 *
 * @author blog.unclezs.com
 * @since 2021/03/27 17:41
 */
public class CopyDependencies extends ArtifactGenerator {

  public CopyDependencies() {
    super("Dependencies");
  }

  @Override
  protected File doApply(Packager packager) {
    File libsFolder = new File(packager.getJarFileDestinationFolder(), "library");
    Project project = Context.getProject();
    FileUtil.del(libsFolder);
    project.copy(c -> {
      c.from(project.getConfigurations().getByName("runtimeClasspath"));
      c.into(project.file(libsFolder));
    });
    // 拷贝项目jar包
    project.copy(c -> {
      c.from(((Jar) project.getTasks().getByName("jar")).getArchiveFile());
      c.into(libsFolder);
    });
    return libsFolder;
  }
}
