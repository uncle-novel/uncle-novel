package com.unclezs.novel.app.jfx.launcher;

/**
 * @author blog.unclezs.com
 * @since 2021/03/19 11:58
 */
public class PluginTest {
    public void hello() {
        System.out.println("我是" + getClass().getClassLoader().getName());
    }
}
