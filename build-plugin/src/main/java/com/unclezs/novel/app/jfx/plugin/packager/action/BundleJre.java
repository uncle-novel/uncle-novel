package com.unclezs.novel.app.jfx.plugin.packager.action;

import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.CommandUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.FileUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.JavaUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.JdkUtils;
import com.unclezs.novel.app.jfx.plugin.packager.util.Logger;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Bundles a Java Runtime Enrironment (JRE) with the app
 */
public class BundleJre extends ArtifactGenerator {

  public BundleJre() {
    super("JRE");
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.getBundleJre();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override
  protected File doApply(Packager packager) throws Exception {

    boolean bundleJre = packager.getBundleJre();
    File specificJreFolder = packager.getJrePath();
    Platform platform = packager.getPlatform();
    File destinationFolder = packager.getJreDestinationFolder();
    File jdkPath = packager.getJdkPath();
    File libsFolder = packager.getLibsFolder();
    boolean customizedJre = packager.getCustomizedJre();
    File jarFile = packager.getJarFile();
    List<String> requiredModules = packager.getModules();
    List<String> additionalModules = packager.getAdditionalModules();
    List<File> additionalModulePaths = packager.getAdditionalModulePaths();

    File currentJdk = new File(System.getProperty("java.home"));

    Logger.infoIndent("Bundling JRE ... with " + currentJdk);

    if (specificJreFolder != null) {
      Logger.info("Embedding JRE from " + specificJreFolder);
      // fixes the path to the JRE on MacOS if "release" file not found
      if (platform.equals(Platform.mac) && !FileUtils.folderContainsFile(specificJreFolder, "release")) {
        specificJreFolder = new File(specificJreFolder, "Contents/Home");
        Logger.warn("Specified jrePath fixed: " + specificJreFolder);
      }
      // checks if valid jre specified
      if (!JdkUtils.isValidJRE(platform, specificJreFolder)) {
        throw new Exception(
            "Invalid JRE specified for '" + platform + "' platform: " + specificJreFolder);
      }
      // removes old jre folder from bundle
      if (destinationFolder.exists()) {
        FileUtils.removeFolder(destinationFolder);
      }

      // copies JRE folder to bundle
      FileUtils.copyFolderContentToFolder(specificJreFolder, destinationFolder);

      // sets execution permissions on executables in jre
      File binFolder = new File(destinationFolder, "bin");
      Arrays.asList(Objects.requireNonNull(binFolder.listFiles()))
          .forEach(f -> f.setExecutable(true, false));

    } else if (JavaUtils.getJavaMajorVersion() <= 8) {

      throw new Exception(
          "Could not create a customized JRE due to JDK version is " + SystemUtils.JAVA_VERSION
              + ". Must use jrePath property to specify JRE location to be embedded");

    } else if (!platform.isCurrentPlatform() && jdkPath.equals(currentJdk)) {
      Logger.warn("Cannot create a customized JRE ... target platform (" + platform
          + ") is different than execution platform (" + Platform.getCurrentPlatform()
          + "). Use jdkPath property.");
      bundleJre = false;
    } else {
      Logger.info("Creating customized JRE ...");
      // tests if specified JDK is for the same platform than target platform
      if (!JdkUtils.isValidJDK(platform, jdkPath)) {
        throw new Exception("Invalid JDK for platform '" + platform + "': " + jdkPath);
      }
      String modules = getRequiredModules(libsFolder, customizedJre, jarFile, requiredModules,
          additionalModules, additionalModulePaths);
      Logger.info("Creating JRE with next modules included: " + modules);
      File modulesDir = new File(jdkPath, "jmods");
      if (!modulesDir.exists()) {
        throw new Exception("jmods folder doesn't exist: " + modulesDir);
      }
      Logger.info("Using " + modulesDir + " modules directory");
      File jlink = new File(currentJdk, "/bin/jlink");
      if (destinationFolder.exists()) {
        FileUtils.removeFolder(destinationFolder);
      }
      // generates customized jre using modules
      CommandUtils
          .execute(jlink.getAbsolutePath(), "--module-path", modulesDir, "--add-modules", modules,
              "--output", destinationFolder, "--no-header-files", "--no-man-pages", "--strip-debug",
              "--compress=2");
      // sets execution permissions on executables in jre
      File binFolder = new File(destinationFolder, "bin");
      Arrays.asList(Objects.requireNonNull(binFolder.listFiles()))
          .forEach(f -> f.setExecutable(true, false));
    }

    // removes jre/legal folder
    File legalFolder = new File(destinationFolder, "legal");
    if (legalFolder.exists()) {
      FileUtils.removeFolder(legalFolder);
    }

    if (bundleJre) {
      Logger.infoUnindent("JRE bundled in " + destinationFolder.getAbsolutePath() + "!");
    } else {
      Logger.infoUnindent("JRE bundling skipped!");
    }

    return destinationFolder;
  }

  /**
   * Uses jdeps command tool to determine which modules all used jar files depend on
   *
   * @param libsFolder            folder containing all needed libraries
   * @param customizedJre         if true generates a customized JRE, including only identified or
   *                              specified modules. Otherwise, all modules will be included.
   * @param jarFile               Runnable jar file reference
   * @param defaultModules        Additional files and folders to include in the bundled app.
   * @param additionalModules     Defines modules to customize the bundled JRE. Don't use jdeps to
   *                              get module dependencies.
   * @param additionalModulePaths Defines additional module paths to customize the bundled JRE.
   * @return string containing a comma separated list with all needed modules
   * @throws Exception Process failed
   */
  protected String getRequiredModules(File libsFolder, boolean customizedJre, File jarFile,
      List<String> defaultModules, List<String> additionalModules, List<File> additionalModulePaths)
      throws Exception {

    Logger.infoIndent("Getting required modules ... ");

    File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

    File jarLibs = null;
    if (libsFolder != null && libsFolder.exists()) {
      jarLibs = new File(libsFolder, "*.jar");
    } else {
      Logger.warn("No dependencies found!");
    }

    List<String> modulesList;

    if (customizedJre && defaultModules != null && !defaultModules.isEmpty()) {
      modulesList =
          defaultModules
              .stream()
              .map(String::trim)
              .collect(Collectors.toList());
    } else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 13) {
      String modules =
          CommandUtils.execute(
              jdeps.getAbsolutePath(),
              "-q",
              "--multi-release", JavaUtils.getJavaMajorVersion(),
              "--ignore-missing-deps",
              "--print-module-deps",
              additionalModulePathsToParams(additionalModulePaths),
              jarLibs,
              jarFile
          );
      modulesList =
          Arrays.stream(modules.split(","))
              .map(String::trim)
              .filter(module -> !module.isEmpty())
              .collect(Collectors.toList());
    } else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 9) {
      String modules =
          CommandUtils.execute(
              jdeps.getAbsolutePath(),
              "-q",
              "--multi-release", JavaUtils.getJavaMajorVersion(),
              "--list-deps",
              additionalModulePathsToParams(additionalModulePaths),
              jarLibs,
              jarFile
          );
      modulesList =
          Arrays.stream(modules.split("\n"))
              .map(String::trim)
              .map(module -> (module.contains("/") ? module.split("/")[0] : module))
              .filter(module -> !module.isEmpty())
              .filter(module -> !module.startsWith("JDK removed internal"))
              .distinct()
              .collect(Collectors.toList());

    } else {
      modulesList = Collections.singletonList("ALL-MODULE-PATH");
    }
    modulesList.addAll(additionalModules);
    if (modulesList.isEmpty()) {
      Logger.warn(
          "It was not possible to determine the necessary modules. All modules will be included");
      modulesList.add("ALL-MODULE-PATH");
    }
    Logger.infoUnindent("Required modules found: " + modulesList);
    return StringUtils.join(modulesList, ",");
  }

  private String[] additionalModulePathsToParams(List<File> additionalModulePaths) {
    List<String> additionalPaths = new ArrayList<>();
    if (additionalModulePaths != null) {
      additionalModulePaths.stream()
          .filter(path -> {
            if (path.exists()) {
              return true;
            }
            Logger.warn("Additional module path not found: " + path);
            return false;
          })
          .forEach(path -> {
            additionalPaths.add("--module-path");
            additionalPaths.add(path.toString());
          });
    }
    return additionalPaths.toArray(new String[0]);
  }

}
