package com.unclezs.novel.app.jfx.plugin.sass;

import lombok.Getter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;

import java.io.File;
import java.util.Set;

/**
 * @author Lars Grefer
 */
@Getter
public class SassCompileJavaPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        SassExtension jSass = project.getExtensions().create("sass", SassExtension.class);
        project.afterEvaluate(p -> {
            project.getPlugins().apply(JavaPlugin.class);
            project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(sourceSet -> {
                String taskName = sourceSet.getTaskName("compile", "Sass");
                Set<File> srcDirs = sourceSet.getResources().getSrcDirs();
                int i = 1;
                for (File srcDir : srcDirs) {
                    SassCompile sassCompile = project.getTasks().create(i == 1 ? taskName : taskName + i, SassCompile.class);
                    i++;
                    sassCompile.setGroup(BasePlugin.BUILD_GROUP);
                    sassCompile.setDescription("Compile sass and scss files for the " + sourceSet.getName() + " source set");
                    Copy processResources = (Copy) project.getTasks().getByName(sourceSet.getProcessResourcesTaskName());
                    sassCompile.setSourceDir(srcDir);
                    sassCompile.setDestinationDir(jSass.isInplace() ? srcDir : processResources.getDestinationDir());
                    sassCompile.setSassPath(jSass.getSassPath());
                    sassCompile.setCssPath(jSass.getCssPath());
                    processResources.dependsOn(sassCompile);
                }
            });
        });
    }
}
