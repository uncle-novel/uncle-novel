package com.unclezs.novel.app.packager.subtask;

import com.unclezs.novel.app.packager.util.Logger;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.jvm.tasks.Jar;

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
  protected boolean enabled() {
    return Boolean.TRUE.equals(packager.getBundleFxJre()) || Boolean.TRUE.equals(packager.getCopyDependencies());
  }

  @Override
  protected File run() {
    // 拷贝fx的依赖
    if (Boolean.TRUE.equals(packager.getBundleFxJre())) {
      copyOpenJfxDependencies();
    }
    // 拷贝项目依赖
    if (Boolean.TRUE.equals(packager.getCopyDependencies())) {
      copyDependencies();
    }
    return null;
  }

  /**
   * 拷贝项目依赖
   */
  private void copyDependencies() {
    File libsFolder = new File(packager.userLauncher() ? packager.getTmpDir() : packager.getAppFolder(), packager.getLibsFolderName());
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
    // 根据依赖获取fx的版本，如果找不到就使用 11
    String version = "11";
    Optional<ResolvedArtifact> openJfxArtifact = artifacts.stream().filter(artifact -> artifact.getName().contains(OPEN_JFX_SYMBOL)).findFirst();
    if (openJfxArtifact.isPresent()) {
      version = openJfxArtifact.get().getModuleVersion().getId().getVersion();
    }
    // 获取fx的全部模块的依赖
    String[] modules = {"javafx-base", "javafx-controls", "javafx-fxml", "javafx-graphics", "javafx-media", "javafx-swing", "javafx-web"};
    Configuration fx = project.getConfigurations().maybeCreate("openjfx_internal");
    for (String module : modules) {
      fx.getDependencies().add(project.getDependencies().create(String.format("org.openjfx:%s:%s:%s", module, version, packager.getPlatform())));
    }
    // 拷贝依赖
    File fxFolder = new File(packager.getOutputDir(), OPEN_JFX_SYMBOL);
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
    packager.getAdditionalModulePaths().add(fxFolder);
    packager.getAdditionalModules().addAll(Arrays.stream(modules).map(module -> module.replace("-", ".")).collect(Collectors.toList()));
    Logger.info("拷贝OpenJfx依赖完成：{}", fxFolder);
  }
}
