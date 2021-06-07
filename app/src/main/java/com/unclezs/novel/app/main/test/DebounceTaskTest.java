package com.unclezs.novel.app.main.test;

import com.unclezs.novel.app.framework.executor.DebounceTask;

/**
 * @author blog.unclezs.com
 * @date 2021/6/6 20:15
 */
public class DebounceTaskTest {

  public static void main(String[] args) {
    DebounceTask task = DebounceTask.build(new Runnable() {
      @Override
      public void run() {
        System.out.println("do task: " + System.currentTimeMillis());
      }
    }, 1000L, false);
    long delay = 100;
    //noinspection InfiniteLoopStatement
    while (true) {
      System.out.println("call task: " + System.currentTimeMillis());
      task.run();
      delay += 100;
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
