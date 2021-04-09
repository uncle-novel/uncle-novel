package com.unclezs.novel.app.main;


import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.Set;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    Set<ModuleReference> references = ModuleFinder.of(Path.of("G:\\coder\\self-coder\\uncle-novel-jfx\\buildSrc\\build\\libs\\app-packager.jar")).findAll();
    for (ModuleReference reference : references) {
      System.out.println(reference.descriptor().name());
    }
  }
}
