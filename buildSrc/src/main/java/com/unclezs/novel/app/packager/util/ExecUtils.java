package com.unclezs.novel.app.packager.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;

/**
 * 命令行工具
 *
 * @author blog.unclezs.com
 * @date 2021/04/02 22:39
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
    String[] cmd = buildParams(args);
    Process process = null;
    try {
      Logger.info("执行CMD：{}", Logger.blue(ArrayUtil.join(cmd, StrUtil.SPACE)));
      process = RuntimeUtil.exec(cmd);
      String result = RuntimeUtil.getResult(process, Charset.defaultCharset());
      if (process.exitValue() != 0) {
        throw new RuntimeException(result);
      }
      return result;
    } catch (Exception e) {
      Logger.error("执行CMD失败: \n{}", e.getMessage());
      throw new RuntimeException(e);
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
   * 构建CMD参数
   *
   * @param args 参数
   * @return 参数字符串
   */
  private static String[] buildParams(Object... args) {
    String[] params = new String[args.length];
    params[0] = args[0].toString();
    for (int i = 1; i < args.length; i++) {
      Object arg = args[i];
      if (arg instanceof File) {
        String path = ((File) arg).getAbsolutePath();
        params[i] = StrUtil.wrap(path, "\"");
      } else {
        params[i] = arg.toString();
      }
    }
    return params;
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
     * @param option 参数
     * @return this
     */
    public CmdBuilder add(String option) {
      if (option == null) {
        return this;
      }
      this.cmd.append(" ").append(option);
      return this;
    }

    /**
     * 添加参数
     *
     * @param option 选项
     * @return this
     */
    public CmdBuilder add(File option) {
      if (option == null) {
        return this;
      }
      return add(option.getAbsolutePath());
    }

    /**
     * 添加参数
     *
     * @param option 选项
     * @param param  参数
     * @return this
     */
    public CmdBuilder add(String option, String param) {
      this.cmd.append(" ").append(option).append(" ").append("\"").append(param).append("\"");
      return this;
    }

    /**
     * 添加参数
     *
     * @param option 选项
     * @param param  参数
     * @return this
     */
    public CmdBuilder add(String option, File param) {
      return add(option, param.getAbsolutePath());
    }

    /**
     * 添加全部参数
     *
     * @param options 选项
     * @return this
     */
    public CmdBuilder addAll(String... options) {
      for (String option : options) {
        add(option);
      }
      return this;
    }

    /**
     * 获取CMD
     *
     * @return this
     */
    public String get() {
      return this.cmd.toString();
    }

    /**
     * 执行CMD
     *
     * @return 响应结果
     */
    public String exec() {
      return ExecUtils.exec(this.get());
    }
  }
}
