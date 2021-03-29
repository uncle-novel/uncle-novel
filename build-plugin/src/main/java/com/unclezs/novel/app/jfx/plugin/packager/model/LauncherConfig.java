package com.unclezs.novel.app.jfx.plugin.packager.model;

import com.unclezs.novel.app.jfx.launcher.model.Library;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.gradle.api.Project;

/**
 * 启动器配置
 *
 * @author blog.unclezs.com
 * @date 2021/03/20 10:46
 */
@Data
public class LauncherConfig {

  private final Project project;
  /**
   * 是否移除非Launcher的依赖(也就是真正App的依赖)
   */
  protected Boolean deleteAppLibrary = true;
  private Set<String> excludes;
  private String configName;
  private String appName = "Welcome";
  /**
   * 服务器地址
   */
  private String serverUri;
  /**
   * 服务端配置的URI
   */
  private String configServerUri;
  /**
   * 依赖文件夹
   */
  private String libDir;
  /**
   * 版本
   */
  private String version;
  /**
   * 更新内容
   */
  private List<String> changeLog = new ArrayList<>();
  /**
   * 依赖
   */
  private List<Library> libs = new ArrayList<>();
  /**
   * 启动类
   */
  private String launcherClass;
  /**
   * launcher的的依赖
   */
  private String[] runTimeLibrary = {};
  /**
   * 生成部署依赖的文件夹
   */
  private String deployDir = "deploy";
  private String launcherJarName = "app";

  public LauncherConfig(Project project) {
    this.project = project;
  }
}
