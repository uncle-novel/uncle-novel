package com.unclezs.config;

import cn.hutool.system.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 爬虫配置
 *
 * @author uncle
 * @date 2020.01.22 21:42
 */
public class SpiderConfig {
    /**
     * 小说下载临时目录
     */
    public static String TMP_DIR;
    private static Properties props;
    public static final Integer RETRY_COUNT = Integer.valueOf(props.getProperty("retry_count"));
    public static final Long RETRY_DELAY = Long.valueOf(props.getProperty("retry_delay"));

    static {
        props = new Properties();
        try {
            props.load(new InputStreamReader(SpiderConfig.class.getResourceAsStream("/conf/spider.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        String dir = SystemUtil.getUserInfo().getTempDir();
        if (dir.endsWith(File.separator)) {
            TMP_DIR = dir.concat("UncleNovel/");
        } else {
            TMP_DIR = dir.concat("/UncleNovel/");
        }
    }
}
