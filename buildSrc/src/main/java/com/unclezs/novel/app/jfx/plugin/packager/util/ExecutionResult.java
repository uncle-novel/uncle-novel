package com.unclezs.novel.app.jfx.plugin.packager.util;

/**
 * Command execution result
 */
public class ExecutionResult {

  private String commandLine;
  private int exitCode;
  private String output;
  private String error;

  public String getCommandLine() {
    return commandLine;
  }

  public void setCommandLine(String commandLine) {
    this.commandLine = commandLine;
  }

  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return "ExecutionResult [commandLine=" + commandLine + ", exitCode=" + exitCode + ", output="
        + output
        + ", error=" + error + "]";
  }

}
