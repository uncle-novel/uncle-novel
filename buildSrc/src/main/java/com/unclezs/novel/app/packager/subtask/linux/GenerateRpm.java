package com.unclezs.novel.app.packager.subtask.linux;

import com.unclezs.novel.app.packager.subtask.BaseSubTask;

/**
 * Creates a RPM package file including all app folder's content only for GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateRpm extends BaseSubTask {

  public GenerateRpm() {
    super("RPM package");
  }

  @Override
  protected boolean enabled() {
    return !packager.getLinuxConfig().isGenerateRpm();
  }

  @Override
  protected Object run() throws Exception {
    return null;
  }
}
