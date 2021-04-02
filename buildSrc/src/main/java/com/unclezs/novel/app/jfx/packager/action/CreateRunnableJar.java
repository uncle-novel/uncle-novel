package com.unclezs.novel.app.jfx.packager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.subtask.BaseSubTask;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;

/**
 * 创建可运行的jar文件，其中对 Launcher处理
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @date 2021/03/24 0:06
 */
public class CreateRunnableJar extends BaseSubTask {

  public static final String CLASSIFIER = "runnable";
  public File launcherJar;
  private Manifest manifest;

  public CreateRunnableJar() {
    super("创建可执行Jar包");
  }

  @Override
  protected File run() throws IOException {
    createRunnableJar();
    return launcherJar;
  }


  /**
   * 创建可执行的Jar
   */
  private void createRunnableJar() {
    Project project = Context.project;
    File libsFolder = packager.getLibsFolder();
    List<String> dependencies = new ArrayList<>();
    if (libsFolder != null && libsFolder.exists()) {
      // 处理自带启动器情况
      File[] files = libsFolder.listFiles((dir, libName) -> {
        if (!packager.userLauncher() || packager.getLauncher().getRunTimeLibrary() == null) {
          return true;
        }
        return StrUtil.containsAny(libName, packager.getLauncher().getRunTimeLibrary());
      });
      if (files != null) {
        dependencies = Arrays.stream(files)
          .map(f -> libsFolder.getName() + "/" + f.getName())
          .collect(Collectors.toList());
      }
      Logger.info("Runnable Jar 依赖:".concat(dependencies.toString()));
    }
    Jar jarTask = (Jar) project.getTasks().findByName("jar");
    assert jarTask != null;
    jarTask.setProperty("archiveBaseName", packager.getName());
    jarTask.setProperty("archiveVersion", packager.getVersion());
    jarTask.setProperty("archiveClassifier", CLASSIFIER);
    jarTask.setProperty("destinationDirectory", packager.getOutputDir());
    jarTask.getManifest().getAttributes().put("Created-By", "https://blog.unclezs.com");
    jarTask.getManifest().getAttributes().put("Built-By", "https://blog.unclezs.com");
    jarTask.getManifest().getAttributes().put("Build-Jdk", System.getProperty("java.version"));
    jarTask.getManifest().getAttributes().put("Class-Path", StrUtil.join(" ", dependencies.toArray(new Object[0])));
    jarTask.getManifest().getAttributes().put("Main-Class", packager.getMainClass());
    // 自定义manifest内容
    if (packager.getManifest() != null) {
      jarTask.getManifest().attributes(packager.getManifest().getAdditionalEntries());
      packager.getManifest().getSections().forEach(s -> jarTask.getManifest().attributes(s.getEntries(), s.getName()));
    }
    jarTask.getActions().forEach(action -> action.execute(jarTask));
    launcherJar = jarTask.getArchiveFile().get().getAsFile();
  }

  public void createManifest() throws IOException {
    Logger.info("开始创建manifest");
    BeanUtil.copyProperties(packager.getLauncher(), manifest, CopyOptions.create().ignoreNullValue());
    manifest.setLibDir(packager.getLibsFolderName());
    String manifestJson = Manifest.GSON.toJson(manifest);
    Path manifestPath = Paths.get(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME);
    Files.writeString(Paths.get(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME), manifestJson);
    Logger.info("生成Launcher配置文件：".concat(manifestPath.toAbsolutePath().toString()));
  }
}
