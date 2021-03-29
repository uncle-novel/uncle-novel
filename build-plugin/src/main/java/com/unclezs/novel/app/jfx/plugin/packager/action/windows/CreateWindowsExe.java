package com.unclezs.novel.app.jfx.plugin.packager.action.windows;

import com.unclezs.novel.app.jfx.plugin.packager.Context;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.WindowsPackager;
import com.unclezs.novel.app.jfx.plugin.packager.util.FileUtils;
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

/**
 * Creates Windows native executable on Gradle context
 */
public class CreateWindowsExe extends WindowsArtifactGenerator {

  private File genericManifest;
  private File genericIcon;
  private File genericJar;
  private File genericExe;

  public CreateWindowsExe() {
    super("Windows EXE");
  }

  @Override
  protected File doApply(Packager packager) throws Exception {

    WindowsPackager windowsPackager = (WindowsPackager) packager;

    List<String> vmArgs = windowsPackager.getVmArgs();
    WindowsConfig winConfig = windowsPackager.getWinConfig();
    File executable = windowsPackager.getExecutable();
    String mainClass = windowsPackager.getMainClass();
    boolean useResourcesAsWorkingDir = windowsPackager.isUseResourcesAsWorkingDir();
    boolean bundleJre = windowsPackager.getBundleJre();
    String jreDirectoryName = windowsPackager.getJreDirectoryName();
    String jreMinVersion = windowsPackager.getJreMinVersion();
    File jarFile = windowsPackager.getJarFile();

    try {
      // creates a folder only for launch4j assets
      createAssets(windowsPackager);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    String jarPath = winConfig.isWrapJar() ? genericJar.getAbsolutePath() : jarFile.getName();

    Launch4jLibraryTask l4jTask = createLaunch4jTask();
    l4jTask.setHeaderType(winConfig.getHeaderType().toString());
    l4jTask.setJar(jarPath);
    l4jTask.setDontWrapJar(!winConfig.isWrapJar());
    l4jTask.setOutfile(genericExe.getName());
    l4jTask.setIcon(genericIcon.getAbsolutePath());
    l4jTask.setManifest(genericManifest.getAbsolutePath());
    l4jTask.setMainClassName(mainClass);
    l4jTask.setClasspath(new HashSet<>(windowsPackager.getClasspathList()));
    l4jTask.setChdir(useResourcesAsWorkingDir ? "." : "");
    l4jTask.setBundledJrePath(bundleJre ? jreDirectoryName : "%JAVA_HOME%");
    if (!StringUtils.isBlank(jreMinVersion)) {
      l4jTask.setJreMinVersion(jreMinVersion);
    }
    l4jTask.getJvmOptions().addAll(vmArgs);
    l4jTask.setVersion(winConfig.getProductVersion());
    l4jTask.setTextVersion(winConfig.getTxtProductVersion());
    l4jTask.setCopyright(winConfig.getCopyright());
    l4jTask.setCompanyName(winConfig.getCompanyName());
    l4jTask.setFileDescription(winConfig.getFileDescription());
    l4jTask.setProductName(winConfig.getProductName());
    l4jTask.setInternalName(winConfig.getInternalName());
    l4jTask.setTrademarks(winConfig.getTrademarks());
    l4jTask.setLanguage(winConfig.getLanguage());
    l4jTask.setLibraryDir("");
    l4jTask.getActions().forEach(action -> action.execute(l4jTask));

    sign(genericExe, windowsPackager);

    FileUtils.copyFileToFile(genericExe, executable);

    return executable;
  }

  private Launch4jLibraryTask createLaunch4jTask() {
    return Context.getProject().getTasks()
      .create("launch4j_" + UUID.randomUUID(), Launch4jLibraryTask.class);
  }

  private void createAssets(WindowsPackager packager) throws Exception {

    File manifestFile = packager.getManifestFile();
    File iconFile = packager.getIconFile();
    File jarFile = packager.getJarFile();

    File launch4j = new File(Context.getProject().getBuildDir(), "launch4j");
    if (!launch4j.exists()) {
      //noinspection ResultOfMethodCallIgnored
      launch4j.mkdirs();
    }

    genericManifest = new File(launch4j, "app.exe.manifest");
    genericIcon = new File(launch4j, "app.ico");
    genericJar = new File(launch4j, "app.jar");
    genericExe = new File(launch4j, "app.exe");

    FileUtils.copyFileToFile(manifestFile, genericManifest);
    FileUtils.copyFileToFile(iconFile, genericIcon);
    FileUtils.copyFileToFile(jarFile, genericJar);

  }

}
