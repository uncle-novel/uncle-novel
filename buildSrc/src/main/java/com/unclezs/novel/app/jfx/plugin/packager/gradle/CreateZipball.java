package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.ArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.MacPackager;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;
import java.util.UUID;

/**
 * Creates zipball (zip file)  on Gradle context
 */
public class CreateZipball extends ArtifactGenerator {

    public CreateZipball() {
        super("Zipball");
    }

    @Override
    public boolean skip(Packager packager) {
        return !packager.getCreateZipball();
    }

    @Override
    protected File doApply(Packager packager) throws Exception {
        String name = packager.getName();
        String version = packager.getVersion();
        Platform platform = packager.getPlatform();
        File outputDirectory = packager.getOutputDirectory();
        File appFolder = packager.getAppFolder();
        File executable = packager.getExecutable();
        String jreDirectoryName = packager.getJreDirectoryName();

        File zipFile = new File(outputDirectory, name + "-" + version + "-" + platform + ".zip");

        Zip zipTask = createZipTask();
        zipTask.setProperty("archiveFileName", zipFile.getName());
        zipTask.setProperty("destinationDirectory", outputDirectory);

        // if zipball is for windows platform
        if (Platform.windows.equals(platform)) {

            zipTask.from(appFolder.getParentFile(), copySpec -> {
                copySpec.include(appFolder.getName() + "/**");
            });

        }

        // if zipball is for linux platform
        else if (Platform.linux.equals(platform)) {

            zipTask.from(appFolder.getParentFile(), copySpec -> {
                copySpec.include(appFolder.getName() + "/**");
                copySpec.exclude(appFolder.getName() + "/" + executable.getName());
                copySpec.exclude(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
            });
            zipTask.from(appFolder.getParentFile(), copySpec -> {
                copySpec.include(appFolder.getName() + "/" + executable.getName());
                copySpec.include(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
                copySpec.setFileMode(0755);
            });

        }

        // if zipball is for macos platform
        else if (Platform.mac.equals(platform)) {

            MacPackager macPackager = (MacPackager) packager;
            File appFile = macPackager.getAppFile();

            zipTask.from(appFolder, copySpec -> {
                copySpec.include(appFile.getName() + "/**");
                copySpec.exclude(appFile.getName() + "/Contents/MacOS/" + executable.getName());
                copySpec.exclude(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
                copySpec.exclude(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");

            });
            zipTask.from(appFolder, copySpec -> {
                copySpec.include(appFile.getName() + "/Contents/MacOS/" + executable.getName());
                copySpec.include(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
                copySpec.include(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
                copySpec.setFileMode(0755);
            });

        }

        zipTask.getActions().forEach(action -> action.execute(zipTask));

        return zipFile;
    }

    private Zip createZipTask() {
        return Context.getGradleContext().getProject().getTasks().create("createZipball_" + UUID.randomUUID(), Zip.class);
    }

}
