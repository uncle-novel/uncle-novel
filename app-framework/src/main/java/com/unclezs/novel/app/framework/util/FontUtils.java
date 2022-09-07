package com.unclezs.novel.app.framework.util;

import javafx.scene.text.Font;
import lombok.experimental.UtilityClass;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author blog.unclezs.com
 * @since 2021/7/16 8:37
 */
@UtilityClass
public class FontUtils {
  /**
   * 获取系统的所有字体
   */
  public List<String> getAllFontFamilies() {
    Set<String> fonts = new HashSet<>();
    fonts.addAll(Font.getFamilies());
    fonts.addAll(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
    ArrayList<String> list = new ArrayList<>(fonts);
    Collections.sort(list);
    return list;
  }
}
