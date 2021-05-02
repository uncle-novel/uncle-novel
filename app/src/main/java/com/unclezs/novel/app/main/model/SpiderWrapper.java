package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.spider.pipline.MediaFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.TxtPipeline;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.pipeline.EbookPipeline;
import java.io.Serializable;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 爬虫任务包装，适用于FX监听任务
 *
 * @author blog.unclezs.com
 * @date 2021/4/30 11:18
 */
@Getter
@Setter
public class SpiderWrapper implements Serializable {

  /**
   * 等待下载中
   */
  public static final int WAITING = 1001;
  /**
   * 转码中
   */
  public static final int TRANSCODE = 1002;
  /**
   * 下载进度
   */
  private final transient ObjectProperty<Double> progress;
  /**
   * 下载进度 文字
   */
  private final transient ObjectProperty<String> progressText;
  /**
   * 错误数量
   */
  private final transient ObjectProperty<Integer> errorCount;
  /**
   * 爬虫
   */
  private Spider spider;
  /**
   * 是否分卷下载
   */
  private boolean volume;
  private boolean mobi;
  private boolean txt;
  private boolean epub;
  /**
   * 小说名称,也是文件名称
   */
  private String name;
  /**
   * 完成时回调，反序列化后需要手动重新设置
   */
  @Setter
  private transient Consumer<SpiderWrapper> onCompleted;
  /**
   * 当前状态
   */
  private transient ObjectProperty<Integer> state;

  /**
   * 无参构造，json反序列化时使用
   */
  public SpiderWrapper() {
    this.progress = new SimpleObjectProperty<>();
    this.progressText = new SimpleObjectProperty<>();
    this.errorCount = new SimpleObjectProperty<>();
    this.state = new SimpleObjectProperty<>(WAITING);
  }

  /**
   * 首次创建调用
   *
   * @param spider 爬虫
   */
  public SpiderWrapper(Spider spider, Consumer<SpiderWrapper> onCompleted) {
    this();
    this.spider = spider;
    this.onCompleted = onCompleted;
    init();
    // 设置数据初始化
    DownloadConfig downloadConfig = SettingManager.manager().getDownload();
    this.epub = Boolean.TRUE.equals(downloadConfig.getEpub().get());
    this.mobi = Boolean.TRUE.equals(downloadConfig.getMobi().get());
    this.txt = Boolean.TRUE.equals(downloadConfig.getTxt().get());
    this.volume = Boolean.TRUE.equals(downloadConfig.getVolume().get());
    // 根据设置数据初始化爬虫
    initSpider();
  }

  /**
   * 初始化事件绑定
   */
  public void init() {
    // 初始数据
    progress.set(spider.progress());
    int total = spider.getNovel().getChapters() == null ? spider.getToc().size() : spider.getNovel().getChapters().size();
    progressText.set(String.format("%d/%d", total - spider.leftCount(), total));
    errorCount.set(spider.errorCount());
    this.name = spider.getNovel().getTitle();
    // 事件监听
    spider.progressChangeHandler((numberProgress, textProgress) -> {
      Executor.runFx(() -> {
        progress.set(numberProgress);
        progressText.set(textProgress);
        errorCount.set(spider.errorCount());
        // 转码状态
        if (numberProgress == 1) {
          state.set(TRANSCODE);
        }
      });
    });
    // 状态切换监听
    spider.onStateChange((old, newState) -> Executor.runFxAndWait(() -> {
      state.set(newState);
      if (newState == Spider.COMPLETED && onCompleted != null) {
        // 处理其他完成后逻辑
        onCompleted.accept(this);
      }
    }));
  }

  /**
   * 初始化爬虫
   */
  private void initSpider() {
    boolean isAudio = Boolean.TRUE.equals(spider.getAnalyzerRule().getAudio());
    // 下载格式
    if (isAudio) {
      spider.pipeline(new MediaFilePipeline());
    } else {
      if (mobi || epub) {
        EbookPipeline pipeline = new EbookPipeline();
        pipeline.setEpub(epub);
        pipeline.setMobi(mobi);
        spider.pipeline(pipeline);
      }
      if (txt) {
        TxtPipeline pipeline = new TxtPipeline();
        // 合并章节
        pipeline.setMerge(true);
        // 分卷文件
        pipeline.setDeleteVolume(!volume);
        spider.pipeline(pipeline);
      }
    }
  }

  /**
   * 反序列化之后调用
   *
   * @param onCompleted 完成回调
   */
  public void init(Consumer<SpiderWrapper> onCompleted) {
    this.onCompleted = onCompleted;
    // 初始化事件
    init();
    // 初始化爬虫
    initSpider();
  }

  public void stop() {
    this.spider.stop();
  }

  public void run() {
    this.spider.runAsync();
  }

  public void retry() {
    this.spider.setCurrentTimes(0);
    run();
  }

  public void pause() {
    this.spider.pause();
  }
}
