package com.unclezs.novel.app.main.test;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.app.main.model.WebDav;
import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/5/10 20:38
 */
public class WebDavTest {

  public static String url = "https://dav.jianguoyun.com/dav/";
  public static String username = "unclezs@qq.com";
  public static String password = "adpiy2mmpbv3svq5";

  public static void main(String[] args) throws IOException {
    WebDav webDav = WebDav.createDefault()
      .setUrl(url)
      .setPassword(password)
      .setUsername(username)
      .child("conf.json");
    webDav.upload(FileUtil.file("G:\\coder\\self-coder\\uncle-novel-jfx\\conf\\conf.json"));
//    webDav.download();
  }

}
