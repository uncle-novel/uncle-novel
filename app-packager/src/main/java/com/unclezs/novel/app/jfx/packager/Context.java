package com.unclezs.novel.app.jfx.packager;

import com.unclezs.novel.app.jfx.packager.action.ArtifactGenerator;
import com.unclezs.novel.app.jfx.packager.action.CopyDependencies;
import com.unclezs.novel.app.jfx.packager.action.CreateRunnableJar;
import com.unclezs.novel.app.jfx.packager.action.CreateTar;
import com.unclezs.novel.app.jfx.packager.action.CreateZip;
import com.unclezs.novel.app.jfx.packager.action.mac.GenerateDmg;
import com.unclezs.novel.app.jfx.packager.action.mac.GeneratePkg;
import com.unclezs.novel.app.jfx.packager.action.windows.CreateWindowsExe;
import com.unclezs.novel.app.jfx.packager.packager.Packager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Gradle打包 构建工具上下文
 *
 * @author blog.unclezs.com
 * @date 2021/3/28 23:07
 */
@UtilityClass
public class Context {

  private static final List<ArtifactGenerator> MAC_INSTALLER_GENERATORS = new ArrayList<>();
  private static final List<ArtifactGenerator> WINDOWS_INSTALLER_GENERATORS = new ArrayList<>();
  private static Project project;

  public static void init(Project project) {
    Context.project = project;
    MAC_INSTALLER_GENERATORS.add(new GenerateDmg());
    MAC_INSTALLER_GENERATORS.add(new GeneratePkg());
  }

  public static List<ArtifactGenerator> getMacInstallerGenerators() {
    return MAC_INSTALLER_GENERATORS;
  }

  public static Logger getLogger() {
    return project.getLogger();
  }

  public static Project getProject() {
    return project;
  }

  public static File getRootDir() {
    return project.getRootDir();
  }

  public static File createRunnableJar(Packager packager) throws Exception {
    return new CreateRunnableJar().apply(packager);
  }

  public static File copyDependencies(Packager packager) throws Exception {
    return new CopyDependencies().apply(packager);
  }

  public static File createTar(Packager packager) throws Exception {
    return new CreateTar().apply(packager);
  }

  public static File createZip(Packager packager) throws Exception {
    return new CreateZip().apply(packager);
  }

  public static File createExe(Packager packager) throws Exception {
    return new CreateWindowsExe().apply(packager);
  }
}
