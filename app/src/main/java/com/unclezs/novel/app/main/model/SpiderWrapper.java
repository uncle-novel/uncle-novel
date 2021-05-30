package com.unclezs.novel.app.main.model;

import com.unclezs.novel.analyzer.spider.Spider;
import com.unclezs.novel.analyzer.spider.pipline.MediaFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.TxtPipeline;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.config.DownloadConfig;
import com.unclezs.novel.app.main.pipeline.EbookPipeline;
import com.unclezs.novel.app.main.ui.home.views.DownloadManagerView;
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
  public static final int WAIT_RUN = 1001;
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
   * 当前状态
   */
  private ObjectProperty<Integer> state;
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
  private transient Consumer<SpiderWrapper> onSucceed;

  /**
   * 无参构造，json反序列化时使用
   */
  public SpiderWrapper() {
    this.progress = new SimpleObjectProperty<>();
    this.progressText = new SimpleObjectProperty<>();
    this.errorCount = new SimpleObjectProperty<>();
    this.state = new SimpleObjectProperty<>(WAIT_RUN);
  }

  /**
   * 首次创建调用
   *
   * @param spider 爬虫
   */
  public SpiderWrapper(Spider spider, Consumer<SpiderWrapper> onSucceed) {
    this();
    this.spider = spider;
    this.onSucceed = onSucceed;
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
    progressText.set(spider.progressText());
    errorCount.set(spider.errorCount());
    this.name = spider.getNovel().getTitle();
    // 事件监听
    spider.setProgressChangeHandler((numberProgress, textProgress) -> {
      Executor.runFx(() -> {
        progress.set(numberProgress);
        progressText.set(textProgress);
        errorCount.set(spider.errorCount());
      });
    });
    // 状态切换监听
    spider.setOnStateChange(newState -> Executor.runFxAndWait(() -> {
      state.set(newState);
      switch (newState) {
        case Spider.SUCCESS:
          // 处理完成后的其他逻辑
          onSucceed.accept(this);
          checkRunTask();
          break;
        case Spider.STOPPED:
        case Spider.PAUSED:
        case Spider.COMPLETE:
          checkRunTask();
          break;
        default:
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
      spider.addPipeline(new MediaFilePipeline());
    } else {
      if (mobi || epub) {
        EbookPipeline pipeline = new EbookPipeline();
        pipeline.setEpub(epub);
        pipeline.setMobi(mobi);
        spider.addPipeline(pipeline);
      }
      if (txt) {
        TxtPipeline pipeline = new TxtPipeline();
        // 合并章节
        pipeline.setMerge(true);
        // 分卷文件
        pipeline.setDeleteVolume(!volume);
        spider.addPipeline(pipeline);
      }
    }
  }

  /**
   * 反序列化之后调用
   *
   * @param onSucceed 完成回调
   */
  public void init(Consumer<SpiderWrapper> onSucceed) {
    this.onSucceed = onSucceed;
    // 初始化事件
    init();
    // 初始化爬虫
    initSpider();
  }

  /**
   * 状态是否一致
   *
   * @param expectStates 状态
   * @return true 一致
   */
  public boolean isState(int... expectStates) {
    if (this.state == null) {
      return false;
    }
    for (int expectState : expectStates) {
      if (expectState == this.state.get()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 停止任务
   */
  public void stop() {
    this.spider.stop();
  }

  /**
   * 执行任务
   */
  public void run() {
    this.spider.runAsync();
  }

  /**
   * 执行任务,判断是否可以执行，不可执行则提交等待
   */
  public void runTask() {
    if (AppContext.getView(DownloadManagerView.class).canRunTasksNumber() > 0) {
      this.spider.runAsync();
    } else {
      this.waiting();
    }
  }

  /**
   * 重试执行
   */
  public void retry() {
    spider.resetRetryTimes();
    runTask();
  }

  /**
   * 暂停执行
   */
  public void pause() {
    // 已经完成了，不能暂停了
    if (spider.isExceed(Spider.COMPLETE)) {
      return;
    }
    this.state.set(Spider.PAUSED);
    this.spider.pause();
  }

  /**
   * 检测是否可以执行新的任务，如果可以则执行
   */
  public void checkRunTask() {
    AppContext.getView(DownloadManagerView.class).runTask();
  }

  /**
   * 等待执行
   */
  public void waiting() {
    if (isState(Spider.RUNNING)) {
      this.spider.pause();
    }
    this.state.set(WAIT_RUN);
  }

  /**
   * 忽略错误直接保存，只能是COMPLETE状态
   */
  public void save() {
    this.spider.setIgnoreError(true);
    runTask();
  }
}
