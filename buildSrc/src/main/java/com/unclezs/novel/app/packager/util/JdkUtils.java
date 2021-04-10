package com.unclezs.novel.app.packager.util;

import com.unclezs.novel.app.packager.exception.PackageException;
import com.unclezs.novel.app.packager.model.Platform;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * JDK工具
 *
 * @author blog.unclezs.com
 * @date 2021/4/6 16:41
 */
@UtilityClass
public class JdkUtils {

  /**
   * Converts "release" file from specified JDK or JRE to map
   *
   * @param jdkPath JDK directory path
   * @return Map with all properties
   * @throws java.io.FileNotFoundException release file not found
   * @throws java.io.IOException           release file could not be read
   */
  private static Map<String, String> getRelease(File jdkPath) throws FileNotFoundException, IOException {
    Map<String, String> propertiesMap = new HashMap<>(16);
    File releaseFile = new File(jdkPath, "release");
    if (!releaseFile.exists()) {
      throw new FileNotFoundException("release file not found: " + releaseFile);
    }
    Properties properties = new Properties();
    properties.load(new FileInputStream(releaseFile));
    properties.forEach((key, value) -> propertiesMap.put(key.toString(), value.toString().replaceAll("^\"|\"$", "")));
    return propertiesMap;
  }

  /**
   * Checks if the platform specified in the "release" file matches the required platform
   *
   * @param platform /
   * @param jdkPath  /
   * @return /
   * @throws java.io.FileNotFoundException /
   * @throws java.io.IOException           /
   */
  private static boolean checkPlatform(Platform platform, File jdkPath)
    throws FileNotFoundException, IOException {
    Map<String, String> releaseMap = getRelease(jdkPath);
    String osName = releaseMap.get("OS_NAME");
    switch (platform) {
      case linux:
        return "Linux".equalsIgnoreCase(osName);
      case mac:
        return "Darwin".equalsIgnoreCase(osName);
      case win:
        return "Windows".equalsIgnoreCase(osName);
      default:
        return false;
    }
  }

  /**
   * Checks if a JDK is for platform
   *
   * @param platform Specific platform
   * @param jdkPath  Path to the JDK folder
   * @return true if is valid, otherwise false
   * @throws java.io.FileNotFoundException Path to JDK not found
   * @throws java.io.IOException           Error reading JDK "release" file
   */
  public static boolean isValidJdk(Platform platform, File jdkPath) throws FileNotFoundException, IOException {
    return checkPlatform(platform, jdkPath);
  }

  /**
   * Checks if a JRE is for platform
   *
   * @param platform Specific platform
   * @param jrePath  Path to the JRE folder
   * @return true if is valid, otherwise false
   * @throws java.io.IOException Error reading JDK "release" file
   */
  public static boolean isValidJre(Platform platform, File jrePath) throws IOException {
    try {
      return checkPlatform(platform, jrePath);
    } catch (FileNotFoundException e) {
      return new File(jrePath, "bin/java").exists() || new File(jrePath, "bin/java.exe").exists();
    }
  }

  public static int getJavaMajorVersion() {
    String[] version = System.getProperty("java.version").split("\\.");
    int major = Integer.parseInt(version[0]);
    if (major >= 2) {
      return major;
    }
    return Integer.parseInt(version[1]);
  }

  /**
   * 读取 module-info 获取Jar包的 模块名称
   * <p>
   * gson.jar -> com.google.gson
   *
   * @param jar jar文件
   * @return 模块名字
   */
  public static String getModuleName(File jar) {
    Set<ModuleReference> references = ModuleFinder.of(jar.toPath()).findAll();
    return references.stream().findFirst()
      .orElseThrow(() -> new PackageException("模块未找到"))
      .descriptor().name();
  }
}
