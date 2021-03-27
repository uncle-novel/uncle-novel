package com.unclezs.novel.app.jfx.plugin.launcher.tasks;

import com.unclezs.novel.app.jfx.launcher.model.Manifest;
import java.io.File;

/**
 * @author blog.unclezs.com
 * @since 2021/03/23 14:13
 */
public class CreateManifestTask {

  public static void main(String[] args) {
    File[] files = new File(".", "app/build/app").listFiles(pathname -> true);
    Manifest manifest = new Manifest();

  }
}
