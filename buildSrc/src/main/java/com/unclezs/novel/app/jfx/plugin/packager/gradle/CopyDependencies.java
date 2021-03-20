package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.ArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

import java.io.File;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CopyDependencies extends ArtifactGenerator {

	public Copy copyLibsTask;

	public CopyDependencies() {
		super("Dependencies");
	}

	@Override
	protected File doApply(Packager packager) {

		File libsFolder = new File(packager.getJarFileDestinationFolder(), "runtimes/lib");
		Project project = Context.getGradleContext().getProject();

		copyLibsTask = (Copy) project.getTasks().findByName("copyLibs");
		if (copyLibsTask == null) {
			copyLibsTask = project.getTasks().create("copyLibs", Copy.class);
		}
		copyLibsTask.from(project.getConfigurations().getByName("default"));
		copyLibsTask.into(project.file(libsFolder));
		copyLibsTask.getActions().forEach(action -> action.execute(copyLibsTask));

		return libsFolder;
	}

}
