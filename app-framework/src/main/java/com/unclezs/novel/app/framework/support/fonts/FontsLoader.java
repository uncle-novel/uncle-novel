package com.unclezs.novel.app.framework.support.fonts;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import javafx.scene.text.Font;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @since 2021/6/5 16:58
 */
@Slf4j
@UtilityClass
public class FontsLoader {

  /**
   * 加载文件夹下的所有字体
   *
   * @param dir 字体文件夹
   */
  public void loadFonts(File dir) {
    if (!dir.exists()) {
      log.debug("字体文件夹不存在：{}", dir);
      return;
    }
    String[] fonts = dir.list();
    if (fonts == null) {
      log.debug("未发现字体：{}", dir);
      return;
    }
    for (String filename : fonts) {
      try {
        String fontUrl = URLUtil.getURL(FileUtil.file(dir, filename)).toExternalForm();
        Font font = Font.loadFont(fontUrl, Font.getDefault().getSize());
        if (font != null) {
          log.debug("加载{}字体成功", font.getFamily());
        } else {
          log.error("加载{}字体失败", filename);
        }
      } catch (Exception e) {
        log.error("加载{}字体失败", filename, e);
      }
    }
  }
}
