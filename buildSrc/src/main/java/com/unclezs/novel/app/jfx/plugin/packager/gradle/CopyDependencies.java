package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.ArtifactGenerator;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Context;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;
import org.gradle.jvm.tasks.Jar;

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

        File libsFolder = new File(packager.getJarFileDestinationFolder(), "libx");
        Project project = Context.getGradleContext().getProject();
        FileUtil.del(libsFolder);
        project.copy(c -> {
            c.from(project.getConfigurations().getByName("runtimeClasspath"));
            c.into(project.file(libsFolder));
        });
        // 拷贝项目jar包
        project.copy(c -> {
            c.from(((Jar) project.getTasks().getByName("jar")).getArchiveFile());
            c.into(libsFolder);
        });
        return libsFolder;
    }

}
