package com.unclezs.novel.app.packager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.jfx.launcher.Platform;
import com.unclezs.jfx.launcher.Resource;
import com.unclezs.jfx.launcher.Resource.Type;
import com.unclezs.novel.app.packager.model.LauncherConfig;
import com.unclezs.novel.app.packager.model.PackagerExtension;
import com.unclezs.novel.app.packager.util.FileUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.jvm.tasks.Jar;

/**
 * jfx-launcher辅助工具，生成升级追踪的文件
 *
 * @author blog.unclezs.com
 * @date 2021/04/02 13:28
 */
public class LauncherHelper {

  /**
   * 升级文件输出目录
   */
  public final File outDir;
  private final Project project;
  private final LauncherConfig config;
  private final Map<String, String> mapper;
  private final Manifest manifest;
  /**
   * 如果outDir已经存在，是否删除
   */
  @Setter
  private boolean removeOld = true;

  public LauncherHelper(Project project, File outDir) {
    this.project = project;
    this.outDir = outDir;
    PackagerExtension extension = project.getExtensions().getByType(PackagerExtension.class);
    this.config = extension.getLauncher();
    this.mapper = config.getResourceMapper();
    this.manifest = BeanUtil.toBean(config, Manifest.class);
  }

  /**
   * 生成升级配置文件及相关资源
   */
  public void generate() {
    if (removeOld) {
      FileUtils.del(outDir);
    }
    generateResource();
    generateLibraries();
    // 生成配置文件
    String configPath = mapper(manifest.getConfigPath());
    manifest.setConfigPath(configPath);
    FileUtil.writeUtf8String(manifest.toJson(), FileUtil.file(outDir, configPath));
  }

  /**
   * 生成项目依赖
   */
  private void generateLibraries() {
    // 生成项目依赖
    for (ResolvedArtifact artifact : project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getResolvedArtifacts()) {
      if (artifact.getName().contains("javafx") || artifact.getName().contains("gson")) {
        continue;
      }
      String artifactName;
      if (artifact.getClassifier() != null) {
        artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
      } else {
        artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
      }
      // 拷贝更新文件
      String path = mapper(artifactName);
      final File outFile = new File(outDir, path);
      project.copy(c -> c.from(artifact.getFile()).into(outFile.getParentFile()).rename(old -> artifactName));
      // 填充manifest
      Resource library = new Resource(path, outFile.length(), Platform.fromString(artifact.getClassifier()), Type.JAR);
      manifest.getResources().add(library);
    }
    // 本项目的Jar
    Jar jar = ((Jar) project.getTasks().getByName("jar"));
    String artifactName = jar.getArchiveBaseName().get().concat(".").concat(jar.getArchiveExtension().get());
    String path = mapper(artifactName);
    final File outFile = new File(outDir, path);
    project.copy(c -> c.from(jar.getArchiveFile()).into(outFile.getParentFile()).rename(closure -> artifactName));
    Resource library = new Resource(path, outFile.length(), Type.JAR);
    manifest.getResources().add(library);
  }


  /**
   * 生成额外的资源
   */
  private void generateResource() {
    for (Resource resource : config.getResources()) {
      File file = new File(resource.getPath());
      String path = mapper(file.getName());
      FileUtil.copy(file, new File(outDir, path), true);
      resource.setPath(path);
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
