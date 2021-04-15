package com.unclezs.novel.app.framework.executor;

import cn.hutool.core.thread.ThreadUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/4/15 23:39
 */
@UtilityClass
public class Executor {

  private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = ThreadUtil.createScheduledExecutor(1);
  private static final ExecutorService EXECUTOR_SERVICE = ThreadUtil.newExecutor(0);

  public static void execute(Runnable runnable) {
    EXECUTOR_SERVICE.execute(runnable);
  }

  public static void execute(Runnable runnable, long delay) {
    SCHEDULED_THREAD_POOL_EXECUTOR.schedule(runnable, delay, TimeUnit.MILLISECONDS);
  }
}
