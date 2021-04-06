package com.unclezs.novel.app.main;


import cn.hutool.core.io.FileUtil;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    String exe="java";
    String paths = System.getenv("PATH");
    for (String path : paths.split(";")) {
      for (String name : FileUtil.listFileNames(path)) {

        if(name.startsWith(exe)){
          System.out.println(path);
        }
      }
    }
  }
}
