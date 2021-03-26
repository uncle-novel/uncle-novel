package com.unclezs.novel.app.jfx.launcher;

import javafx.application.Platform;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/03/26 23:22
 */
@UtilityClass
public class FxUtils {

  public static void runFx(Runnable runnable) {
    if (Platform.isFxApplicationThread()) {
      runnable.run();
    } else {
      Platform.runLater(runnable);
    }
  }
}
