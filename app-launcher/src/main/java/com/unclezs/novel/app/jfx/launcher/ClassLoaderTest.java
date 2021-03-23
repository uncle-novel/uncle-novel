package com.unclezs.novel.app.jfx.launcher;

import java.io.File;
import java.util.Arrays;

/**
 * @author blog.unclezs.com
 * @since 2021/03/19 11:45
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws Exception {
        String classpath = System.getProperty("java.class.path");
        System.out.println(classpath);
        System.out.println(Arrays.toString(classpath.split(File.pathSeparator)));
    }

    public static void test() throws Exception {
//        LauncherClassLoader launcherClassLoader = new LauncherClassLoader();
//        Class<?> loadClass = launcherClassLoader.loadClass("/Users/zhanghongguo/coder/self-coder/uncle-novel-jfx/unclezs/PluginTest.class");
//        Method main = loadClass.getDeclaredMethod("main", String[].class);
//        main.invoke(null, (Object) new String[0]);
    }
}
