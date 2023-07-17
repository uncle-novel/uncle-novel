package com.unclezs.novel.app.packager.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.unclezs.novel.app.packager.exception.PackageException;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * 命令行工具
 *
 * @author blog.unclezs.com
 * @since 2021/04/02 22:39
 */
@UtilityClass
public class ExecUtils {

  /**
   * 执行cmd
   *
   * @param args 参数
   * @return 结果
   */
  public String exec(Object... args) {
    return exec(new CmdBuilder().add(args).cmdArray());
  }

  /**
   * 执行cmd
   *
   * @param args 参数
   * @return 结果
   */
  public String exec(String... args) {
    Process process = null;
    try {
      Logger.info("执行CMD：{}", Logger.blue(ArrayUtil.join(args, CharSequenceUtil.SPACE)));
      process = RuntimeUtil.exec(args);
      String result = RuntimeUtil.getResult(process, CharsetUtil.CHARSET_UTF_8);
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new PackageException(result);
      }
      return result;
    } catch (Exception e) {
      Logger.error("执行CMD失败: \n{}", e.getMessage());
      throw new PackageException(e);
    } finally {
      if (process != null && process.isAlive()) {
        process.destroy();
      }
    }
  }

  /**
   * 创建流式CMD
   *
   * @param executable 可执行程序
   * @return CmdBuilder
   */
  public static CmdBuilder create(String executable) {
    return new CmdBuilder().executable(executable);
  }

  /**
   * 创建流式CMD
   *
   * @param executable 可执行程序
   * @return CmdBuilder
   */
  public static CmdBuilder create(File executable) {
    return create(executable.getAbsolutePath());
  }

  /**
   * cmd命令构建器
   */
  public static class CmdBuilder {

    private final StringBuilder cmd = new StringBuilder();

    /**
     * 添加可执行程序
     *
     * @param executable 可执行程序
     * @return this
     */
    public CmdBuilder executable(String executable) {
      cmd.append(executable);
      return this;
    }

    /**
     * 添加可执行程序
     *
     * @param executable 可执行程序
     * @return this
     */
    public CmdBuilder executable(File executable) {
      return executable(executable.getAbsolutePath());
    }

    /**
     * 添加参数
     *
     * @param args 参数
     * @return this
     */
    public CmdBuilder add(Object... args) {
      for (Object arg : args) {
        if (arg == null) {
          continue;
        }
        if (arg.getClass().isArray()) {
          add((Object[]) arg);
        } else if (arg instanceof File) {
          String path = ((File) arg).getAbsolutePath();
          this.cmd.append(CharSequenceUtil.SPACE).append(
              CharSequenceUtil.containsBlank(path) ? CharSequenceUtil.wrap(path, "\"") : path);
        } else {
          String argStr = arg.toString();
          argStr = CharSequenceUtil.containsBlank(argStr) ? CharSequenceUtil.wrap(argStr, "\"") : argStr;
          this.cmd.append(CharSequenceUtil.SPACE).append(argStr.trim());
        }
      }
      return this;
    }

    /**
     * 获取CMD
     *
     * @return this
     */
    public String cmd() {
      return this.cmd.toString().trim();
    }

    /**
     * 获取CMD
     *
     * @return this
     */
    public String[] cmdArray() {
      return this.cmd.toString().trim().split(CharSequenceUtil.SPACE);
    }

    /**
     * 执行CMD
     *
     * @return 响应结果
     */
    public String exec() {
      return ExecUtils.exec(cmdArray());
    }
  }
}
