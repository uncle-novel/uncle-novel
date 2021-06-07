package com.unclezs.novel.app.framework.executor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 节流任务
 *
 * @author blog.unclezs.com
 * @date 2021/6/6 20:14
 */
public class ThrottleTask {

  private Timer timer;
  private Long delay;
  private Runnable runnable;
  private boolean needWait = false;

  public ThrottleTask(Runnable runnable, Long delay) {
    this.runnable = runnable;
    this.delay = delay;
    this.timer = new Timer();
  }

  public static ThrottleTask build(Runnable runnable, Long delay) {
    return new ThrottleTask(runnable, delay);
  }

  public void run() {
    if (!needWait) {
      needWait = true;
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          needWait = false;
          runnable.run();
        }
      }, delay);
    }
  }
}
