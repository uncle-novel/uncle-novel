package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract packaging task for Gradle
 */
public abstract class AbstractPackageTask extends DefaultTask {

	private List<File> outputFiles;

	@OutputFiles
	public List<File> getOutputFiles() {
		return outputFiles != null ? outputFiles : new ArrayList<>();
	}

	/**
	 * Task constructor
	 */
	public AbstractPackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("将应用程序打包为本地Windows，Mac OS X或GNULinux可执行文件，并创建安装程序");
		getOutputs().upToDateWhen(o -> false);
	}

	/**
	 * Packaging task action
	 * @throws Exception Throw if something went wrong
	 */
	@TaskAction
	public void doPackage() throws Exception {

		Packager packager = createPackager();

		// generates app, installers and bundles
		File app = packager.createApp();
		List<File> installers = packager.generateInstallers();
		List<File> bundles = packager.createBundles();

		// sets generated files as output
		outputFiles = new ArrayList<>();
		outputFiles.add(app);
		outputFiles.addAll(installers);
		outputFiles.addAll(bundles);

	}

	/**
	 * Creates a platform specific packager
	 * @return Packager
	 * @throws Exception Throw if something went wrong
	 */
	protected abstract Packager createPackager() throws Exception;

}
