package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.io.File;

/**
 * Gradle context
 */
public class GradleContext extends Context<Logger> {

    private Project project;

    public GradleContext(Project project) {
        super();
        this.project = project;

        // gradle dependant generators
        this.getLinuxInstallerGenerators().add(new GenerateDeb());
        this.getLinuxInstallerGenerators().add(new GenerateRpm());

    }

    @Override
    public Logger getLogger() {
        return project.getLogger();
    }

    public Project getProject() {
        return project;
    }

    @Override
    public File getRootDir() {
        return project.getRootDir();
    }

    @Override
    public File createRunnableJar(Packager packager) throws Exception {
        return new CreateRunnableJar().apply(packager);
    }

    @Override
    public File copyDependencies(Packager packager) throws Exception {
        return new CopyDependencies().apply(packager);
    }

    @Override
    public File createTarball(Packager packager) throws Exception {
        return new CreateTarball().apply(packager);
    }

    @Override
    public File createZipball(Packager packager) throws Exception {
        return new CreateZipball().apply(packager);
    }

    @Override
    public File resolveLicense(Packager packager) throws Exception {
        // do nothing
        return null;
    }

    @Override
    public File createWindowsExe(Packager packager) throws Exception {
        return new CreateWindowsExe().apply(packager);
    }

}
