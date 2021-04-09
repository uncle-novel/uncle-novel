package com.unclezs.novel.app.packager.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.jfx.launcher.Platform;
import com.unclezs.jfx.launcher.Resource;
import com.unclezs.jfx.launcher.Resource.Type;
import com.unclezs.novel.app.packager.model.LauncherConfig;
import com.unclezs.novel.app.packager.model.PackagerExtension;
import com.unclezs.novel.app.packager.util.ExecUtils;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.jvm.tasks.Jar;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 13:28
 */
public class Upgrade {

  /**
   * 升级文件输出目录
   */
  public final File outDir;
  private final Project project;
  private final LauncherConfig config;
  private final Map<String, String> mapper;
  private final Manifest manifest;
  private final PackagerExtension extension;
  /**
   * 如果outDir已经存在，是否删除
   */
  @Setter
  private boolean removeOld = true;

  public Upgrade(Project project, File outDir) {
    this.project = project;
    this.outDir = outDir;
    this.extension = project.getExtensions().getByType(PackagerExtension.class);
    this.config = extension.getLauncher();
    this.mapper = config.getFileMapper();
    this.manifest = BeanUtil.toBean(config, Manifest.class);
  }

  /**
   * 拷贝依赖及App Jar包, 生成升级配置文件
   */
  public void createLocal() {
    if (removeOld) {
      FileUtil.del(outDir);
    }
    generateResource();
    generateLibraries();
    // 生成配置文件
    String configPath = mapper(manifest.getConfigPath());
    manifest.setConfigPath(configPath);
    FileUtil.writeUtf8String(manifest.toJson(), FileUtil.file(outDir, configPath));
  }

  private void generateLibraries() {
    // 生成项目依赖
    for (ResolvedArtifact artifact : project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getResolvedArtifacts()) {
      project.copy(c -> {
        if (artifact.getName().contains("javafx")) {
          return;
        }
        String artifactName;
        if (artifact.getClassifier() != null) {
          artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
        } else {
          artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
        }
        // 拷贝更新文件
        String outPath = mapper(artifactName);
        c.from(artifact.getFile()).into(new File(outDir, outPath).getParentFile()).rename(old -> artifactName);
        // 填充manifest
        Resource library = new Resource(outPath, artifact.getFile().length(), Platform.fromString(artifact.getClassifier()), Type.JAR);
        manifest.getResources().add(library);
      });
    }
    // 本项目的Jar
    project.copy(c -> {
      Jar jar = ((Jar) project.getTasks().getByName("jar"));
      String artifactName = jar.getArchiveBaseName().get().concat(".").concat(jar.getArchiveExtension().get());
      String outPath = mapper(artifactName);
      c.from(jar.getArchiveFile()).into(new File(outDir, outPath).getParentFile()).rename(closure -> artifactName);
      Resource library = new Resource(outPath, jar.getArchiveFile().get().getAsFile().length(), Type.JAR);
      manifest.getResources().add(library);
    });
  }


  private void generateResource() {
    for (Resource resource : config.getResources()) {
      File file = new File(resource.getPath());
      String path = file.getName();
      FileUtil.copy(file, new File(outDir, path), true);
      resource.setPath(path);
    }
  }

  /**
   * 部署到远程
   */
  public void deploy() {
    String server = config.getUrl();
    if (server.startsWith("file:")) {
      File serverDir = new File(URI.create(server));
      FileUtil.del(serverDir);
      FileUtil.copyContent(outDir, serverDir, true);
    } else if (server.startsWith("http")) {
      ExecUtils.exec("scp", "-r", outDir, server);
    }
  }

  /**
   * 根据名称获取文件路径
   * <pre>
   * 例子：
   * configPath = [['jar','lib']]
   * name: xx.jar => lib/xx.jar
   * </pre>
   *
   * @param name 名称
   * @return 路径
   */
  private String mapper(String name) {
    String dir = mapper.get(name);
    if (dir == null) {
      String suffix = FileUtil.getSuffix(name);
      dir = mapper.get(suffix);
    }
    if (dir == null) {
      return name;
    }
    return Path.of(dir, name).toString().replace("\\", "/");
  }
}
