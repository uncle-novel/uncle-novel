package com.unclezs.gui.utils;

import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.thead.CountableThreadPool;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 图片加载
 *
 * @author uncle
 * @date 2020/4/27 22:30
 */
@Slf4j
public class ImageLoader {
    private static CountableThreadPool loadImagePool;

    static {
        loadImagePool = new CountableThreadPool(5);
    }

    public static void loadImage(String imageUrl, Consumer<InputStream> loader) {
        loadImagePool.execute(() -> {
            try {
                InputStream stream = RequestUtil.stream(imageUrl);
                loader.accept(stream);
            } catch (IOException e) {
                log.info("封面图片加载失败:{}", imageUrl);
            }
        });
    }
}
