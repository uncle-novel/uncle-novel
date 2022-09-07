package com.unclezs.novel.app.packager.subtask;

import java.io.File;
import org.gradle.api.tasks.bundling.Jar;

/**
 * 创建可运行的jar文件
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/24 0:06
 */
public class CreateRunnableJar extends BaseSubTask {

  public CreateRunnableJar() {
    super("创建可执行Jar包");
  }

  @Override
  protected File run() {
    Jar jarTask = (Jar) project.getTasks().getByName("jar");
    jarTask.getManifest().getAttributes().put("Main-Class", packager.getMainClass());
    jarTask.getActions().forEach(action -> action.execute(jarTask));
    // classpath 增加依赖目录
    packager.getClasspath().add(packager.getLibsFolderPath());
    return jarTask.getArchiveFile().get().getAsFile();
  }
}
