package com.unclezs.novel.app.jfx.packager.action;

import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;
import com.unclezs.novel.app.jfx.packager.util.Logger;
import java.io.File;


/**
 * 生成基类
 *
 * @author blog.unclezs.com
 * @date 2021/3/29 0:38
 */
public abstract class ArtifactGenerator {

  private String artifactName;

  public ArtifactGenerator() {
    super();
  }

  public ArtifactGenerator(String artifactName) {
    super();
    this.artifactName = artifactName;
  }

  public boolean skip(AbstractPackager packager) {
    return false;
  }

  public String getArtifactName() {
    return artifactName;
  }

  protected abstract File doApply(AbstractPackager packager) throws Exception;

  public File apply(AbstractPackager packager) throws Exception {
    if (skip(packager)) {
      Logger.warn(getArtifactName() + " artifact generation skipped!");
      return null;
    }
    return doApply(packager);
  }

}
