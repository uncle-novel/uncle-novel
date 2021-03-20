package com.unclezs.novel.app.jfx.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/03/19 11:45
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws Exception {
        String libDir = "G:\\coder\\self-coder\\uncle-novel-jfx\\app\\build\\libs";
        File[] files = Paths.get(libDir).toFile().listFiles((dir, name) -> name.endsWith(".jar"));
        assert files != null;
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader classLoader = new URLClassLoader(urls, systemClassLoader);
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass("com.unclezs.novel.app.jfx.app.Launcher");
        Method main = loadClass.getDeclaredMethod("main", String[].class);
        main.invoke(null, (Object) new String[0]);
    }

    public static void test() throws Exception {
        AppClassLoader appClassLoader = new AppClassLoader();
        Class<?> loadClass = appClassLoader.loadClass("/Users/zhanghongguo/coder/self-coder/uncle-novel-jfx/unclezs/PluginTest.class");
        Method main = loadClass.getDeclaredMethod("main", String[].class);
        main.invoke(null, (Object) new String[0]);
    }
}
