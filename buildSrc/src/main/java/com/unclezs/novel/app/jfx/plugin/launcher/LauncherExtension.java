package com.unclezs.novel.app.jfx.plugin.launcher;

import lombok.Data;
import org.gradle.api.Project;

import java.io.File;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 10:46
 */
@Data
public class LauncherExtension {
    private File workDir;
    private String nativeLibPath;

    private final Project project;

    public LauncherExtension(Project project) {
        this.project = project;
    }

    public File getWorkDir() {
        return workDir == null ? new File(String.format("%s/app", project.getProject().getBuildDir())) : workDir;
    }
}
