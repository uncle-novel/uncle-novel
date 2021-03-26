package com.unclezs.novel.app.jfx.launcher;

import com.sun.javafx.application.PlatformImpl;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.List;
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

  private Stage launcherStage;
  private Manifest manifest;
  private UpdateView ui;

  @Override
  public void init() {
    ui = new UpdateView();
  }

  public void startApplication() throws Exception {
    // 1. 下载配置\解析配置 获取配置内容
    sync();
    ClassLoader classLoader = loadLibraries();
    // 4. 启动launcher class
    Class<?> appClass = classLoader.loadClass(manifest.getLauncherClass());
    Application app = (Application) appClass.getConstructor().newInstance();
    PlatformImpl.runAndWait(() -> {
      try {
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
      Thread.currentThread().setName("Launcher");
      try {
        startApplication();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void sync() throws Exception {
    setupIgnoreSslCertificate();
    // 嵌入Jar包中的
    String embeddedConfigName = File.separator.concat(Manifest.CONFIG_NAME);
    URL resource = FxLauncher.class.getResource(embeddedConfigName);
    manifest = Manifest.load(resource.toURI());
    Path localManifestPath = manifest.localManifest();
    // libDir下的
    if (Files.exists(localManifestPath)) {
      manifest = Manifest.load(localManifestPath.toUri());
    }
    Manifest remoteManifest = Manifest.load(manifest.remoteManifest());
    if (manifest.getVersion().equals(remoteManifest.getVersion())) {
      return;
    }
    // 下载配置
    Files.write(manifest.localManifest(), manifest.remoteManifest().toURL().openStream().readAllBytes());
    // 下载依赖
    List<Library> libraries = manifest.resolveRemoteLibraries();
    Files.createDirectories(Path.of(manifest.getLibDir()));
    for (Library library : libraries) {
      Path localPath = Paths.get(manifest.getLibDir(), library.getPath());
      if (!Files.exists(localPath) || Files.size(localPath) != library.getSize()) {
        URL url = library.toUrl(Path.of(manifest.getServerUri()));
        Files.write(localPath, url.openStream().readAllBytes());
        System.out.printf("同步完成: %s%n", library.getPath());
      }
    }
    if (!manifest.getChangeLog().isEmpty()) {
      System.out.println("更新内容：");
      manifest.getChangeLog().forEach(System.out::println);
    }
  }

  /**
   * 忽略SSL 错误
   *
   * @throws Exception /
   */
  protected void setupIgnoreSslCertificate() throws Exception {
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
    List<URL> libs = manifest.resolveLibraries();
    URLClassLoader classLoader = new URLClassLoader(libs.toArray(new URL[0]));
    Thread.currentThread().setContextClassLoader(classLoader);
    FXMLLoader.setDefaultClassLoader(classLoader);
    Platform.runLater(() -> Thread.currentThread().setContextClassLoader(classLoader));
    return classLoader;
  }
}
