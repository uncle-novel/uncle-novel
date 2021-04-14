package com.unclezs.novel.app.packager.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.util.StringBuilderWriter;

/**
 * Velocity工具
 *
 * @author blog.unclezs.com
 * @date 2021/4/6 16:40
 */
@UtilityClass
public class VelocityUtils {

  @Setter
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
      velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
      velocityEngine.init();
    }
    return velocityEngine;
  }

  /**
   * 指定编码渲染渲染
   *
   * @param templatePath 模板路径
   * @param info         信息
   * @param charset      当作模板变量，通常用于设置标头
   * @return 结果
   */
  public static String render(String templatePath, Object info, Charset charset) {
    VelocityContext context = new VelocityContext();
    context.put("GUID", UUID.class);
    context.put("StrUtil", StrUtil.class);
    context.put("charset", charset);
    context.put("info", info);
    Template template = getVelocityEngine().getTemplate(templatePath, StandardCharsets.UTF_8.name());
    StringBuilderWriter writer = new StringBuilderWriter();
    template.merge(context, writer);
    return writer.toString();
  }

  /**
   * 指定编码渲染渲染
   *
   * @param templatePath 模板路径
   * @param info         信息
   * @return 结果
   */
  public static String render(String templatePath, Object info) {
    return render(templatePath, info, StandardCharsets.UTF_8);
  }


  /**
   * 默认UTF8
   *
   * @param templatePath 模板路径
   * @param output       输出文件
   * @param info         信息
   */
  public static void render(String templatePath, File output, Object info) {
    render(templatePath, output, info, StandardCharsets.UTF_8);
  }

  /**
   * 指定编码的渲染
   *
   * @param templatePath 模板路径
   * @param output       输出文件
   * @param info         信息
   * @param charset      编码
   */
  public static void render(String templatePath, File output, Object info, Charset charset) {
    String data = render(templatePath, info, charset);
    data = data.replace("\\r\\n", "\n").replace("\\r", "\n");
    FileUtil.writeString(data, output, charset);
  }

}
