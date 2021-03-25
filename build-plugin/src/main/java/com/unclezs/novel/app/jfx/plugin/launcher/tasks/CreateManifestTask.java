package com.unclezs.novel.app.jfx.plugin.launcher.tasks;

import com.unclezs.novel.app.jfx.launcher.enums.Os;
import com.unclezs.novel.app.jfx.launcher.model.Library;
import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.File;
import java.util.ArrayList;

/**
 * @author blog.unclezs.com
 * @since 2021/03/23 14:13
 */
public class CreateManifestTask {

  public static void main(String[] args) {
    File[] files = new File(".", "app/build/app").listFiles(pathname -> true);
    Manifest manifest = new Manifest();
    ArrayList<Library> libs = new ArrayList<>();
    manifest.setLibs(libs);
    if (files != null) {
      for (File file : files) {
        Library lib = new Library();
        lib.setOs(Os.WIN);
        lib.setPath(file.getName());
        lib.setSize(file.length() + "");
        libs.add(new Library());
      }
    }
    System.out.println(libs);
  }
}
