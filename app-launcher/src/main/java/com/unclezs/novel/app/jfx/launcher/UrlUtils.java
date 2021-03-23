package com.unclezs.novel.app.jfx.launcher;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author blog.unclezs.com
 * @date 2021/03/24 1:21
 */
@Slf4j
@UtilityClass
public class UrlUtils {
    public static URL toUrl(String filePath) {
        try {
            return new File(filePath).toURI().toURL();
        } catch (MalformedURLException e) {
            log.error("url未找到：{}", filePath, e);
            return null;
        }
    }
}
