package com.unclezs.novel.app.jfx.launcher;

import java.io.FileInputStream;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/03/19 11:43
 */
public class AppClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            FileInputStream inputStream = new FileInputStream(name);
            byte[] bytes = inputStream.readAllBytes();
            return defineClass("unclezs.PluginTest", bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }
}
