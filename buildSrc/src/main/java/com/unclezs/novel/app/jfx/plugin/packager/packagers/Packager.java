package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.utils.FileUtils;
import com.unclezs.novel.app.jfx.plugin.packager.utils.IconUtils;
import com.unclezs.novel.app.jfx.plugin.packager.utils.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.utils.Platform;
import com.unclezs.novel.app.jfx.plugin.packager.utils.VelocityUtils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * Packager base class
 */
public abstract class Packager extends PackagerSettings {

    private static final String DEFAULT_ORGANIZATION_NAME = "ACME";

    // artifact generators
    protected List<ArtifactGenerator> installerGenerators = new ArrayList<>();
    private final BundleJre generateJre = new BundleJre();

    // internal generic properties (setted in "createAppStructure/createApp")
    protected File appFolder;
    protected File assetsFolder;
    protected File executable;
    protected File jarFile;
    protected File libsFolder;

    // internal specific properties (setted in "doCreateAppStructure")
    protected File executableDestinationFolder;
    protected File jarFileDestinationFolder;
    protected File jreDestinationFolder;
    protected File resourcesDestinationFolder;

    // processed classpaths list
    protected List<String> classpaths = new ArrayList<>();

    // ===============================================

    public File getAppFolder() {
        return appFolder;
    }

    public File getAssetsFolder() {
        return assetsFolder;
    }

    public File getExecutable() {
        return executable;
    }

    public File getJarFile() {
        return jarFile;
    }

    public File getJarFileDestinationFolder() {
        return jarFileDestinationFolder;
    }

    public File getLibsFolder() {
        return libsFolder;
    }

    public List<String> getClasspaths() {
        return classpaths;
    }

    public File getJreDestinationFolder() {
        return jreDestinationFolder;
    }

    // ===============================================

    public Packager() {
        super();
        Logger.info("Using packager " + this.getClass().getName());
    }

    private void init() throws Exception {

        Logger.infoIndent("Initializing packager ...");

        if (mainClass == null || mainClass.isEmpty()) {
            throw new Exception("'mainClass' cannot be null");
        }

        // sets assetsDir for velocity to locate custom velocity templates
        VelocityUtils.setAssetsDir(assetsDir);

        // using name as displayName, if it's not specified
        displayName = defaultIfBlank(displayName, name);

        // using displayName as description, if it's not specified
        description = defaultIfBlank(description, displayName);

        // using "ACME" as organizationName, if it's not specified
        organizationName = defaultIfBlank(organizationName, DEFAULT_ORGANIZATION_NAME);

        // using empty string as organizationUrl, if it's not specified
        organizationUrl = defaultIfBlank(organizationUrl, "");

        // determines target platform if not specified
        if (platform == null || platform == Platform.auto) {
            platform = Platform.getCurrentPlatform();
        }

        // sets jdkPath by default if not specified
        if (jdkPath == null) {
            jdkPath = new File(System.getProperty("java.home"));
        }
        if (!jdkPath.exists()) {
            throw new Exception("JDK path doesn't exist: " + jdkPath);
        }

        // check if name is valid as filename
        try {
            Paths.get(name);
            if (name.contains("/")) throw new InvalidPathException(name, "Illegal char </>");
            if (name.contains("\\")) throw new InvalidPathException(name, "Illegal char <\\>");
        } catch (InvalidPathException e) {
            throw new Exception("Invalid name specified: " + name, e);
        }

        // init setup languages
        if (platform.equals(Platform.windows) && (winConfig.getSetupLanguages() == null || winConfig.getSetupLanguages().isEmpty())) {
            winConfig.getSetupLanguages().put("english", "compiler:Default.isl");
            winConfig.getSetupLanguages().put("spanish", "compiler:Languages\\Spanish.isl");
        }

        doInit();

        // removes not necessary platform specific configs
        switch (platform) {
            case linux:
                macConfig = null;
                winConfig = null;
                break;
            case mac:
                winConfig = null;
                linuxConfig = null;
                break;
            case windows:
                linuxConfig = null;
                macConfig = null;
                break;
            default:
        }

        Logger.info("" + this); // prints packager settings

        Logger.infoUnindent("Packager initialized!");

    }

