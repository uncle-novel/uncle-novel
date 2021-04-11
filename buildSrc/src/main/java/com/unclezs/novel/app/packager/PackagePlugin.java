package com.unclezs.novel.app.packager;

import com.unclezs.novel.app.packager.model.PackagerExtension;
import com.unclezs.novel.app.packager.task.UpgradeTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Java 打平台包插件
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@NonNullApi
public class PackagePlugin implements Plugin<Project> {

  public static final String GROUP_NAME = "packager";
  public static final String EXTENSION_NAME = "packager";

  @Override
  public void apply(Project project) {
    Context.project = project;
    project.getExtensions().create(GROUP_NAME, PackagerExtension.class, project);
    project.getTasks().create("upgrade", UpgradeTask.class).dependsOn("build");
  }
}
