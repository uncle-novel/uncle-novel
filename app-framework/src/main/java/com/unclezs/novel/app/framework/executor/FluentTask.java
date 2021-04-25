package com.unclezs.novel.app.framework.executor;

import com.unclezs.novel.app.framework.components.Loading;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 * 可在执行期间显示 Loading...
 *
 * @author blog.unclezs.com
 * @date 2021/4/15 22:24
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class FluentTask<R> extends Task<R> {

  /**
   * 是否执行任务
   */
  private final boolean loadingEnabled;
  private Loading loading;
  private Runnable finallyTask;

  protected FluentTask() {
    this(true);
  }

  protected FluentTask(boolean loadingEnabled) {
    this.loadingEnabled = loadingEnabled;
    if (loadingEnabled) {
      loading = new Loading();
      loading.setOnCloseRequest(event -> this.cancel());
      //启动时显示
      super.setOnRunning(e -> loading.show());
      super.setOnSucceeded(e -> close());
      super.setOnFailed(e -> close());
      super.setOnCancelled(e -> close());
    }
  }

  /**
   * 关闭loading
   */
  private void close() {
    if (loadingEnabled && loading.isShowing()) {
      loading.hideWithAnimation();
    }
    if (finallyTask != null) {
      finallyTask.run();
    }
  }

  /**
   * 任务执行成功回调
   *
   * @param callback 回调
   * @return this
   */
  public FluentTask<R> onSuccess(Consumer<R> callback) {
    super.setOnSucceeded(e -> {
      if (isCancelled()) {
        return;
      }
      callback.accept(FluentTask.this.getValue());
      close();
    });
    return this;
  }

  /**
   * 任务失败成功回调
   *
   * @param callback 回调
   * @return this
   */
  public FluentTask<R> onFailed(Consumer<Throwable> callback) {
    super.setOnFailed(e -> {
      callback.accept(FluentTask.this.getException());
      close();
    });
    return this;
  }

  /**
   * 任务取消回调
   *
   * @param callback 回调
   * @return this
   */
  public FluentTask<R> onCanceled(Runnable callback) {
    super.setOnCancelled(e -> {
      callback.run();
      close();
    });
    return this;
  }

  /**
   * 最终执行的任务,无论成功失败
   *
   * @param callback 回调
   * @return this
   */
  public FluentTask<R> onFinally(Runnable callback) {
    this.finallyTask = callback;
    return this;
  }

  /**
   * 提交执行任务
   */
  public void start() {
    Executor.run(this);
  }

  /**
   * 延迟执行
   *
   * @param delay 延迟毫秒
   */
  public void start(long delay) {
    Executor.run(this, delay);
  }
}
