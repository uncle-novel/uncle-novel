package com.unclezs.novel.app.main.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.SerializationUtils;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.main.manager.RuleManager;
import com.unclezs.novel.app.main.model.BookBundle;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.ui.home.views.AnalysisDownloadView;
import com.unclezs.novel.app.main.ui.home.views.AudioBookShelfView;
import com.unclezs.novel.app.main.ui.home.views.DownloadManagerView;
import com.unclezs.novel.app.main.ui.home.views.FictionBookshelfView;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author blog.unclezs.com
 * @date 2021/5/7 14:19
 */
@Slf4j
@UtilityClass
public class BookHelper {

  public static final String COVER_NAME = "cover.jpeg";
  public static final String MANIFEST = "manifest";

  /**
   * 封面下载
   *
   * @param cover    封面
   * @param referer  防盗链
   * @param outDir   输出目录
   * @param callback 完成回调
   */
  public static void downloadCover(String cover, String referer, File outDir, Consumer<String> callback) {
    if (cover == null) {
      return;
    }
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
    File ruleCache = FileUtil.file(outDir, MANIFEST);
    FileUtil.writeUtf8String(GsonUtils.toJson(bookCache), ruleCache);
  }

  /**
   * 缓存书籍
   *
   * @param outDir 输出目录
   */
  public static BookCache loadCache(File outDir) {
    File chapterCache = FileUtil.file(outDir, MANIFEST);
    if (chapterCache.exists()) {
      return GsonUtils.parse(FileUtil.readUtf8String(chapterCache), BookCache.class);
    }
    return new BookCache();
  }

  /**
   * 提交解析
   *
   * @param novel 小说
   */
  public static void submitAnalysis(Novel novel) {
    SidebarNavigateBundle bundle = new SidebarNavigateBundle().put(AnalysisDownloadView.BUNDLE_KEY_NOVEL_INFO, novel);
    AppContext.getView(AnalysisDownloadView.class).getNavigation().navigate(AnalysisDownloadView.class, bundle);
  }

  /**
   * 提交下载
   *
   * @param novel 小说
   * @param rule  规则
   */
  public static void submitDownload(Novel novel, AnalyzerRule rule, List<Chapter> toc) {
    if (rule == null) {
      rule = RuleManager.getOrDefault(novel.getUrl());
    }
    BookBundle bookBundle = new BookBundle(novel, rule);
    // 如果章节存在
    if (CollUtil.isNotEmpty(toc)) {
      bookBundle.getNovel().setChapters(SerializationUtils.deepClone(toc));
    }
    SidebarNavigateBundle bundle = new SidebarNavigateBundle().put(DownloadManagerView.BUNDLE_DOWNLOAD_KEY, bookBundle);
    AppContext.getView(DownloadManagerView.class).getNavigation().navigate(DownloadManagerView.class, bundle);
  }

  /**
   * 加入书架
   *
   * @param audio        是否为有声
   * @param novel        小说
   * @param analyzerRule 规则
   * @param onSuccess    加入书架成功回调
   */
  public static void addBookShelf(boolean audio, Novel novel, AnalyzerRule analyzerRule, Runnable onSuccess) {
    if (analyzerRule == null) {
      analyzerRule = RuleManager.getOrDefault(novel.getUrl());
    }
    AnalyzerRule rule = analyzerRule;
    BookBundle bookBundle = new BookBundle(novel, rule);
    if (CollUtil.isEmpty(novel.getChapters())) {
      TaskFactory.create(() -> {
        NovelSpider spider = new NovelSpider(rule);
        return spider.toc(novel.getUrl());
      }).onSuccess(toc -> {
        bookBundle.getNovel().setChapters(toc);
        addBookShelf(audio, bookBundle, onSuccess);
      }).onFailed(error -> {
        Toast.error("加入书架失败");
        log.error("加入书架失败：{}", novel, error);
      }).start();
    } else {
      bookBundle.getNovel().setChapters(SerializationUtils.deepClone(novel.getChapters()));
      addBookShelf(audio, bookBundle, onSuccess);
    }
  }

  /**
   * 添加到书架
   *
   * @param audio      是否为有声
   * @param bookBundle 携带书籍信息的书籍包
   * @param onSuccess  成功回调
   */
  private void addBookShelf(boolean audio, BookBundle bookBundle, Runnable onSuccess) {
    if (onSuccess != null) {
      onSuccess.run();
    }
    SidebarNavigateBundle bundle = new SidebarNavigateBundle()
      .put(audio ? AudioBookShelfView.BUNDLE_BOOK_KEY : FictionBookshelfView.BUNDLE_BOOK_KEY, bookBundle);
    AppContext.getView(FictionBookshelfView.class).getNavigation().navigate(audio ? AudioBookShelfView.class : FictionBookshelfView.class, bundle);
  }
}
