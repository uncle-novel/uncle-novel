package com.unclezs.novel.app.framework.util;

import cn.hutool.core.io.IORuntimeException;
import com.unclezs.novel.app.framework.components.Toast;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 桌面工具
 *
 * @author blog.unclezs.com
 * @date 2021/4/21 9:35
 */
@Slf4j
@UtilityClass
public class DesktopUtils {

  /**
   * 浏览器打开
   *
   * @param url 地址
   */
  public static void openBrowse(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (Exception e) {
      log.error("链接打开失败：{}", url, e);
      throw new IORuntimeException(e);
    }
  }

  /**
   * 打开文件夹
   *
   * @param dir 文件夹
   */
  public static void openDir(File dir) {
    if (!dir.exists()) {
      Toast.error("文件夹不存在");
    }
    try {
      Desktop.getDesktop().open(dir);
    } catch (IOException e) {
      log.error("文件夹打开失败：{}", dir, e);
      throw new IORuntimeException(e);
    }
  }

  /**
   * 复制到剪贴板 , 须在FX线程调用
   *
   * @param text 要复制的文字
   */
  public static void copyLink(String text) {
    Clipboard cb = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.put(DataFormat.PLAIN_TEXT, text);
    cb.setContent(content);
  }
}
