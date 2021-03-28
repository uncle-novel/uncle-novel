package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author blog.unclezs.com
 * @date 2021/03/29 1:41
 */
public class GenerateLauncher extends ArtifactGenerator {

  private Packager packager;
  private Manifest manifest;

  @Override
  protected File doApply(Packager packager) throws Exception {
    this.packager = packager;
    this.manifest = new Manifest();
    createManifest();
    embeddedManifest();
    deploy();
    return null;
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.userLauncher();
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

  public void embeddedManifest() throws IOException {
    Logger.info("开始嵌入manifest");
    Path manifestPath = Paths.get(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME);
    File launcherJar = new File(packager.getJarFileDestinationFolder(), "launcher.jar");
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(launcherJar))) {
      zos.putNextEntry(new ZipEntry(Manifest.EMBEDDED_CONFIG_NAME));
      zos.write(Files.readAllBytes(manifestPath));
      ZipFile zipFile = new ZipFile(packager.getJarFile());
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
    packager.setJarFile(launcherJar);
    Logger.info("生成LauncherJar:".concat(launcherJar.getAbsolutePath()));
  }

  public void deploy() {
    System.out.println(manifest);
    FileUtil.copyFile(new File(packager.getLibsFolder().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME), new File(URI.create(manifest.getConfigServerUri())), StandardCopyOption.REPLACE_EXISTING);
    for (Library lib : manifest.getLibs()) {
      FileUtil.copyFile(Paths.get(packager.getLibsFolder().getAbsolutePath(), lib.getPath()).toFile(), new File(URI.create(manifest.getServerUri().concat("/").concat(lib.getPath()))),
        StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
