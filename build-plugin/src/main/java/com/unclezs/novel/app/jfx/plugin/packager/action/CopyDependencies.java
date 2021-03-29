package com.unclezs.novel.app.jfx.plugin.packager.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.jfx.launcher.Library;
import com.unclezs.jfx.launcher.Os;
import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.model.LauncherConfig;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

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
        String artifactName;
        Os os = null;
        if (artifact.getClassifier() != null) {
          artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
        } else {
          artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
        }
        // 如果启用Launcher 记录launcher相关依赖
        if (packager.userLauncher()) {
          LauncherConfig launcher = packager.getLauncher();
          if (!StrUtil.containsAny(artifactName, launcher.getRunTimeLibrary()) && !artifactName.contains(launcher.getLauncherJarLibName())) {
            String classifier = artifact.getClassifier();
            if (StrUtil.containsIgnoreCase(classifier, Os.WIN.name())) {
              os = Os.WIN;
            } else if (StrUtil.containsIgnoreCase(classifier, Os.MAC.name())) {
              os = Os.MAC;
            } else if (StrUtil.containsIgnoreCase(classifier, Os.LINUX.name())) {
              os = Os.LINUX;
            }
            packager.getLauncher().getLibs().add(new Library(artifactName, artifact.getFile().length(), os));
          } else {
            // 设置启动类的classPath
            launcher.getClasspath().add(libsFolder.getName().concat("/").concat(artifactName));
          }
        }
        // 拷贝
        c.rename(closure -> artifactName);
        c.from(artifact.getFile());
        c.into(project.file(libsFolder));
      });
    });
    // 启动器需要拷贝本项目的Jar
    if (packager.userLauncher()) {
      project.copy(c -> {
        Jar jar = ((Jar) project.getTasks().getByName("jar"));
        String artifactName = jar.getArchiveBaseName().get().concat(".").concat(jar.getArchiveExtension().get());
        c.from(jar.getArchiveFile());
        c.into(project.file(libsFolder));
        c.rename(closure -> artifactName);
        packager.getLauncher().getLibs().add(new Library(artifactName, jar.getArchiveFile().get().getAsFile().length(), null));
      });
    }
    return libsFolder;
  }
}
