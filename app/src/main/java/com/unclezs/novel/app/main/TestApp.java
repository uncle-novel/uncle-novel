package com.unclezs.novel.app.main;


import com.unclezs.novel.app.framework.util.ResourceUtils;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    ResourceUtils.stream("com/sun/javafx/scene/control/skin/modena/modena.css");
    for (int i = 1; i < 500; i++) {
      System.out.printf("$_%dpx: %.2fem;%n", i, (i / 12.0));
    }
  }
}
