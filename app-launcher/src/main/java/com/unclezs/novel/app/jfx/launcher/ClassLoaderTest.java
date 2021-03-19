package com.unclezs.novel.app.jfx.launcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/03/19 11:45
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        System.getProperties().list(System.out);
    }

    public static void test() throws Exception {
        AppClassLoader appClassLoader = new AppClassLoader();
        Class<?> loadClass = appClassLoader.loadClass("/Users/zhanghongguo/coder/self-coder/uncle-novel-jfx/unclezs/PluginTest.class");
        Method main = loadClass.getDeclaredMethod("main", String[].class);
        main.invoke(null, (Object) new String[0]);
    }
}
