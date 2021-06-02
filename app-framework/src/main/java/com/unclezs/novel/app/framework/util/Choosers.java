package com.unclezs.novel.app.framework.util;

import com.unclezs.novel.app.framework.core.AppContext;
import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/5/29 1:34
 */
@UtilityClass
public class Choosers {

  private static File dir;

  /**
   * 选择图片
   *
   * @param tip 提示
   * @return 图片
   */
  public static File chooseImage(String tip) {
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(dir);
    chooser.getExtensionFilters().add(new ExtensionFilter(tip, "*.jpg", "*.png", "*.jpeg"));
    updateDir(chooser.showOpenDialog(AppContext.getInstance().getPrimaryStage()));
    return dir;
  }

  /**
   * 选择文件夹
   *
   * @return 文件夹
   */
  public static File chooseFolder() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setInitialDirectory(dir);
    updateDir(chooser.showDialog(AppContext.getInstance().getPrimaryStage()));
    return dir;
  }

  /**
   * 选择文件
   *
   * @return 文件
   */
  public static File chooseFile() {
    FileChooser chooser = new FileChooser();
    chooser.setInitialDirectory(dir);
    updateDir(chooser.showOpenDialog(AppContext.getInstance().getPrimaryStage()));
    return dir;
  }

  private static void updateDir(File file) {
    if (file != null) {
      dir = file;
    }
  }

}
