package com.unclezs.novel.app.packager.model;

import com.unclezs.jfx.launcher.Manifest;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gradle.api.Project;

/**
 * 启动器配置
 *
 * @author blog.unclezs.com
 * @date 2021/03/20 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LauncherConfig extends Manifest {

  private final Project project;
  /**
   * 是否启用
   */
  private Boolean enabled;
  /**
   * maven仓库坐标
   * <p>
   * com.unclezs.jfx:fx-launcher:1.0.2-SNAPSHOT
   */
  private String coordinate;
  /**
   * 文件 mapper
   */
  private Map<String, String> fileMapper = new HashMap<>();
  /**
   * 是否需要同时包含最新的依赖
   */
  private Boolean withLibraries = true;

  public LauncherConfig(Project project) {
    this.project = project;
  }
}
