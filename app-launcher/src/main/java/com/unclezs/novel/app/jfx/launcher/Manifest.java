package com.unclezs.jfx.launcher;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author blog.unclezs.com
 * @date 2021/03/21 11:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Manifest {

  public static final Gson GSON = new Gson();

  /**
   * 嵌入Jar的配置文件名
   */
  public static final String EMBEDDED_CONFIG_NAME = "app.json";
  public static final String BACKSLASH = "/";
  /**
   * 配置文件名
   */
  protected String configName = EMBEDDED_CONFIG_NAME;
  /**
   * 应用 Logo
   */
  protected String appName = "Uncle小说";
  /**
   * 服务器地址
   */
  protected String serverUri;
  /**
   * 服务端配置的URI
   */
  protected String configServerUri;
  /**
   * 依赖文件夹
   */
  protected String libDir;
  /**
   * 版本
   */
  protected String version;
  /**
   * 更新内容
   */
  protected List<String> changeLog = new ArrayList<>();
  /**
   * 依赖
   */
  protected List<Library> libs = new ArrayList<>();
  /**
   * 启动类
   */
  protected String launcherClass;

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
      return GSON.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Manifest.class);
    }
  }

  /**
   * 获取嵌入的 manifest
   *
   * @return manifest
   * @throws Exception /
   */
  public static Manifest embedded() throws Exception {
    URL resource = LauncherApp.class.getResource(BACKSLASH.concat(Manifest.EMBEDDED_CONFIG_NAME));
    return load(resource.toURI());
  }

  /**
   * 设置 服务器地址 保证 /结尾
   *
   * @param serverUri 文件服务器地址
   */
  public void setServerUri(String serverUri) {
    if (!serverUri.endsWith(BACKSLASH)) {
      serverUri = serverUri.concat(BACKSLASH);
    }
    this.serverUri = serverUri;
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
    if (configServerUri == null || configServerUri.isBlank()) {
      return URI.create(serverUri.concat(configName));
    }
    return URI.create(configServerUri);
  }
}
