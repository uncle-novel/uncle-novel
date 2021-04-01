package com.unclezs.novel.app.jfx.packager;

import com.unclezs.novel.app.jfx.packager.action.CreateTar;
import com.unclezs.novel.app.jfx.packager.action.CreateZip;
import com.unclezs.novel.app.jfx.packager.action.windows.CreateWindowsExe;
import com.unclezs.novel.app.jfx.packager.packager.AbstractPackager;
import java.io.File;
import lombok.experimental.UtilityClass;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Gradle打包 构建工具上下文
 *
 * @author blog.unclezs.com
 * @date 2021/3/28 23:07
 */
@UtilityClass
public class Context {

  public static Project project;
  public static AbstractPackager packager;

  public static Logger getLogger() {
    return project.getLogger();
  }

  public static File getRootDir() {
    return project.getRootDir();
  }

  public static File createTar(AbstractPackager packager) throws Exception {
    return new CreateTar().apply(packager);
  }

  public static File createZip(AbstractPackager packager) throws Exception {
    return new CreateZip().apply(packager);
  }

  public static File createExe(AbstractPackager packager) throws Exception {
    return new CreateWindowsExe().apply(packager);
  }
}
