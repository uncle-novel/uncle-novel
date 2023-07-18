package com.unclezs.novel.app.packager.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.novel.app.packager.exception.PackageException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

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
   * 执行 cmd
   *
   * @param cmds 参数
   * @return 结果
   */
  public String exec(String... cmds) {
    return exec(null, cmds);
  }

  /**
   * 执行 cmd
   *
   * @param cmds    参数
   * @param workdir workdir
   * @return 结果
   */
  public String exec(File workdir, String... cmds) {
    Process process = null;
    try {
      Logger.info("执行CMD：{}", Logger.blue(ArrayUtil.join(cmds, CharSequenceUtil.SPACE)));
      ProcessBuilder processBuilder = new ProcessBuilder(handleCmds(cmds)).redirectErrorStream(true);
      if (FileUtil.exist(workdir)) {
        processBuilder.directory(workdir);
      }
      process = processBuilder.start();
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
   * 处理命令，多行命令原样返回，单行命令拆分处理
   *
   * @param cmds 命令
   * @return 处理后的命令
   */
  private static String[] handleCmds(String... cmds) {
    if (ArrayUtil.isEmpty(cmds)) {
      throw new NullPointerException("Command is empty !");
    }

    // 单条命令的情况
    if (1 == cmds.length) {
      final String cmd = cmds[0];
      if (StrUtil.isBlank(cmd)) {
        throw new NullPointerException("Command is blank !");
      }
      cmds = cmdSplit(cmd);
    }
    return cmds;
  }

  /**
   * 命令分割，使用空格分割，考虑双引号和单引号的情况
   *
   * @param cmd 命令，如 git commit -m 'test commit'
   * @return 分割后的命令
   */
  private static String[] cmdSplit(String cmd) {
    final List<String> cmds = new ArrayList<>();

    final int length = cmd.length();
    final Stack<Character> stack = new Stack<>();
    boolean inWrap = false;
    final StrBuilder cache = StrUtil.strBuilder();

    char c;
    for (int i = 0; i < length; i++) {
      c = cmd.charAt(i);
      switch (c) {
        case CharUtil.SINGLE_QUOTE:
        case CharUtil.DOUBLE_QUOTES:
          if (inWrap) {
            if (c == stack.peek()) {
              //结束包装
              stack.pop();
              inWrap = false;
            }
            cache.append(c);
          } else {
            stack.push(c);
            cache.append(c);
            inWrap = true;
          }
          break;
        case CharUtil.SPACE:
          if (inWrap) {
            // 处于包装内
            cache.append(c);
          } else {
            cmds.add(cache.toString());
            cache.reset();
          }
          break;
        default:
          cache.append(c);
          break;
      }
    }

    if (cache.hasContent()) {
      cmds.add(cache.toString());
    }

    return cmds.toArray(new String[0]);
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
