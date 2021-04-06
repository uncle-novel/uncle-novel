package com.unclezs.novel.app.packager;

import com.unclezs.novel.app.packager.packager.AbstractPackager;
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
}
