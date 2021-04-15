package com.unclezs.novel.app.main;


/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    for (int i = 1; i < 500; i++) {
      System.out.printf("$_%dpx: %.2fem;%n", i, (i / 12.0));
    }
  }
}
