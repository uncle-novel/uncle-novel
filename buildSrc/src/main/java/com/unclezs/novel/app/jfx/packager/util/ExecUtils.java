package com.unclezs.novel.app.jfx.packager.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 22:39
 */
public class ExecUtils {

  public void execute(String... args) {
    Process process = null;
    try {
      process = RuntimeUtil.exec(args);
    } catch (Exception e) {
      Logger.warn("执行命令 {} 失败: {}", ArrayUtil.join(args, " "), e);
      e.printStackTrace();
    } finally {
      if (process != null && process.isAlive()) {
        process.destroy();
      }
    }
  }

}
