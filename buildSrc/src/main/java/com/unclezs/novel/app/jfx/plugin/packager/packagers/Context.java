package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.gradle.GradleContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Building-tool context
 */
public abstract class Context<T> {

    private static Context<?> context;

    // common properties
    private List<ArtifactGenerator> linuxInstallerGenerators = new ArrayList<>();
    private List<ArtifactGenerator> macInstallerGenerators = new ArrayList<>();

    // platform independent functions
    private List<ArtifactGenerator> windowsInstallerGenerators = new ArrayList<>();

    public Context() {
        super();

        // building tool independent generators
        macInstallerGenerators.add(new GenerateDmg());
        macInstallerGenerators.add(new GeneratePkg());
        windowsInstallerGenerators.add(new GenerateSetup());
        windowsInstallerGenerators.add(new GenerateMsm());
        windowsInstallerGenerators.add(new GenerateMsi());

    }

    public static Context<?> getContext() {
        return context;
    }

    public static void setContext(Context<?> context) {
        Context.context = context;
    }

    public static boolean isGradle() {
        return context instanceof GradleContext;
    }

    public static GradleContext getGradleContext() {
        return (GradleContext) context;
    }

    // installer producers

    public abstract File getRootDir();

    public abstract T getLogger();

    public abstract File createRunnableJar(Packager packager) throws Exception;

    public abstract File copyDependencies(Packager packager) throws Exception;

    public abstract File createTarball(Packager packager) throws Exception;

    public abstract File createZipball(Packager packager) throws Exception;

    // static context

    public abstract File resolveLicense(Packager packager) throws Exception;

    public abstract File createWindowsExe(Packager packager) throws Exception;

    public List<ArtifactGenerator> getLinuxInstallerGenerators() {
        return linuxInstallerGenerators;
    }

    public List<ArtifactGenerator> getMacInstallerGenerators() {
        return macInstallerGenerators;
    }

    public List<ArtifactGenerator> getWindowsInstallerGenerators() {
        return windowsInstallerGenerators;
    }

}
