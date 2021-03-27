package com.unclezs.novel.app.jfx.launcher.model;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;

/**
 * @author blog.unclezs.com
 * @date 2021/03/21 11:35
 */
@Data
public class Manifest {

  public static final Gson GSON = new Gson();

  /**
   * 嵌入Jar的配置文件名
   */
  public static final String EMBEDDED_CONFIG_NAME = "app.json";
  /**
   * 配置文件名
   */
  private String configName = EMBEDDED_CONFIG_NAME;
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

  /**
   * 加载配合
   *
   * @param uri 配置文件URI
   * @return 配置
   * @throws Exception 加载失败
   */
  @NonNull
  public static Manifest load(URI uri) throws Exception {
    try (InputStream stream = uri.toURL().openStream()) {
      return GSON.fromJson(new BufferedReader(new InputStreamReader(stream)), Manifest.class);
    }
  }

  /**
   * 解析依赖的URL
   *
   * @return 依赖URL列表
   */
  public List<URL> resolveLibraries() {
    return libs.stream().filter(Library::currentPlatform).map(library -> library.toUrl(Path.of(libDir))).collect(Collectors.toList());
  }

  /**
   * 解析远程依赖的URL
   *
   * @return 远程依赖URL列表
   */
  public List<Library> resolveRemoteLibraries() {
    return libs.stream().filter(Library::currentPlatform).collect(Collectors.toList());
  }

  /**
   * 获取 libDir下的配置
   *
   * @return 配置
   */
  public Path localManifest() {
    return Paths.get(libDir, configName);
  }

  /**
   * 获取 远程的配置
   *
   * @return 配置
   */
  public URI remoteManifest() {
    return URI.create(configServerUri);
  }
}
