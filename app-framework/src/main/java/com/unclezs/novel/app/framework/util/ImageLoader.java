package com.unclezs.novel.app.framework.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.image.Image;
import lombok.experimental.UtilityClass;

/**
 * 图片加载并缓存1分钟
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 14:52
 */
@UtilityClass
public class ImageLoader {

  /**
   * 图片缓存
   */
  private static final TimedCache<String, Image> IMAGE_CACHE = CacheUtil.newTimedCache(60L * 1000L);

  /**
   * 直接读取缓存，可能为null
   *
   * @param url 图片地址
   * @return 图片
   */
  public Image get(String url) {
    return IMAGE_CACHE.get(url, true);
  }

  /**
   * 读取缓存,不存在则加载
   *
   * @param url          图片地址
   * @param defaultImage 失败时默认图片
   * @param callback     图片获取回调
   */
  public void load(String url, Image defaultImage, Consumer<Image> callback) {
    Image cacheImage = get(url);
    if (cacheImage == null) {
      if (!UrlUtils.isHttpUrl(url) && FileUtil.exist(url)) {
        url = URLUtil.getURL(FileUtil.file(url)).toString();
      } else if (!UrlUtils.isHttpUrl(url)) {
        callback.accept(defaultImage);
        return;
      }
      Image image = new Image(url, true);
      IMAGE_CACHE.put(url, image);
      String finalUrl = url;
      image.progressProperty().addListener(new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
          if (image.getProgress() == 1) {
            if (image.isError()) {
              IMAGE_CACHE.put(finalUrl, defaultImage);
            } else {
              callback.accept(image);
            }
            image.progressProperty().removeListener(this);
          }
        }
      });
    } else {
      // 如果重复获取正在加载的图片则创建新的
      if (cacheImage.getProgress() != 1) {
        Image image = new Image(url, true);
        String finalUrl = url;
        image.progressProperty().addListener(new InvalidationListener() {
          @Override
          public void invalidated(Observable observable) {
            if (image.getProgress() == 1) {
              callback.accept(get(finalUrl));
              image.progressProperty().removeListener(this);
            }
          }
        });
      } else {
        callback.accept(cacheImage);
      }
    }
  }
}
