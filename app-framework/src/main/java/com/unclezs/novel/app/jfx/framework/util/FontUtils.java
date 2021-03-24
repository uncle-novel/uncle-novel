package com.unclezs.novel.app.jfx.framework.util;

import javafx.scene.text.Font;

/**
 * @author blog.unclezs.com
 * @date 2021/02/27 0:59
 */
public class FontUtils {

  public static void loadFontawesome() {
    //加载字体图标
    Font.loadFont(ResourceUtils.load("/fonts/fontawesome-webfont.ttf").toString(), 14);
  }

  public static void loadTraditional() {
    //加载字体图标
    Font.loadFont(ResourceUtils.load("/fonts/pingfang-traditional-bold.ttf").toString(), 14);
  }
}
