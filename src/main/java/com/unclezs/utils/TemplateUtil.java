package com.unclezs.utils;

import cn.hutool.core.io.FileUtil;
import com.unclezs.constrant.Charsets;
import com.unclezs.gui.utils.ThemeUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Freemarker模板工具
 *
 * @author uncle
 * @date 2020/2/26 16:25
 */
@Slf4j
@UtilityClass
public class TemplateUtil {
    private static final Configuration CONFIGURATION;

    static {
        CONFIGURATION = new Configuration(Configuration.VERSION_2_3_29);
        CONFIGURATION.setClassForTemplateLoading(ThemeUtil.class, "/templates");
        CONFIGURATION.setDefaultEncoding(Charsets.UTF8);
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

    public void process(Map<String, Object> model, String templateLocation, File out) {
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