    public void resolveResources() throws Exception {

        Logger.infoIndent("Resolving resources ...");

        // locates license file
        licenseFile = resolveLicense(licenseFile);

        // locates icon file
        iconFile = resolveIcon(iconFile, name, assetsFolder);

        // adds to additional resources
        if (additionalResources != null) {
            if (licenseFile != null) {
                additionalResources.add(licenseFile);
            }
            additionalResources.add(iconFile);
            Logger.info("Effective additional resources " + additionalResources);
        }

        Logger.infoUnindent("Resources resolved!");

    }

    /**
     * Copy a list of resources to a folder
     *
     * @param resources   List of files and folders to be copied
     * @param destination Destination folder. All specified resources will be copied
     *                    here
     */
    protected void copyAdditionalResources(List<File> resources, File destination) {

        Logger.infoIndent("Copying additional resources");

        resources.forEach(r -> {
            if (!r.exists()) {
                Logger.warn("Additional resource " + r + " doesn't exist");
                return;
            }
            try {
                if (r.isDirectory()) {
                    FileUtils.copyFolderToFolder(r, destination);
                } else if (r.isFile()) {
                    FileUtils.copyFileToFolder(r, destination);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Logger.infoUnindent("All additional resources copied!");

    }

    /**
     * Locates license file
     *
     * @param licenseFile Specified license file
     * @return Resolved license file
     */
    protected File resolveLicense(File licenseFile) {

        // if default license file doesn't exist
        if (licenseFile != null && !licenseFile.exists()) {
            Logger.warn("Specified license file doesn't exist: " + licenseFile.getAbsolutePath());
            licenseFile = null;
        }

        // invokes custom license resolver if exists
        if (licenseFile == null) {
            try {
                licenseFile = Context.getContext().resolveLicense(this);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }

        // if license is still null, looks for LICENSE file
        if (licenseFile == null || !licenseFile.exists()) {
            licenseFile = new File(Context.getContext().getRootDir(), "LICENSE");
            if (!licenseFile.exists()) licenseFile = null;
        }

        if (licenseFile != null) {
            Logger.info("License file found: " + licenseFile.getAbsolutePath());
        } else {
            Logger.warn("No license file specified");
        }

        return licenseFile;
    }

    /**
     * Locates assets or default icon file if the specified one doesn't exist or isn't specified
     *
     * @param iconFile     Specified icon file
     * @param name         Name
     * @param assetsFolder Assets folder
     * @return Resolved icon file
     * @throws Exception Process failed
     */
    protected File resolveIcon(File iconFile, String name, File assetsFolder) throws Exception {

        // search for specific icons
        switch (platform) {
            case linux:
                iconFile = FileUtils.exists(linuxConfig.getPngFile()) ? linuxConfig.getPngFile() : null;
                break;
            case mac:
                iconFile = FileUtils.exists(macConfig.getIcnsFile()) ? macConfig.getIcnsFile() : null;
                break;
            case windows:
                iconFile = FileUtils.exists(winConfig.getIcoFile()) ? winConfig.getIcoFile() : null;
                break;
            default:
        }

        String iconExtension = IconUtils.getIconFileExtensionByPlatform(platform);
        if (iconFile != null) {
            //noinspection ResultOfMethodCallIgnored
            iconFile.renameTo(new File(assetsFolder, iconFile.getName()));
        } else {
            // if not specific icon specified for target platform, search for an icon in "${assetsDir}" folder
            iconFile = new File(assetsDir, name + iconExtension);
        }

        // if there's no icon yet, uses default one
        if (!iconFile.exists()) {
            iconFile = new File(assetsFolder, iconFile.getName());
            FileUtils.copyResourceToFile("/" + platform + "/default-icon" + iconExtension, iconFile);
        }

        Logger.info("Icon file resolved: " + iconFile.getAbsolutePath());

        return iconFile;
    }

    /**
     * Bundling app folder in tarball and/or zipball
     *
     * @return Generated bundles
     * @throws Exception Process failed
     */
    public List<File> createBundles() throws Exception {

        List<File> bundles = new ArrayList<>();

        Logger.infoIndent("Creating bundles ...");

        if (createZipball) {
            File zipball = Context.getContext().createZipball(this);
            Logger.info("Zipball created: " + zipball);
            bundles.add(zipball);
        }

        if (createTarball) {
            File tarball = Context.getContext().createTarball(this);
            Logger.info("Tarball created: " + tarball);
            bundles.add(tarball);
        }

        Logger.infoUnindent("Bundles created!");

        return bundles;
    }

    private void createAppStructure() throws Exception {

        Logger.infoIndent("Creating app structure ...");

        // creates output directory if it doesn't exist
        if (!outputDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputDirectory.mkdirs();
        }

        // creates app destination folder
        appFolder = new File(outputDirectory, name);
        if (appFolder.exists()) {
            FileUtils.removeFolder(appFolder);
            Logger.info("Old app folder removed " + appFolder.getAbsolutePath());
        }
        appFolder = FileUtils.mkdir(outputDirectory, name);
        Logger.info("App folder created: " + appFolder.getAbsolutePath());

        // creates folder for intermediate assets
        assetsFolder = FileUtils.mkdir(outputDirectory, "assets");
        Logger.info("Assets folder created: " + assetsFolder.getAbsolutePath());

        // create the rest of the structure
        doCreateAppStructure();

        Logger.infoUnindent("App structure created!");

    }

    public File createApp() throws Exception {

        Logger.infoIndent("Creating app ...");

        init();

        // creates app folders structure
        createAppStructure();

        // resolve resources
        resolveResources();

        // copies additional resources
        copyAdditionalResources(additionalResources, resourcesDestinationFolder);

        // copies all dependencies to Java folder
        Logger.infoIndent("Copying all dependencies ...");
        libsFolder = copyDependencies ? Context.getContext().copyDependencies(this) : null;
        Logger.infoUnindent("Dependencies copied to " + libsFolder + "!");

        // creates a runnable jar file
        if (runnableJar != null && runnableJar.exists()) {
            Logger.info("Using runnable JAR: " + runnableJar);
            jarFile = runnableJar;
        } else {
            Logger.infoIndent("Creating runnable JAR...");
            jarFile = Context.getContext().createRunnableJar(this);
            Logger.infoUnindent("Runnable jar created in " + jarFile + "!");
        }

        // embeds a JRE if is required
        generateJre.apply(this);

        File appFile = doCreateApp();

        Logger.infoUnindent("App created in " + appFolder.getAbsolutePath() + "!");

        return appFile;
    }

    public List<File> generateInstallers() throws Exception {
        List<File> installers = new ArrayList<>();

        if (!generateInstaller) {
            Logger.warn("Installer generation is disabled by 'generateInstaller' property!");
            return installers;
        }
        if (!platform.isCurrentPlatform()) {
            Logger.warn("Installers cannot be generated due to the target platform (" + platform + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
            return installers;
        }

        Logger.infoIndent("Generating installers ...");

        init();

        // creates folder for intermmediate assets if it doesn't exist
        assetsFolder = FileUtils.mkdir(outputDirectory, "assets");

        // invokes installer producers

        for (ArtifactGenerator generator : installerGenerators) {
            try {
                Logger.infoIndent("Generating " + generator.getArtifactName() + "...");
                File artifact = generator.apply(this);
                if (artifact != null) {
                    addIgnoreNull(installers, artifact);
                    Logger.infoUnindent(generator.getArtifactName() + " generated in " + artifact + "!");
                } else {
                    Logger.warnUnindent(generator.getArtifactName() + " NOT generated!!!");
                }

            } catch (Exception e) {
                Logger.errorUnindent(generator.getArtifactName() + " generation failed due to: " + e.getMessage(), e);
            }
        }

        Logger.infoUnindent("Installers generated! " + installers);

        return installers;
    }

    protected abstract void doCreateAppStructure() throws Exception;

    public abstract File doCreateApp() throws Exception;

    public abstract void doInit() throws Exception;

}
