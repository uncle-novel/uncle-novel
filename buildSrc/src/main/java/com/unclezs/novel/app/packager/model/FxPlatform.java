package com.unclezs.novel.app.packager.model;

import com.google.gradle.osdetector.OsDetector;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 平台工具
 *
 * @author unclezs
 * @since 2022/05/11
 */
public enum FxPlatform {

  /**
   * linux
   */
  LINUX("linux", "linux-x86_64"),
  /**
   * linux aarch64
   */
  LINUX_AARCH64("linux-aarch64", "linux-aarch_64"),
  /**
   * 窗户
   */
  WINDOWS("win", "windows-x86_64"),
  /**
   * osx
   */
  OSX("mac", "osx-x86_64"),
  /**
   * osx aarch64
   */
  OSX_AARCH64("mac-aarch64", "osx-aarch_64");

  private final String classifier;
  private final String osDetectorClassifier;

  FxPlatform(String classifier, String osDetectorClassifier) {
    this.classifier = classifier;
    this.osDetectorClassifier = osDetectorClassifier;
  }

  public String getClassifier() {
    return classifier;
  }

  public static FxPlatform detect(Project project) {

    final String osClassifier = project.getExtensions().getByType(OsDetector.class).getClassifier();;

    for (FxPlatform platform : values()) {
      if (platform.osDetectorClassifier.equals(osClassifier)) {
        return platform;
      }
    }

    String supportedPlatforms = Arrays.stream(values())
      .map(p -> p.osDetectorClassifier)
      .collect(Collectors.joining("', '", "'", "'"));

    throw new GradleException(
      String.format(
        "Unsupported JavaFX platform found: '%s'! " +
          "This plugin is designed to work on supported platforms only." +
          "Current supported platforms are %s.", osClassifier, supportedPlatforms)
    );

  }
}
