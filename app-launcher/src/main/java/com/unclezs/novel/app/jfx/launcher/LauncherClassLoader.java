package com.unclezs.novel.app.jfx.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author blog.unclezs.com
 * @since 2021/03/19 11:43
 */
public class LauncherClassLoader extends URLClassLoader {
    public LauncherClassLoader(ClassLoader parent) {
        super(buildClasspath(System.getProperty("java.class.path")), parent);
    }

    public LauncherClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    void addUrls(List<URL> urls) {
        for (URL url : urls) {
            this.addURL(url);
        }
    }

    private static URL[] buildClasspath(String classpath) {
        return Arrays.stream(classpath.split(File.pathSeparator)).map(u -> {
            try {
                return new File(u).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).toArray(URL[]::new);
    }
}
