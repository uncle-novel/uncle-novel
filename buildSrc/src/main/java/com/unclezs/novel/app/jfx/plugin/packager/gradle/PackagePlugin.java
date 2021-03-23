package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * JavaPackager Gradle plugin
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@NonNullApi
public class PackagePlugin implements Plugin<Project> {

	public static final String GROUP_NAME = "packager";
	public static final String SETTINGS_EXT_NAME = "packager";
	public static final String PACKAGE_TASK_NAME = "package";

	@Override
	public void apply(Project project) {
        project.getPluginManager().apply("edu.sc.seis.launch4j");
		Context.setContext(new GradleContext(project));
		project.getExtensions().create(SETTINGS_EXT_NAME, PackagePluginExtension.class, project);
        project.getTasks().create(PACKAGE_TASK_NAME, DefaultPackageTask.class).dependsOn("build");
	}
}
