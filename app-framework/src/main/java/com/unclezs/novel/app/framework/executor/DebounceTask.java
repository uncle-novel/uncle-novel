package com.unclezs.novel.app.framework.executor;

import java.util.concurrent.ScheduledFuture;

/**
 * 防抖任务
 *
 * @author blog.unclezs.com
 * @since 2021/6/6 20:13
 */
public class DebounceTask {

  private final Long delay;
  private final Runnable runnable;
  private final boolean fx;
  private ScheduledFuture<?> scheduledFuture;

  public DebounceTask(Runnable runnable, Long delay) {
    this(runnable, delay, true);
  }

  public DebounceTask(Runnable runnable, Long delay, boolean fx) {
    this.runnable = runnable;
    this.delay = delay;
    this.fx = fx;
  }

  public static DebounceTask build(Runnable runnable, Long delay) {
    return new DebounceTask(runnable, delay);
  }

  public static DebounceTask build(Runnable runnable, Long delay, boolean fx) {
    return new DebounceTask(runnable, delay, fx);
  }


  public void run() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    scheduledFuture = Executor.run(() -> {
      scheduledFuture = null;
      if (fx) {
        Executor.runFxAndWait(runnable);
      } else {
        Executor.run(runnable);
      }
    }, delay);
  }
}
