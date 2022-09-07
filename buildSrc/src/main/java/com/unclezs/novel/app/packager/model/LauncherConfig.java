package com.unclezs.novel.app.packager.model;

import com.unclezs.jfx.launcher.Manifest;
import com.unclezs.jfx.launcher.Platform;
import com.unclezs.jfx.launcher.Resource;
import com.unclezs.jfx.launcher.Resource.Type;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gradle.api.Project;

/**
 * 启动器配置
 *
 * @author blog.unclezs.com
 * @since 2021/03/20 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LauncherConfig extends Manifest {

  private final Project project;
  /**
   * maven仓库坐标
   * <p>
   * com.unclezs.jfx:fx-launcher:1.0.2-SNAPSHOT
   */
  private String coordinate;
  /**
   * 资源位置映射 mapper，按照文件名和后缀映射到本地相对目录
   */
  private Map<String, String> resourceMapper = new HashMap<>();
  /**
   * 其他资源
   */
  private List<File> extResources = new ArrayList<>();
  /**
   * 是否需要同时包含最新的依赖
   */
  private Boolean withLibraries = true;

  public LauncherConfig(Project project) {
    this.project = project;
  }

  /**
   * 添加需要更新的资源
   *
   * @param path     资源本地路径
   * @param type     资源类型 {@link com.unclezs.jfx.launcher.Resource.Type}
   * @param platform 操作系统 {@link com.unclezs.jfx.launcher.Platform}
   * @param dir      同步后到本地的文件夹，相对于app目录
   */
  public void resource(String path, String type, String platform, String dir) {
    Resource resource = new Resource(path, new File(path).length(), Platform.fromString(platform), type == null ? null : Type.valueOf(type));
    resources.add(resource);
    // 添加文件位置映射
    if (dir != null) {
      resourceMapper.put(new File(path).getName(), dir);
    }
  }

  /**
   * 添加需要更新的资源
   *
   * @param path 资源本地路径
   * @param type 资源类型 {@link com.unclezs.jfx.launcher.Resource.Type}
   * @param dir  同步后到本地的文件夹，相对于app目录
   */
  public void resource(String path, String type, String dir) {
    resource(path, type, null, dir);
  }

  /**
   * 添加需要更新的资源
   *
   * @param path 资源本地路径
   * @param dir  同步后到本地的文件夹，相对于app目录
   */
  public void resource(String path, String dir) {
    resource(path, null, dir);
  }

  /**
   * 添加需要更新的资源
   *
   * @param path 资源本地路径
   */
  public void resource(String path) {
    resource(path, null);
  }
}
