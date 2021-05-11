package com.unclezs.novel.app.main.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import java.io.File;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 23:04
 */
public class ZipTest {

  public static void main(String[] args) {
    File file = ZipUtil.zip("G:\\coder\\self-coder\\uncle-novel-jfx\\conf");
    ZipUtil.unzip(file, FileUtil.file("G:\\coder\\self-coder\\uncle-novel-jfx\\conf1"));
  }
}
