package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
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
  public File launcherJar;
  private Packager packager;
  private Manifest manifest;

  public CreateRunnableJar() {
    super("Runnable JAR");
  }

  @Override
  protected File doApply(Packager packager) throws IOException {
    this.packager = packager;
    if (packager.userLauncher()) {
      launcherJar = new File(packager.getLibsFolder(), packager.getLauncher().getLauncherJarLibName().concat(".jar"));
      this.manifest = new Manifest();
      this.createManifest();
      this.embeddedManifest();
      this.createDeployFiles();
    } else {
      createRunnableJar();
    }
    return launcherJar;
  }


  /**
   * 创建可执行的Jar
   */
  private void createRunnableJar() {
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

  /**
   * 将配置文件嵌入 launcherJar
   *
   * @throws IOException /
   */
  public void embeddedManifest() throws IOException {
    Logger.info("开始嵌入manifest");
    Path manifestPath = Paths.get(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME);
    File jar = new File(packager.getJarFileDestinationFolder(), packager.getLauncher().getLauncherJarName().concat(".jar"));
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jar))) {
      zos.putNextEntry(new ZipEntry(Manifest.EMBEDDED_CONFIG_NAME));
      zos.write(Files.readAllBytes(manifestPath));
      ZipFile zipFile = new ZipFile(this.launcherJar);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        if (zipEntry.getName().equals(Manifest.EMBEDDED_CONFIG_NAME)) {
          continue;
        }
        zos.putNextEntry(zipEntry);
        zos.write(zipFile.getInputStream(zipEntry).readAllBytes());
      }
      zos.closeEntry();
    }
    launcherJar = jar;
    Logger.info("生成LauncherJar:".concat(jar.getAbsolutePath()));
  }

  /**
   * 创建部署文件
   */
  public void createDeployFiles() {
    File deployDir = new File(Context.getProject().getBuildDir(), packager.getLauncher().getDeployDir());
    FileUtil.del(deployDir);
    FileUtil.copy(new File(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME), new File(deployDir, manifest.getConfigName()), true);
    if (packager.getLauncher().getDeleteAppLibrary()) {
      manifest.getLibs().forEach(library -> {
        File libFile = new File(packager.getLibsFolder().getAbsolutePath(), library.getPath());
        FileUtil.copy(libFile, new File(deployDir, library.getPath()), true);
        if (packager.getLauncher().getDeleteAppLibrary()) {
          FileUtil.del(libFile);
        }
      });
    }
  }
}
