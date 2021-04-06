package com.unclezs.novel.app.main.util;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Freemarker模板工具
 *
 * @author blog.unclezs.com
 * @date 2020/2/26 16:25
 */
@Slf4j
@UtilityClass
public class TemplateUtil {

  private static final Configuration CONFIGURATION;

  static {
    CONFIGURATION = new Configuration(Configuration.VERSION_2_3_29);
    CONFIGURATION.setClassForTemplateLoading(TemplateUtil.class, "/templates");
    CONFIGURATION.setDefaultEncoding(StandardCharsets.UTF_8.name());
    CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    //数字格式处理不用逗号分隔 1222 -> 1222
    CONFIGURATION.setNumberFormat("0");
  }

  /**
   * 获取CONFIGURATION单例对象
   *
   * @return /
   */
  public Configuration getConfiguration() {
    return CONFIGURATION;
  }

  public static void process(Map<String, Object> model, String templateLocation, File out) {
    //文件不存在则创建
    FileUtil.touch(out);
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(out, true))) {
      Template template = CONFIGURATION.getTemplate(templateLocation);
      template.process(model, writer);
      writer.flush();
    } catch (IOException e) {
      log.error("模板不存在:/templates/{}", templateLocation, e);
    } catch (TemplateException e) {
      log.error("Freemarker渲染异常：template:{}, model:{}", templateLocation, model, e);
    }
  }
}
