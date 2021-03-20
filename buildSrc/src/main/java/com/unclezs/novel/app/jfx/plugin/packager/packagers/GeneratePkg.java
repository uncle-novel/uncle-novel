package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.utils.CommandUtils;

import java.io.File;

/**
 * Creates a PKG installer file including all app folder's content only for MacOS so
 * app could be easily distributed
 */
public class GeneratePkg extends ArtifactGenerator {

	public GeneratePkg() {
		super("PKG installer");
	}

	@Override
	public boolean skip(Packager packager) {
		return !packager.getMacConfig().isGeneratePkg();
	}

	@Override
	protected File doApply(Packager packager) throws Exception {
		MacPackager macPackager = (MacPackager) packager;

		File appFile = macPackager.getAppFile();
		String name = macPackager.getName();
		File outputDirectory = macPackager.getOutputDirectory();
		String version = macPackager.getVersion();

		File pkgFile = new File(outputDirectory, name + "_" + version + ".pkg");

		// invokes pkgbuild command
		CommandUtils.execute("pkgbuild", "--install-location", "/Applications", "--component", appFile, pkgFile);

		// checks if pkg file was created
		if (!pkgFile.exists()) {
			throw new Exception(getArtifactName() + " generation failed!");
		}

		return pkgFile;
	}

}
