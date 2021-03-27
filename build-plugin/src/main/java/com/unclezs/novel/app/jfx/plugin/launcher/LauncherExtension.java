package com.unclezs.novel.app.jfx.plugin.launcher;

import com.unclezs.novel.app.jfx.launcher.model.Library;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.gradle.api.Project;

/**
 * @author blog.unclezs.com
 * @date 2021/03/20 10:46
 */
@Data
public class LauncherExtension {

  private final Project project;
  private File workDir;
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
  private List<Library> libs;
  /**
   * 启动类
   */
  private String launcherClass;

  public LauncherExtension(Project project) {
    this.project = project;
  }

  public File getWorkDir() {
    return workDir == null ? new File(String.format("%s/app", project.getProject().getBuildDir())) : workDir;
  }
}
