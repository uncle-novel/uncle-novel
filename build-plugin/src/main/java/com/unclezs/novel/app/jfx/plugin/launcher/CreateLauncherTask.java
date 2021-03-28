package com.unclezs.novel.app.jfx.plugin.launcher;

import static com.unclezs.novel.app.jfx.plugin.launcher.LauncherPlugin.GROUP;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.unclezs.novel.app.jfx.launcher.enums.Os;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import lombok.ToString;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskAction;
import org.gradle.jvm.tasks.Jar;

/**
 * @author blog.unclezs.com
 * @since 2021/03/27 17:41
 */
@ToString
public class CreateLauncherTask extends DefaultTask {

  private Manifest manifest;
  private Project project;
  private LauncherExtension options;
  private File launcherJar;

  public CreateLauncherTask() {
    setGroup(GROUP);
    setDescription("打包Launcher");
    this.manifest = new Manifest();
    this.project = getProject();
    this.options = project.getExtensions().getByType(LauncherExtension.class);
  }

  @TaskAction
  public void createLauncher() throws Exception {
    createDependencies();
    createManifest();
    embeddedManifest();
    StringBuilder cmd = new StringBuilder();
    cmd.append("java11 -cp ");
    cmd.append("\"");
    for (Library lib : manifest.getLibs()) {
      if (lib.getPath().contains("javafx")) {
        cmd.append(lib.getPath()).append(";");
      }
    }
    cmd.append("launcher.jar;gson.jar\" ");
    cmd.append("com.unclezs.novel.app.jfx.launcher.Launcher");
    System.out.println(cmd);
  }

  public void createDependencies() {
    project.getLogger().quiet("开始拷贝依赖");
    File workDir = options.getWorkDir();
    //noinspection ResultOfMethodCallIgnored
    workDir.delete();
    manifest.setLibs(new ArrayList<>());
    // 拷贝依赖
    project.getConfigurations().getByName("runtimeClasspath").getResolvedConfiguration()
      .getResolvedArtifacts().forEach(artifact -> {
      if (artifact.getName().contains("app-launcher")) {
        launcherJar = artifact.getFile();
        return;
      }
      if (artifact.getName().contains("javafx")) {
        return;
      }
      project.copy(c -> {
        c.from(artifact.getFile());
        c.into(workDir);
        Library lib = new Library();
        String artifactName;
        if (artifact.getClassifier() != null) {
          String classifier = artifact.getClassifier();
          if (classifier.contains("win")) {
            lib.setOs(Os.WIN);
          } else if (classifier.contains("mac")) {
            lib.setOs(Os.MAC);
          } else if (classifier.contains("linux")) {
            lib.setOs(Os.LINUX);
          }
          artifactName = String.format("%s-%s.%s", artifact.getName(), artifact.getClassifier(), artifact.getExtension());
        } else {
          artifactName = String.format("%s.%s", artifact.getName(), artifact.getExtension());
        }
        c.rename(closure -> artifactName);
        lib.setPath(artifactName);
        lib.setSize(artifact.getFile().length());
        manifest.getLibs().add(lib);
      });
    });
    // 拷贝项目jar包
    project.copy(c -> {
      Provider<RegularFile> jar = ((Jar) project.getTasks().getByName("jar")).getArchiveFile();
      c.from(((Jar) project.getTasks().getByName("jar")).getArchiveFile());
      c.into(workDir);
      Library library = new Library();
      library.setSize(jar.get().getAsFile().length());
      library.setPath(jar.get().getAsFile().getName());
      manifest.getLibs().add(library);
    });
  }

  public void createManifest() throws IOException {
    project.getLogger().quiet("开始创建manifest");
    BeanUtil.copyProperties(options, manifest, CopyOptions.create().ignoreNullValue());
    String manifestJson = Manifest.GSON.toJson(manifest);
    Files.writeString(Paths.get(options.getWorkDir().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME), manifestJson);
  }

  public void embeddedManifest() throws IOException {
    System.out.println(launcherJar);
    project.getLogger().quiet("开始嵌入manifest");
    Path manifestPath = Paths.get(options.getWorkDir().getAbsolutePath(), Manifest.EMBEDDED_CONFIG_NAME);
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(options.getWorkDir(), "launcher.jar")))) {
      zos.putNextEntry(new ZipEntry(Manifest.EMBEDDED_CONFIG_NAME));
      zos.write(Files.readAllBytes(manifestPath));
      ZipFile zipFile = new ZipFile(launcherJar);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry zipEntry = entries.nextElement();
        if (zipEntry.getName().equals(Manifest.EMBEDDED_CONFIG_NAME)) {
          continue;
        }
        zos.putNextEntry(zipEntry);
        zos.write(zipFile.getInputStream(zipEntry).readAllBytes());
      }
      zos.closeEntry();
    }
  }
}
