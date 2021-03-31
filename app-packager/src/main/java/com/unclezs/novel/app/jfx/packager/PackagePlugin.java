package com.unclezs.novel.app.jfx.packager;

import com.unclezs.novel.app.jfx.packager.task.DefaultPackageTask;
import com.unclezs.novel.app.jfx.packager.task.DeployAppTask;
import com.unclezs.novel.app.jfx.packager.task.PackagePluginExtension;
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
  public static final String PACKAGE_TASK_NAME = "package";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply("edu.sc.seis.launch4j");
    Context.init(project);
    project.getExtensions().create(EXTENSION_NAME, PackagePluginExtension.class, project);
    project.getTasks().create(PACKAGE_TASK_NAME, DefaultPackageTask.class).dependsOn("build");
    project.getTasks().create("deploy", DeployAppTask.class).dependsOn("build");
  }
}
