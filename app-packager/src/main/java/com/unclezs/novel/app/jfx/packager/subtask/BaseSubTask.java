package com.unclezs.novel.app.jfx.packager.subtask;

import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;

/**
 * 子任务基类
 *
 * @author blog.unclezs.com
 * @date 2021/04/01 16:56
 */
public abstract class BaseSubTask {

  /**
   * 打包任务
   */
  protected AbstractPackager packager;

  public BaseSubTask() {
    this.packager = Context.packager;
  }

  /**
   * 实现核心逻辑
   */
  protected abstract void run();

  /**
   * 是启用执行
   *
   * @return true 启用执行
   */
  protected boolean enabled() {
    return true;
  }

  /**
   * 真正执行任务
   */
  public void apply() {
    if (enabled()) {
      run();
    }
  }
}
