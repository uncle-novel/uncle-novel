package com.unclezs.novel.app.jfx.plugin.packager.util;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.util.StringBuilderWriter;

/**
 * Velocity utils
 */
public class VelocityUtils {

  private static File assetsDir = new File("assets");
  private static VelocityEngine velocityEngine = null;

  private static VelocityEngine getVelocityEngine() {

    if (velocityEngine == null) {

      velocityEngine = new VelocityEngine();

      // specify resource loaders to use
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,class");

      // for the loader 'file', set the FileResourceLoader as the class to use and use 'assets' directory for templates
      velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
      velocityEngine.setProperty("file.resource.loader.path", assetsDir.getAbsolutePath());

      // for the loader 'class', set the ClasspathResourceLoader as the class to use
      velocityEngine
          .setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

      velocityEngine.init();

    }

    return velocityEngine;
  }

  private static String render(String templatePath, Object info) throws Exception {
    VelocityContext context = new VelocityContext();
    context.put("features", new ArrayList<String>());
    context.put("GUID", UUID.class);
    context.put("StringUtils", StringUtils.class);
    context.put("info", info);
    Template template = getVelocityEngine().getTemplate(templatePath, "UTF-8");
    StringBuilderWriter writer = new StringBuilderWriter();
    template.merge(context, writer);
    return writer.toString();
  }

  public static void setAssetsDir(File assetsDir) {
    VelocityUtils.assetsDir = assetsDir;
  }

  public static void render(String templatePath, File output, Object info) throws Exception {
    try {
      String data = render(templatePath, info);
      data = data.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
      writeStringToFile(output, data, "UTF-8");
    } catch (IOException e) {
      throw new Exception(e.getMessage(), e);
    }
  }

}
