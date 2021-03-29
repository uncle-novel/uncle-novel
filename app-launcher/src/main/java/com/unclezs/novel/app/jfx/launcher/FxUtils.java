package com.unclezs.jfx.launcher;

import java.util.concurrent.CountDownLatch;
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

  public static void runAndWait(final Runnable r) {
    final CountDownLatch doneLatch = new CountDownLatch(1);
    runFx(() -> {
      try {
        r.run();
      } finally {
        doneLatch.countDown();
      }
    });
    try {
      doneLatch.await();
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }
}
