package com.unclezs.novel.app.framework.executor;

import java.util.concurrent.Callable;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/4/15 20:48
 */
@UtilityClass
public class TaskFactory {

  /**
   * 创建loading任务
   *
   * @param task 任务内容
   * @param <R>  返回值类型
   * @return 任务
   */
  public static <R> FluentTask<R> create(Callable<R> task) {
    return create(true, task);
  }

  /**
   * 创建loading任务
   *
   * @param task    任务内容
   * @param <R>     返回值类型
   * @param loading 显示loading
   * @return 任务
   */
  public static <R> FluentTask<R> create(boolean loading, Callable<R> task) {
    return new FluentTask<>(loading) {
      @Override
      protected R call() throws Exception {
        return task.call();
      }
    };
  }
}
