package com.unclezs.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置工具
 * Created by Uncle
 * 2019.08.08.
 */
public class ConfUtil {
    public static final String USE_ANALYSIS_PASTE = "useAnalysisPaste";//解析页是否使用剪贴板导入
    public static final String PROXY_HOSTNAME = "Proxy_HostName";//代理主机地址
    public static final String PROXY_PORT = "Proxy_Port";//代理端口
    public static final String CONF_VERSION = "CONF_VERSION";//配置版本号
    public static final int CURRRNT_VERSION = 1;//当前配置版本号
    static Properties pro = new Properties();
    static final String path = "./conf/setting.properties";

    static {
        try {
            initCreated();
        } catch (IOException e) {
            System.out.println("设置配置读取失败");
        }
    }

    //初始化配置信息
    private static void initCreated() throws IOException {
        File file = new File(path);
        //配置不存在
        if (!file.exists()) {
            file.createNewFile();//创建配置

        }
        //加载配置
        pro.load(new FileReader(file));
        //版本更新
        if (pro.getProperty(CONF_VERSION) == null || Integer.parseInt(pro.getProperty(CONF_VERSION)) < CURRRNT_VERSION) {
            pro.setProperty(CONF_VERSION, CURRRNT_VERSION + "");//版本更新号
            setValueIfNull(USE_ANALYSIS_PASTE,"true");//自动读取剪贴版
            setValueIfNull(PROXY_HOSTNAME, "");//代理主机
            setValueIfNull(PROXY_PORT, "");//代理端口
            pro.store(new FileWriter(file), "setting info");
        }
    }

    //读取配置信息
    public static String get(String key) {
        return pro.getProperty(key);
    }

    //保存配置信息
    public static void set(String key, String value) {
        try {
            pro.setProperty(key, value);
            pro.store(new FileWriter(path), "setting info");
        } catch (IOException e) {
            System.out.println("设置配置保存失败");
        }
    }

    //设置配置，如果为空
    private static void setValueIfNull(String key,String value){
        if(pro.get(key)==null){
            pro.setProperty(key,value);
        }
    }
}