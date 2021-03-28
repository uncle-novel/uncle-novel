package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import java.io.File;
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
public class CreateRunnableJar extends ArtifactGenerator {

  public static final String CLASSIFIER = "runnable";

  public CreateRunnableJar() {
    super("Runnable JAR");
  }

  @Override
  protected File doApply(Packager packager) {
    Project project = Context.getProject();
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
    jarTask.setProperty("destinationDirectory", packager.getOutputDirectory());
    jarTask.getManifest().getAttributes().put("Created-By", "https://blog.unclezs.com");
    jarTask.getManifest().getAttributes().put("Built-By", "https://blog.unclezs.com");
    jarTask.getManifest().getAttributes().put("Build-Jdk", System.getProperty("java.version"));
    jarTask.getManifest().getAttributes().put("Class-Path", StrUtil.join(" ", dependencies.toArray(new Object[0])));
    jarTask.getManifest().getAttributes().put("Main-Class", packager.getMainClass());
    // 自定义manifest内容
    Manifest manifest = packager.getManifest();
    if (manifest != null) {
      jarTask.getManifest().attributes(manifest.getAdditionalEntries());
      manifest.getSections().forEach(s -> jarTask.getManifest().attributes(s.getEntries(), s.getName()));
    }
    jarTask.getActions().forEach(action -> action.execute(jarTask));
    return jarTask.getArchiveFile().get().getAsFile();
  }

}
