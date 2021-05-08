package com.unclezs.novel.app.framework.executor;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.common.concurrent.factory.DaemonThreadFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import lombok.experimental.UtilityClass;

/**
 * 执行器
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 23:39
 */
@UtilityClass
public class Executor {

  private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory("scheduled"));

  /**
   * 提交全局线程池执行
   *
   * @param runnable 任务
   */
  public static void run(Runnable runnable) {
    ThreadUtils.execute(runnable);
  }

  /**
   * 提交scheduled池执行
   *
   * @param runnable 任务
   * @param delay    延迟
   */
  public static void run(Runnable runnable, long delay) {
    SCHEDULED_THREAD_POOL_EXECUTOR.schedule(runnable, delay, TimeUnit.MILLISECONDS);
  }

  /**
   * 在fx延迟执行
   *
   * @param runnable 任务
   * @param delay    延迟
   */
  public static void runFx(Runnable runnable, long delay) {
    run(() -> runFx(runnable), delay);
  }

  /**
   * 在fx线程执行
   *
   * @param runnable 任务
   */
  public static void runFx(Runnable runnable) {
    Platform.runLater(runnable);
  }

  /**
   * 在fx线程执行任务，并且等待完成
   *
   * @param task 任务
   */
  public static void runFxAndWait(Runnable task) {
    final CountDownLatch doneLatch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        task.run();
      } finally {
        doneLatch.countDown();
      }
    });
    try {
      doneLatch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
