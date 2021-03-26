package com.unclezs.novel.app.jfx.launcher;

import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:46
 */
public class FxLauncher extends Application {

  private static final Logger LOG = LoggerHelper.get(FxLauncher.class);
  private Stage launcherStage;
  private Manifest manifest;
  private UpdateView ui;


  @Override
  public void init() {
    Thread.currentThread().setName("Launcher");
    ui = new UpdateView();
  }

  public void startApplication() throws Exception {
    ignoreSslCertificate();
    // 1. 下载配置\解析配置 获取配置内容
    sync();
    ClassLoader classLoader = loadLibraries();
    // 4. 启动launcher class
    Class<?> appClass = classLoader.loadClass(manifest.getLauncherClass());
    Platform.runLater(() -> {
      try {
        Application app = (Application) appClass.getConstructor().newInstance();
        launcherStage.close();
        app.init();
        app.start(null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void start(Stage primaryStage) {
    launcherStage = primaryStage;
    StackPane container = new StackPane(ui);
    Scene scene = new Scene(container, 300, 300);
    launcherStage.setScene(scene);
    launcherStage.show();
    //noinspection AlibabaAvoidManuallyCreateThread
    new Thread(() -> {
      try {
        startApplication();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void sync() {
    // 嵌入Jar包中的
    Manifest remoteManifest;
    try {
      LOG.info("解析本地配置文件");
      String embeddedConfigName = "/".concat(Manifest.EMBEDDED_CONFIG_NAME);
      URL resource = FxLauncher.class.getResource(embeddedConfigName);
      manifest = Manifest.load(resource.toURI());
      Path localManifestPath = manifest.localManifest();
      // libDir下的
      if (Files.exists(localManifestPath)) {
        manifest = Manifest.load(localManifestPath.toUri());
      }
      ui.setWhatNew(manifest.getAppName());
      LOG.info(String.format("获取远程配置文件，%s", manifest.remoteManifest()));
      ui.setPhase("正在检测是否有新版本...");
      remoteManifest = Manifest.load(manifest.remoteManifest());
    } catch (Exception e) {
      LOG.warning(String.format("同步配置文件失败，%s \n %s", manifest.remoteManifest(), e.getMessage()));
      throw new RuntimeException(e);
    }
    if (!checkNew(remoteManifest)) {
      ui.setPhase(String.format("当前已是最新版本：%s", manifest.getVersion()));
      return;
    }
    ui.setPhase(String.format("检测到新版本：%s", manifest.getVersion()));
    manifest = remoteManifest;
    // 显示更新内容
    if (manifest.getChangeLog().isEmpty()) {
      ui.setWhatNew(manifest.getAppName());
    } else {
      LOG.info(String.format("更新内容：%s", manifest.getChangeLog()));
      ui.setWhatNew(manifest.getChangeLog());
    }
    syncLibraries();
  }

  /**
   * 忽略 SSL 错误
   *
   * @throws Exception /
   */
  protected void ignoreSslCertificate() throws Exception {
    TrustManager[] trustManager = new TrustManager[]{
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      }};
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustManager, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
  }

  /**
   * 自定义Classloader加载依赖
   */
  private ClassLoader loadLibraries() {
    // 加载依赖
    List<URL> libs = manifest.resolveLibraries();
    URLClassLoader classLoader = new URLClassLoader(libs.toArray(new URL[0]));
    // 配置Classloader
    Thread.currentThread().setContextClassLoader(classLoader);
    FXMLLoader.setDefaultClassLoader(classLoader);
    FxUtils.runFx(() -> Thread.currentThread().setContextClassLoader(classLoader));
    return classLoader;
  }

  private void syncLibraries() {
    try {
      // 不存在则创建
      Path libDir = Path.of(manifest.getLibDir());
      if (Files.notExists(libDir)) {
        Files.createDirectories(Path.of(manifest.getLibDir()));
      }
      ui.setPhase("正在同步最新版本配置...");
      Files.write(manifest.localManifest(), manifest.remoteManifest().toURL().openStream().readAllBytes());
      ui.setPhase("正在下载最新版本...");
      List<Library> libraries = manifest.resolveRemoteLibraries();
      ui.setProgress(0);
      double i = 0;
      for (Library library : libraries) {
        Path localPath = Paths.get(manifest.getLibDir(), library.getPath());
        if (!Files.exists(localPath) || Files.size(localPath) != library.getSize()) {
          URL url = library.toUrl(Path.of(URI.create(manifest.getServerUri())));
          Files.write(localPath, url.openStream().readAllBytes());
          LOG.info(String.format("下载完成: %s", library.getPath()));
        }
        ui.setProgress(++i / libraries.size());
        Thread.sleep(50);
      }
    } catch (Exception e) {
      LOG.warning(String.format("更新最新版本失败: %s", e.getMessage()));
      throw new RuntimeException(e);
    }
  }

  /**
   * 检测是否有新版本
   *
   * @param remote 远程配置
   * @return true 有
   */
  private boolean checkNew(Manifest remote) {
    try {
      if (!manifest.equals(remote)) {
        return true;
      }
      for (Library library : remote.resolveRemoteLibraries()) {
        Path localPath = Paths.get(remote.getLibDir(), library.getPath());
        if (!Files.exists(localPath) || Files.size(localPath) != library.getSize()) {
          return true;
        }
      }
      return false;
    } catch (IOException e) {
      LOG.warning(String.format("检测是否有新版本失败: %s", e.getMessage()));
      throw new RuntimeException();
    }
  }
}
