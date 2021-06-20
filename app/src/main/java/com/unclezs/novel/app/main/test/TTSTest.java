package com.unclezs.novel.app.main.test;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.main.model.config.TTSConfig;

import java.util.Locale;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 9:59
 */
public class TTSTest {

  public static void main(String[] args) {
    Locale locale = Locale.getDefault();
    System.out.println(locale.getCountry());
    System.out.println(locale.getLanguage());
    System.out.println(locale);
  }
}
