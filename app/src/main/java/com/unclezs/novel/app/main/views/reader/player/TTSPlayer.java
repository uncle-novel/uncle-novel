package com.unclezs.novel.app.main.views.reader.player;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.executor.FluentTask;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.model.config.TTSConfig;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * TTS播放器
 *
 * @author blog.unclezs.com
 * @since 2021/5/9 13:45
 */
@Slf4j
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class TTSPlayer {

  public static final long MB_10 = 1024L * 1024L * 10L;
  private static final File CACHE_DIR = ResourceManager.cacheFile("tts");
  private final Runnable nextPageRunner;
  @Getter
  private TTSConfig config;
  private MediaPlayer player;
  private FluentTask<File> transformTask;
  private Queue<String> paragraphs;
  private String originalText;
  private Task<Void> cacheTask;

  public TTSPlayer(TTSConfig config, Runnable nextPageRunner) {
    this.config = config;
    this.nextPageRunner = nextPageRunner;
  }

  /**
   * 初始化要播放的文本
   *
   * @param text 文本
   */
  public synchronized void setText(String text) {
    if (Objects.equals(originalText, text)) {
      return;
    }

    if (text == null) {
      return;
    }
    // 释放先前的
    dispose();
    originalText = text;
    // 清除缓存
    if (FileUtil.size(CACHE_DIR) > MB_10) {
      FileUtils.deleteForce(CACHE_DIR);
    }
    // 分段
    paragraphs = new LinkedList<>();
    for (String paragraph : text.split(StringUtils.LF)) {
      if (StringUtils.isNotBlank(paragraph)) {
        paragraphs.add(paragraph);
      }
    }
    deleteCache();
  }

  /**
   * 重新播放当前文本
   */
  public void speak() {
    speak(true);
  }

  /**
   * 播放每个段落
   *
   * @param first 首次加载
   */
  private void speak(final boolean first) {
    if (CollectionUtils.isEmpty(paragraphs)) {
      return;
    }
    // 音频不存在则加载
    transformTask = TaskFactory.create(false, () -> {
      String text = paragraphs.poll();
      if (text == null) {
        return null;
      }
      return toAudio(text, !first);
    }).onSuccess(audio -> {
      if (audio != null) {
        player = new MediaPlayer(new Media(URLUtil.getURL(audio).toExternalForm()));
        player.setAutoPlay(true);
        player.setOnError(() -> {
          log.warn("TTS音频播放失败：{}", config, player.getError());
          Toast.error("播放失败");
          paragraphs.clear();
        });
      }
      // 播放完成执行下一页回调
      if (paragraphs.isEmpty()) {
        player.setOnEndOfMedia(nextPageRunner);
      } else {
        if (first) {
          runCacheTask();
        }
        player.setOnEndOfMedia(() -> speak(false));
      }
    }).onFailed(e -> Toast.error("音频获取失败"));
    transformTask.start();
  }

  /**
   * 执行缓存任务
   */
  private void runCacheTask() {
    cacheTask = new Task<>() {
      @Override
      protected Void call() throws IOException {
        for (String paragraph : paragraphs) {
          if (!this.isCancelled()) {
            toAudio(paragraph, false);
            ThreadUtils.sleep(2000);
          }
        }
        return null;
      }
    };
    ThreadUtils.execute(cacheTask);
  }

  /**
   * 文本转音频
   *
   * @param text 文本
   * @return 音频文件
   * @throws IOException 转换异常
   */
  private File toAudio(String text) throws IOException {
    return toAudio(text, true);
  }

  /**
   * 文本转音频
   *
   * @param text      文本
   * @param readCache true 读取缓存
   * @return 音频文件
   * @throws IOException 转换异常
   */
  private File toAudio(String text, boolean readCache) throws IOException {
    String ttsName = text.hashCode() + ".mp3";
    File cache = FileUtil.file(CACHE_DIR, ttsName);
    if (readCache && cache.exists()) {
      return cache;
    }
    try{
      return FileUtil.writeBytes(Http.bytes(config.getFormattedParams(text)), cache);
    }catch (Exception e){
      throw e;
    }

  }

  /**
   * 删除当前所有段落缓存
   */
  private void deleteCache() {
    if (CollectionUtils.isEmpty(paragraphs)) {
      return;
    }
    for (String paragraph : paragraphs) {
      String ttsName = paragraph.hashCode() + ".mp3";
      File cache = FileUtil.file(CACHE_DIR, ttsName);
      log.debug("删除缓存：{}", cache);
      try {
        FileUtil.del(cache);
        ThreadUtils.sleep(100);
      }catch (Exception e){
        log.debug(String.format("删除缓存失败:%s\np:%s\ne:%s",ttsName,paragraph,e.getMessage()));
      }

    }
  }

  /**
   * 设置朗读引擎
   */
  public void setConfig(@NonNull TTSConfig config) {
    if (config != this.config) {
      log.info("切换TTS引擎：{}", config);
      this.config = config;
      String text = originalText;
      originalText = null;
      setText(text);
      speak();
    }
  }

  /**
   * 设置播放速度
   *
   * @param speed 速度 0-8
   */
  public void setSpeed(double speed) {
    if (player != null) {
      player.setRate(speed);
    }
  }

  /**
   * 暂停
   */
  public void pause() {
    if (player != null) {
      player.pause();
    }
  }

  /**
   * 暂停
   */
  public void play() {
    if (player != null) {
      player.play();
    }
  }

  /**
   * 释放资源
   */
  public void dispose() {
    if (player != null) {
      player.dispose();
      log.info("释放TTS资源：{}", config);
    }
    if (transformTask != null && transformTask.isRunning()) {
      transformTask.cancel();
    }
    if (cacheTask != null) {
      cacheTask.cancel();
    }
    this.originalText = null;
    if (CollectionUtils.isNotEmpty(paragraphs)) {
      this.paragraphs.clear();
    }
  }
}
