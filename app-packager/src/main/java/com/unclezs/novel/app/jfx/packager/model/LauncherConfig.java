package com.unclezs.novel.app.jfx.packager.model;

import com.unclezs.jfx.launcher.Manifest;
import java.util.ArrayList;
import java.util.List;
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
  private String launcherJarLibName = "fx-launcher";
  /**
   * 只创建部署文件，不打包
   */
  private Boolean onlyCreateDeployFile = false;
  /**
   * 是否移除非Launcher的依赖(也就是真正App的依赖)
   */
  private Boolean deleteAppLibrary = true;
  private List<String> classpath = new ArrayList<>();

  public LauncherConfig(Project project) {
    this.project = project;
  }
}
