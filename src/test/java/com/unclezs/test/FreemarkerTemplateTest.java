package com.unclezs.test;

import com.unclezs.utils.TemplateUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author blog.unclezs.com
 * @date 2020/12/10 11:27 上午
 */
@Slf4j
public class FreemarkerTemplateTest {
    private Configuration configuration;

    @Before
    public void initConfiguration() {
        configuration = TemplateUtil.getConfiguration();
    }

    /**
     * 针对 freemarker的数字国际化处理 把 1222变成了 1，222
     * 改为 1222 -> 1222
     * <p>
     * https://support.qq.com/products/169599/post/160750718659023067/
     *
     * @throws Exception /
     */
    @Test
    public void testNumberFormat() throws Exception {
        //log.info("number_format:{}", configuration.getNumberFormat());
        Template template = configuration.getTemplate("numberFormat.ftl");
        Map<String, Object> map = new HashMap<>();
        map.put("item", 1222);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        template.process(map, writer);
        //log.info("默认渲染后结果：{}", out.toString());
        Assert.assertTrue(out.toString().contains("1,222"));
        writer.close();
        out = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(out);
        configuration.setNumberFormat("0");
        template.process(map, writer);
        //log.info("设置后渲染后结果：{}", out.toString());
        Assert.assertTrue(out.toString().contains("1222"));
    }

    /**
     * 渲染到文件
     *
     * @throws IOException /
     */
    @Test
    public void testRenderToFile() throws IOException {
        Template template = configuration.getTemplate("file.ftl");
        Map<String, String> map = new HashMap<>(1);
        map.put("name", "uncle");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File("file.tmp")))) {
            template.process(map, writer);
        } catch (TemplateException e) {
            log.error("渲染错误", e);
        }
    }
}
