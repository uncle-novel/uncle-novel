package com.unclezs.novel.app.main.util;

import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.model.BookCache;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 14:19
 */
@UtilityClass
public class BookHelper {

  public static final String COVER_NAME = "cover.jpeg";
  private static final String BOOK_CACHE_FILE_NAME = "manifest";

  /**
   * 封面下载
   *
   * @param cover    封面
   * @param referer  防盗链
   * @param outDir   输出目录
   * @param callback 完成回调
   */
  public static void downloadCover(String cover, String referer, File outDir, Consumer<String> callback) {
    // 封面
    Executor.run(() -> {
      try {
        File coverFile = FileUtil.file(outDir, COVER_NAME);
        RequestParams params = RequestParams.create(cover);
        params.addHeader(RequestParams.REFERER, referer);
        FileUtil.writeBytes(Http.bytes(params), coverFile);
        callback.accept(coverFile.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * 缓存书籍
   *
   * @param bookCache 缓存
   * @param outDir    输出目录
   */
  public static void cache(BookCache bookCache, File outDir) {
    File ruleCache = FileUtil.file(outDir, BOOK_CACHE_FILE_NAME);
    FileUtil.writeUtf8String(GsonUtils.toJson(bookCache), ruleCache);
  }

  /**
   * 缓存书籍
   *
   * @param outDir 输出目录
   */
  public static BookCache loadCache(File outDir) {
    File chapterCache = FileUtil.file(outDir, BOOK_CACHE_FILE_NAME);
    if (chapterCache.exists()) {
      return GsonUtils.parse(FileUtil.readUtf8String(chapterCache), BookCache.class);
    }
    return new BookCache();
  }
}
