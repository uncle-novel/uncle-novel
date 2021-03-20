package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.utils.FileUtils;
import com.unclezs.novel.app.jfx.plugin.packager.utils.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.utils.VelocityUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Packager for GNU/Linux
 */
public class LinuxPackager extends Packager {

	public LinuxPackager() {
		super();
		installerGenerators.addAll(Context.getContext().getLinuxInstallerGenerators());
	}

	@Override
	public void doInit() throws Exception {

		// sets linux config default values
		this.linuxConfig.setDefaults(this);

	}

	@Override
	protected void doCreateAppStructure() throws Exception {

		// sets common folders
		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;

	}

	/**
	 * Creates a GNU/Linux app folder with native executable
	 */
	@Override
	public File doCreateApp() throws Exception {

		Logger.infoIndent("Creating GNU/Linux executable ...");

		// sets executable file
		this.executable = new File(appFolder, name);

		// process classpath
		if (classpath != null) {
			classpaths = Arrays.asList(classpath.split("[:;]"));
			if (!isUseResourcesAsWorkingDir()) {
				classpaths = classpaths.stream().map(cp -> new File(cp).isAbsolute() ? cp : "$SCRIPTPATH/" + cp).collect(Collectors.toList());
			}
			classpath = StringUtils.join(classpaths, ":");
		}

		// generates startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, this);
		Logger.info("Startup script generated in " + startupFile.getAbsolutePath());

		// concats linux startup.sh script + generated jar in executable (binary)
		FileUtils.concat(executable, startupFile, jarFile);

		// sets execution permissions
		executable.setExecutable(true, false);

		Logger.infoUnindent("GNU/Linux executable created in " + executable.getAbsolutePath() + "!");

		return appFolder;
	}

}
