package com.unclezs.novel.app.main.util;

import cn.hutool.core.io.FileUtil;
import lombok.experimental.UtilityClass;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.StringBuilderWriter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Velocity工具
 *
 * @author blog.unclezs.com
 * @since 2021/4/6 16:40
 */
@UtilityClass
public class VelocityUtils {

  private static VelocityEngine velocityEngine = null;

  private static VelocityEngine getVelocityEngine() {
    if (velocityEngine == null) {
      velocityEngine = new VelocityEngine();
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "class");
      // 加载classpath资源
      velocityEngine.setProperty("resource.loader.class.class", ClasspathResourceLoader.class.getName());
      velocityEngine.init();
    }
    return velocityEngine;
  }

  /**
   * 指定编码渲染渲染
   *
   * @param templatePath 模板路径
   * @param data         信息
   * @return 结果
   */
  public static String render(String templatePath, Object data) {
    VelocityContext context = new VelocityContext();
    context.put("data", data);
    context.put("uuid", UUID.class);
    Template template = getVelocityEngine().getTemplate(templatePath, StandardCharsets.UTF_8.name());
    StringBuilderWriter writer = new StringBuilderWriter();
    template.merge(context, writer);
    return writer.toString();
  }


  /**
   * 指定编码的渲染
   *
   * @param templatePath 模板路径
   * @param output       输出文件
   * @param info         信息
   */
  public static void render(String templatePath, Object info, File output) {
    String data = render(templatePath, info);
    FileUtil.writeUtf8String(data, output);
  }
}
