package com.unclezs.novel.app.jfx.packager.action;

import com.unclezs.novel.app.jfx.packager.subtask.BaseSubTask;
import java.io.File;

/**
 * 拷贝openJfx依赖
 *
 * @author blog.unclezs.com
 * @since 2021/03/27 17:41
 */
public class CopyFxDependencies extends BaseSubTask {

  public static final String OPEN_JFX_SYMBOL = "javafx";

  public CopyFxDependencies() {
    super("拷贝openJfx依赖");
  }

  @Override
  protected boolean enabled() {
    return Boolean.TRUE.equals(packager.getBundleFxJre());
  }

  @Override
  protected File run() {
    File fxFolder = new File(packager.getOutputDir(), "openJfx");
    project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getResolvedArtifacts().stream()
      .filter(artifact -> artifact.getName().contains(OPEN_JFX_SYMBOL) && artifact.getClassifier() != null).forEach(artifact -> project.copy(c -> {
      String artifactName;
      if (artifact.getClassifier() != null) {
        artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
      } else {
        artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
      }
      // 拷贝
      c.rename(closure -> artifactName);
      c.from(artifact.getFile());
      c.into(project.file(fxFolder));
    }));
    packager.getExtModulePath().add(fxFolder.getAbsolutePath());
    return fxFolder;
  }
}
