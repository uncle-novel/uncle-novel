package com.unclezs.novel.app.jfx.packager.action.linux;

import com.unclezs.novel.app.jfx.packager.action.ArtifactGenerator;
import com.unclezs.novel.app.jfx.packager.packager.Packager;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import java.io.File;

/**
 * Creates a RPM package file including all app folder's content only for GNU/Linux so app could be
 * easily distributed on Gradle context
 */
public class GenerateRpm extends ArtifactGenerator {

  public GenerateRpm() {
    super("RPM package");
  }

  @Override
  public boolean skip(Packager packager) {
    return !packager.getLinuxConfig().isGenerateRpm();
  }

  @Override
  protected File doApply(Packager packager) throws Exception {

    Logger.warn("Sorry! " + getArtifactName() + " generation is not yet available");

    return null;
  }

}
