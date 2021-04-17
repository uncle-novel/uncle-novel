package com.unclezs.novel.app.framework.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author blog.unclezs.com
 * @date 2021/4/16 11:37
 */
public class DaemonThreadFactory implements ThreadFactory {

  private static final AtomicInteger COUNTER = new AtomicInteger(0);

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setDaemon(true);
    thread.setName("executor-" + COUNTER.incrementAndGet());
    return thread;
  }
}
