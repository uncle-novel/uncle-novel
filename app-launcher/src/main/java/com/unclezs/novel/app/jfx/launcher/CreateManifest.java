package com.unclezs.novel.app.jfx.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unclezs.novel.app.jfx.launcher.enums.Os;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author blog.unclezs.com
 * @date 2021/03/25 23:12
 */
public class CreateManifest {

  public static void main(String[] args) throws IOException {
    File[] files = new File(".", "app/build/app").listFiles(pathname -> true);
    Manifest manifest = new Manifest();
    ArrayList<Library> libs = new ArrayList<>();
    manifest.setLibs(libs);
    manifest.setLibDir("lib");
    manifest.setLauncherClass("com.unclezs.novel.app.jfx.app.ui.app.App");
    manifest.setVersion("1.0.7");
    manifest.setAppName("Uncle小说");
    manifest.setServerUri(Path.of("app/build/app").toUri().toURL().toString());
    manifest.getChangeLog().add("1. 啥都没更新");
    manifest.getChangeLog().add("1. 啥都没更新");
    manifest.getChangeLog().add("1. 啥都没更新");
    if (files != null) {
      for (File file : files) {
        Library lib = new Library();
        lib.setOs(Os.CURRENT);
        lib.setPath(file.getName());
        lib.setSize(file.length());
        libs.add(lib);
        System.out.println(lib);
      }
    }
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Path configPath = Path.of(Manifest.EMBEDDED_CONFIG_NAME);
    manifest.setConfigServerUri(configPath.toUri().toURL().toString());
    String json = gson.toJson(manifest);
    Files.writeString(configPath, json);
    Files.writeString(Paths.get("app-launcher/src/main/resources", Manifest.EMBEDDED_CONFIG_NAME), json);
    System.out.println(json);
  }
}
