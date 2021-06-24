package com.unclezs.novel.app.main.test;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 9:59
 */
public class TTSTest {

  public static void main(String[] args) {
    System.out.println(GsonUtils.NULL_PRETTY.toJson(new RequestParams()));
  }
}
