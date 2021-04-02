package com.unclezs.novel.app.jfx.packager.action;

import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;
import java.io.File;

/**
 * 创建包含openJfx的全量Jre
 *
 * @author blog.unclezs.com
 * @date 2021/04/01 16:53
 */
public class CreateFxJre extends ArtifactGenerator {

  @Override
  public boolean skip(AbstractPackager packager) {
    return false;
  }

  @Override
  protected File doApply(AbstractPackager packager) throws Exception {

    return null;
  }
}
