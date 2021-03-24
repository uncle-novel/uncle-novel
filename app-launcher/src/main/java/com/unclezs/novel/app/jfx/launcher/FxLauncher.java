package com.unclezs.novel.app.jfx.launcher;

import com.sun.javafx.application.ParametersImpl;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

/**
 * @author blog.unclezs.com
 * @since 2021/02/27 19:46
 */
public class FxLauncher {

  public static void main(String[] args)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    ParametersImpl parameters = new ParametersImpl(args);
    Map<String, String> named = parameters.getNamed();
    System.out.println(named);
    System.out.println(parameters.getUnnamed());
    System.out.println(parameters.getRaw());

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    LauncherClassLoader launcherClassLoader = new LauncherClassLoader(classLoader);
    List<URL> libs = Arrays.stream(Objects.requireNonNull(
        new File("/Users/zhanghongguo/coder/self-coder/uncle-novel-jfx/app/build/app").listFiles()))
        .map(f -> {
          try {
            return f.toURI().toURL();
          } catch (MalformedURLException e) {
            e.printStackTrace();
          }
          return null;
        }).collect(Collectors.toList());
    launcherClassLoader.addUrls(libs);
    Thread.currentThread().setContextClassLoader(launcherClassLoader);
    FXMLLoader.setDefaultClassLoader(classLoader);
    Class<?> appClass = launcherClassLoader.loadClass("com.unclezs.novel.app.jfx.app.ui.app.App");
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
