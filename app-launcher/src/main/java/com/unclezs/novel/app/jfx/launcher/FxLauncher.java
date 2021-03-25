package com.unclezs.novel.app.jfx.launcher;

import com.google.gson.Gson;
import com.sun.javafx.application.ParametersImpl;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:46
 */
public class FxLauncher {

  public static void main(String[] args) throws Exception {
    ParametersImpl parameters = new ParametersImpl(args);
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    LauncherClassLoader launcherClassLoader = new LauncherClassLoader(classLoader);
    // 1. 下载配置 获取配置内容
    String config = Files.readString(Paths.get("app-out.json"));
    // 2. 解析配置
    Manifest manifest = new Gson().fromJson(config, Manifest.class);
    // 3. 解析依赖
    List<URL> libs = manifest.getLibs().stream()
      .filter(Library::currentPlatform)
      .map(lib -> lib.toUrl(Paths.get(manifest.getServerUri())))
      .collect(Collectors.toList());
    launcherClassLoader.addUrls(libs);
    Thread.currentThread().setContextClassLoader(launcherClassLoader);
    FXMLLoader.setDefaultClassLoader(classLoader);
    // 4. 启动launcher class
    Class<?> appClass = launcherClassLoader.loadClass(manifest.getLauncherClass());
    Platform.startup(() -> {
      try {
        Object app = appClass.getConstructor().newInstance();
        appClass.getMethod("init").invoke(app);
        appClass.getMethod("start").invoke(app);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      } finally {
        Thread.currentThread().setContextClassLoader(classLoader);
      }
    });
  }
}
