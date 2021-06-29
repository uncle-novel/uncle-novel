package com.unclezs.novel.app.main.test;

import com.unclezs.novel.analyzer.util.FileUtils;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 9:59
 */
public class TTSTest {

  public static void main(String[] args) throws IOException {
    FileUtils.touch("G:\\tmp/- 飞速中文网 -/38.第三十八章 青阳剑，月音琴.txt".replace("\\", "/"));
  }
}
