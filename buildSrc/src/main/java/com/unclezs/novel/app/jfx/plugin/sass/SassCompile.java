package com.unclezs.novel.app.jfx.plugin.sass;

import com.google.gson.Gson;
import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * https://github.com/bit3/jsass
 * https://jsass.readthedocs.io/en/latest/examples.html#compile-file
 *
 * @author Lars Grefer
 */
@Getter
@Setter
@NonNullApi
public class SassCompile extends ConventionTask {
    public static final String CHARSET = "@charset \"UTF-8\";";

    @InputFiles
    protected FileTree getSourceFiles() {
        ConfigurableFileTree files = getProject().fileTree(new File(sourceDir, sassPath));
        files.include("**/*.scss");
        files.include("**/*.sass");
        return files;
    }

    @OutputFiles
    protected FileTree getOutputFiles() {
        ConfigurableFileTree files = getProject().fileTree(new File(destinationDir, cssPath));
        files.include("**/*.css");
        return files;
    }

    @Internal
    private File sourceDir;

    @Internal
    private File destinationDir;

    @Internal
    private String cssPath = "";

    @Internal
    private String sassPath = "";

    @TaskAction
    public void compileSass() {
        Compiler compiler = new Compiler();
        Options options = new Options();
        File realSourceDir = new File(sourceDir, sassPath);
        File fakeDestinationDir = new File(sourceDir, cssPath);
        File realDestinationDir = new File(getDestinationDir(), cssPath);
        getProject().fileTree(realSourceDir).visit(new FileVisitor() {
            @Override
            public void visitDir(FileVisitDetails fileVisitDetails) {

            }

            @Override
            public void visitFile(FileVisitDetails fileVisitDetails) {
                String name = fileVisitDetails.getName();
                if (name.startsWith("_")) {
                    return;
                }
                if (name.endsWith(".scss") || name.endsWith(".sass")) {
                    File in = fileVisitDetails.getFile();
                    String pathString = fileVisitDetails.getRelativePath().getPathString();
                    pathString = pathString.substring(0, pathString.length() - 5) + ".css";
                    File realOut = new File(realDestinationDir, pathString);
                    File fakeOut = new File(fakeDestinationDir, pathString);
                    options.setIsIndentedSyntaxSrc(name.endsWith(".sass"));
                    options.setSourceMapFile(null);
                    try {
                        URI inputPath = in.getAbsoluteFile().toURI();
                        Output output = compiler.compileFile(inputPath, fakeOut.toURI(), options);
                        if (realOut.getParentFile().exists() || realOut.getParentFile().mkdirs()) {
                            ResourceGroovyMethods.write(realOut, output.getCss().replace(CHARSET, "").trim());
                        }
                    } catch (CompilationException e) {
                        SassError sassError = new Gson().fromJson(e.getErrorJson(), SassError.class);
                        getLogger().error("{}:{}:{}", sassError.getFile(), sassError.getLine(), sassError.getColumn());
                        getLogger().error(e.getErrorMessage());
                        throw new TaskExecutionException(SassCompile.this, e);
                    } catch (IOException e) {
                        getLogger().error(e.getLocalizedMessage());
                        throw new TaskExecutionException(SassCompile.this, e);
                    }
                }
            }
        });
    }
}
