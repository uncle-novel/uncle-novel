package com.unclezs.novel.app.jfx.packager.model;

import com.unclezs.jfx.launcher.Manifest;
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
   * launcher的的依赖
   */
  private String[] runTimeLibrary = {};
  /**
   * 生成部署依赖的文件夹
   */
  private String deployDir = "deploy";
  /**
   * 启动Jar的名字
   */
  private String launcherJarName = "app";
  /**
   * 只创建部署文件，不打包
   */
  private Boolean onlyCreateDeployFile = false;
  /**
   * maven仓库坐标
   * <p>
   * com.unclezs.jfx:fx-launcher:1.0.2-SNAPSHOT
   */
  private String coordinate;

  public LauncherConfig(Project project) {
    this.project = project;
  }
}
