package com.unclezs.novel.app.jfx.packager.subtask;

import com.unclezs.novel.app.jfx.packager.Context;
import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import org.gradle.api.Project;

/**
 * 子任务基类
 *
 * @author blog.unclezs.com
 * @date 2021/04/01 16:56
 */
public abstract class BaseSubTask {

  /**
   * 打包器
   */
  protected AbstractPackager packager;
  /**
   * 项目
   */
  protected Project project;
  /**
   * 任务名称
   */
  protected String name;

  public BaseSubTask() {
    this.packager = Context.packager;
    this.project = Context.project;
  }

  public BaseSubTask(String name) {
    this();
    this.name = name;
  }

  /**
   * 实现核心逻辑
   *
   * @return 处理结果输出
   */
  protected abstract Object run() throws Exception;

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
  @SuppressWarnings("unchecked")
  public <T> T apply() {
    Object result = null;
    if (enabled()) {
      Logger.infoIndent("开始{}...", name);
      try {
        result = run();
      } catch (Throwable ex) {
        Logger.error("{}失败！", name, ex);
        throw new RuntimeException(ex);
      }
      if (result == null) {
        Logger.infoUnIndent("{}完成！", name);
      } else {
        Logger.infoUnIndent("{}完成:{}", name, result);
      }
    }
    return (T) result;
  }
}
