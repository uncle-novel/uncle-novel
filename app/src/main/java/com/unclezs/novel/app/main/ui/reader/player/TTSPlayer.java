package com.unclezs.novel.app.main.ui.reader.player;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.executor.FluentTask;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.model.config.TTSConfig;
import java.io.File;
import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Setter;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 13:45
 */
public class TTSPlayer {

  private static final File CACHE_DIR = ResourceManager.cacheFile("tts");
  @Setter
  private TTSConfig config;
  private MediaPlayer player;
  private FluentTask<File> transformTask;
  private Runnable nextPageRunner;
  private int currentAudio;

  public TTSPlayer(TTSConfig config, Runnable nextPageRunner) {
    this.config = config;
    this.nextPageRunner = nextPageRunner;
  }

  public void pause() {
    if (player != null) {
      player.pause();
    }
  }

  /**
   * 播放
   *
   * @param text 文本
   */
  public void speak(String text) {
    if (text == null) {
      return;
    }
    if (transformTask != null && transformTask.isRunning()) {
      transformTask.cancel();
    }
    if (player != null) {
      if (currentAudio == text.hashCode()) {
        player.play();
        return;
      }
      player.stop();
      player.dispose();
    }
    // 音频已经存在
    File audioFile = FileUtil.file(CACHE_DIR, text.hashCode() + ".mp3");
    if (audioFile.exists()) {
      player = new MediaPlayer(new Media(URLUtil.getURL(audioFile).toExternalForm()));
      player.play();
      initPlayer();
      return;
    }
    // 音频不存在则加载
    transformTask = TaskFactory.create(() -> toAudio(text))
      .onSuccess(audio -> {
        System.out.println("音频文件大小：" + FileUtil.size(audio));
        player = new MediaPlayer(new Media(URLUtil.getURL(audio).toExternalForm()));
        player.play();
        initPlayer();
      })
      .onFailed(e -> Toast.error("音频获取失败"));
    transformTask.start();
  }

  private void initPlayer() {
    player.setOnError(() -> {
      Toast.error("播放失败");
    });
    player.setOnEndOfMedia(nextPageRunner);
  }

  private File toAudio(String text) throws IOException {
    RequestParams params = config.getFormattedParams(text);
    if (currentAudio == text.hashCode()) {
      return FileUtil.file(CACHE_DIR, currentAudio + ".mp3");
    }
    currentAudio = text.hashCode();
    String name = currentAudio + ".mp3";
    return FileUtil.writeBytes(Http.bytes(params), FileUtil.file(CACHE_DIR, name));
  }
}
