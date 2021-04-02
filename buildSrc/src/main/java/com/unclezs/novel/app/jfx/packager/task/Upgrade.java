package com.unclezs.novel.app.jfx.packager.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.unclezs.jfx.launcher.Library;
import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.jfx.launcher.Platform;
import com.unclezs.novel.app.jfx.packager.packager.PackagerExtension;
import java.io.File;
import lombok.Getter;
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
  public static final String UPGRADE_DIR = "upgrade";
  private final Project project;
  private final PackagerExtension extension;
  @Getter
  private final File upgradeDir;

  public Upgrade(Project project) {
    this.project = project;
    this.extension = project.getExtensions().getByType(PackagerExtension.class);
    this.upgradeDir = new File(project.getBuildDir(), UPGRADE_DIR);
    FileUtil.del(upgradeDir);
  }

  /**
   * 拷贝依赖及App Jar包, 生成升级配置文件
   */
  public String createManifest() {
    Manifest manifest = BeanUtil.toBean(extension.getLauncher(), Manifest.class);
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
        c.from(artifact.getFile()).into(upgradeDir).rename(old -> artifactName);
        // 填充manifest
        Library library = new Library(artifactName, artifact.getFile().length(), Platform.fromString(artifact.getClassifier()));
        manifest.getLibs().add(library);
      });
    }
    // 本项目的Jar
    project.copy(c -> {
      Jar jar = ((Jar) project.getTasks().getByName("jar"));
      String artifactName = jar.getArchiveBaseName().get().concat(".").concat(jar.getArchiveExtension().get());
      c.from(jar.getArchiveFile());
      c.into(upgradeDir);
      c.rename(closure -> artifactName);
      Library library = new Library(artifactName, jar.getArchiveFile().get().getAsFile().length(), null);
      manifest.getLibs().add(library);
    });
    // 生成配置文件
    return FileUtil.writeUtf8String(manifest.toJson(), FileUtil.file(upgradeDir, manifest.getConfigName())).getAbsolutePath();
  }
}
