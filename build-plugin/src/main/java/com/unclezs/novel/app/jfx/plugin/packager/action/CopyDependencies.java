package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.jfx.launcher.enums.Os;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import java.io.File;
import org.gradle.api.Project;

/**
 * 拷贝依赖
 *
 * @author blog.unclezs.com
 * @since 2021/03/27 17:41
 */
public class CopyDependencies extends ArtifactGenerator {

  public CopyDependencies() {
    super("Dependencies");
  }

  @Override
  protected File doApply(Packager packager) {
    File libsFolder = new File(packager.getJarFileDestinationFolder(), packager.getLibsFolderName());
    Project project = Context.getProject();
    FileUtil.del(libsFolder);
    project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration().getResolvedArtifacts().forEach(artifact -> {
      project.copy(c -> {
        c.from(artifact.getFile());
        c.into(project.file(libsFolder));
        String artifactName;
        Os os = null;
        if (artifact.getClassifier() != null) {
          String classifier = artifact.getClassifier();
          if (StrUtil.containsIgnoreCase(classifier, Os.WIN.name())) {
            os = Os.WIN;
          } else if (StrUtil.containsIgnoreCase(classifier, Os.MAC.name())) {
            os = Os.MAC;
          } else if (StrUtil.containsIgnoreCase(classifier, Os.LINUX.name())) {
            os = Os.LINUX;
          }
          artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
        } else {
          artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
        }
        c.rename(closure -> artifactName);
        // 如果启用 launcher
        if (packager.userLauncher() && !StrUtil.containsAny(artifactName, packager.getLauncher().getRunTimeLibrary())) {
          Library lib = new Library();
          lib.setPath(artifactName);
          lib.setSize(artifact.getFile().length());
          lib.setOs(os);
          packager.getLauncher().getLibs().add(lib);
        }
      });
    });
    return libsFolder;
  }
}
