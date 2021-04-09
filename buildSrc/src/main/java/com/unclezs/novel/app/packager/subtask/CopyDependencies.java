package com.unclezs.novel.app.packager.subtask;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.Logger;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 拷贝依赖
 *
 * @author blog.unclezs.com
 * @since 2021/03/27 17:41
 */
public class CopyDependencies extends BaseSubTask {

  public static final String OPEN_JFX_SYMBOL = "javafx";
  private final Set<ResolvedArtifact> artifacts;

  public CopyDependencies() {
    super("拷贝依赖");
    this.artifacts = project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getResolvedArtifacts();
  }

  @Override
  protected File run() {
    // 拷贝fx的依赖
    if (Boolean.TRUE.equals(packager.getBundleFxJre())) {
      copyOpenJfxDependencies();
    }
    // 拷贝项目依赖
    copyDependencies();
    return null;
  }

  /**
   * 拷贝项目依赖
   */
  private void copyDependencies() {
    File libsFolder = new File(packager.userLauncher() ? packager.getTmpDir() : packager.getAppFolder(), packager.getLibsFolderPath());
    // 项目依赖
    artifacts.stream()
      .filter(artifact -> !artifact.getName().contains(OPEN_JFX_SYMBOL))
      .forEach(artifact -> project.copy(c -> {
        String artifactName;
        if (artifact.getClassifier() != null) {
          artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
        } else {
          artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
        }
        // 拷贝
        c.from(artifact.getFile()).rename(closure -> artifactName).into(project.file(libsFolder));
      }));
    // 如果是使用了Launcher，那么本项目的Jar也当作依赖处理
    if (packager.userLauncher()) {
      // 本项目的Jar
      project.copy(c -> {
        Jar jar = ((Jar) project.getTasks().getByName("jar"));
        c.from(jar.getArchiveFile()).into(libsFolder).rename(closure -> jar.getArchiveBaseName().get().concat(".").concat(jar.getArchiveExtension().get()));
      });
    }
    packager.setLibsFolder(libsFolder);
    Logger.info("拷贝项目依赖完成：{}", libsFolder);
  }

  /**
   * 拷贝openJfx的依赖
   */
  private void copyOpenJfxDependencies() {
    // 获取fx的全部模块的依赖
    String[] modules = {"javafx-base", "javafx-controls", "javafx-fxml", "javafx-graphics", "javafx-media", "javafx-swing", "javafx-web"};
    File fxFolder = new File(packager.getOutputDir(), OPEN_JFX_SYMBOL);
    FileUtils.del(fxFolder);
    // 是否自定义jfx
    if (FileUtil.exist(packager.getJfxPath())) {
      FileUtil.copyContent(packager.getJfxPath(), fxFolder, true);
    } else {
      // 根据依赖获取fx的版本，如果找不到就使用 11
      String version = "11";
      Optional<ResolvedArtifact> openJfxArtifact = artifacts.stream().filter(artifact -> artifact.getName().contains(OPEN_JFX_SYMBOL)).findFirst();
      if (openJfxArtifact.isPresent()) {
        version = openJfxArtifact.get().getModuleVersion().getId().getVersion();
      }
      Configuration fx = project.getConfigurations().maybeCreate("openjfx_internal");
      for (String module : modules) {
        fx.getDependencies().add(project.getDependencies().create(String.format("org.openjfx:%s:%s:%s", module, version, packager.getPlatform())));
      }
      // 拷贝依赖
      fx.getResolvedConfiguration().getResolvedArtifacts().forEach(artifact -> {
        if (artifact.getClassifier() == null) {
          return;
        }
        project.copy(c -> {
          c.rename(closure -> String.format("%s.%s", artifact.getName(), artifact.getExtension()));
          c.from(artifact.getFile());
          c.into(project.file(fxFolder));
        });
      });
    }
    packager.getAdditionalModules().addAll(Arrays.stream(modules).map(module -> module.replace("-", ".")).collect(Collectors.toList()));
    packager.getAdditionalModulePaths().add(fxFolder);
    Logger.info("拷贝OpenJfx依赖完成：{}", fxFolder);
  }
}
