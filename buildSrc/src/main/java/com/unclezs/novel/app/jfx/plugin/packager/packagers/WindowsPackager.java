package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.VelocityUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Packager for Windows
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @date 2021/03/24 0:06
 */
public class WindowsPackager extends Packager {

    private File manifestFile;
    private File msmFile;

    public WindowsPackager() {
        super();
        installerGenerators.addAll(Context.getContext().getWindowsInstallerGenerators());
    }

    public File getManifestFile() {
        return manifestFile;
    }

    public File getMsmFile() {
        return msmFile;
    }

    public void setMsmFile(File msmFile) {
        this.msmFile = msmFile;
    }

    @Override
    public void doInit() throws Exception {
        this.winConfig.setDefaults(this);
    }

    @Override
    protected void doCreateAppStructure() {
        // sets common folders
        this.executableDestinationFolder = appFolder;
        this.jarFileDestinationFolder = appFolder;
        this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
        this.resourcesDestinationFolder = appFolder;
    }

    /**
     * Creates a Windows app file structure with native executable
     */
    @Override
    public File doCreateApp() throws Exception {

        Logger.infoIndent("Creating windows EXE ...");

        // copies JAR to app folder
        if (!winConfig.isWrapJar()) {
            FileUtil.copy(jarFile, libsFolder, true);
        }

        // generates manifest file to require administrator privileges from velocity template
        manifestFile = new File(assetsFolder, name + ".exe.manifest");
        VelocityUtils.render("windows/exe.manifest.vm", manifestFile, this);
        Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

        // sets executable file
        executable = new File(appFolder, name + ".exe");

        // process classpath
        if (classpath != null) {
            classpathList = Arrays.asList(classpath.split(";"));
            if (!isUseResourcesAsWorkingDir()) {
                classpathList = classpathList.stream().map(cp -> new File(cp).isAbsolute() ? cp : "%EXEDIR%/" + cp).collect(Collectors.toList());
            }
            classpath = StringUtils.join(classpathList, ";");
        }

        // invokes launch4j to generate windows executable
        executable = Context.getContext().createWindowsExe(this);

        Logger.infoUnindent("Windows EXE file created in " + executable + "!");

        return appFolder;
    }

}
