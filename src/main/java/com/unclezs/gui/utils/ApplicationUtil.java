package com.unclezs.gui.utils;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.unclezs.model.ApplicationConfig;
import com.unclezs.utils.JsonUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.UrlUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 应用配置工具
 *
 * @author uncle
 * @date 2020/4/22 21:31
 */
public class ApplicationUtil {
    private static final String CONFIG_PATH = "conf/application.json";
    private static final String IMAGE_PATH = "images";
    private static final String CACHE_PATH = "cache";

    /**
     * 加载配置
     */
    public static void initConfig() {
        if (DataManager.application != null) {
            return;
        }
        System.setProperty("base.dir", com.unclezs.utils.FileUtil.getCurrentDir());
        //初始化设置
        ApplicationConfig application;
        File configFile = FileUtil.file(com.unclezs.utils.FileUtil.getCurrentDir() + CONFIG_PATH);
        if (configFile.exists()) {
            String appJson = FileUtil.readUtf8String(configFile);
            application = JSON.parseObject(appJson, ApplicationConfig.class);
        } else {
            application = new ApplicationConfig();
        }
        DataManager.application = application;
    }

    /**
     * 保存配置
     */
    public static void storeConfig() {
        String appJson = JsonUtil.toJson(DataManager.application);
        FileUtil.writeUtf8String(appJson, FileUtil.file(com.unclezs.utils.FileUtil.getCurrentDir() + CONFIG_PATH));
    }

    /**
     * 保存图片
     *
     * @param url  /  网址或者路径
     * @param name 文件名字
     * @throws IOException /
     */
    public static String saveImage(String url, String name) throws IOException {
        String path = String.format("%s%s/%s_%s.png", com.unclezs.utils.FileUtil.getCurrentDir(), IMAGE_PATH, name,
            System.currentTimeMillis());
        if (UrlUtil.isHttpUrl(url)) {
            RequestUtil.download(url, path, false);
        } else {
            FileUtil.move(FileUtil.file(url), FileUtil.file(path), true);
        }
        return path;
    }

    /**
     * 保存图片
     *
     * @param stream /  输入流
     * @param name   文件名字
     */
    public static String saveImage(InputStream stream, String name) {
        String path = String.format("%s%s/%s_%s.png", com.unclezs.utils.FileUtil.getCurrentDir(), IMAGE_PATH, name,
            System.currentTimeMillis());
        FileUtil.writeFromStream(stream, FileUtil.file(path));
        return path;
    }

    /**
     * 保存缓存文件
     *
     * @param content 内容
     * @param name    相对于缓存目录的全路径
     */
    public static String saveCache(String name, String content) {
        String path = String.format("%s%s/%s", com.unclezs.utils.FileUtil.getCurrentDir(), CACHE_PATH, name);
        FileUtil.writeUtf8String(content, FileUtil.file(path));
        return path;
    }
}
