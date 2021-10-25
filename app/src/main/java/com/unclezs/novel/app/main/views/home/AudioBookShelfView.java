package com.unclezs.novel.app.main.views.home;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.ChapterState;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.Icon;
import com.unclezs.novel.app.framework.components.icon.IconFont;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.FluentTask;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.DesktopUtils;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.db.beans.AudioBook;
import com.unclezs.novel.app.main.db.dao.AudioBookDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.model.BookBundle;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.util.BookHelper;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import com.unclezs.novel.app.main.util.TimeUtil;
import com.unclezs.novel.app.main.views.components.cell.AudioBookListCell;
import com.unclezs.novel.app.main.views.components.cell.TocListCell;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author blog.unclezs.com
 * @date 2021/4/25 9:40
 */
@Slf4j
@FxView(fxml = "/layout/home/audio-bookshelf.fxml")
@EqualsAndHashCode(callSuper = true)
public class AudioBookShelfView extends SidebarView<StackPane> {

  public static final String BUNDLE_BOOK_KEY = "bundle-audio-book";
  /**
   * 缓存文件位置
   */
  public static final String CACHE_FOLDER_NAME = "audio";
  public static final File CACHE_FOLDER = ResourceManager.cacheFile(CACHE_FOLDER_NAME);
  private static final String INIT_TIME = "00:00";
  private static final String PAGE_NAME = "有声书架";
  private final AudioBookDao audioBookDao = new AudioBookDao();
  /**
   * 监听器
   */
  private ProgressChangeListener progressChangeListener;
  @FXML
  private JFXProgressBar loading;
  @FXML
  private Icon playButton;
  @FXML
  private Label currentTime;
  @FXML
  private Label totalTime;
  @FXML
  private JFXSlider progress;
  @FXML
  private ContextMenu bookContextMenu;
  @FXML
  private Label titleLabel;
  @FXML
  private Label chapterLabel;
  @FXML
  private ListView<Chapter> tocListView;
  @FXML
  private ListView<AudioBook> bookListView;
  @FXML
  private JFXDrawer tocDrawer;
  @FXML
  private JFXDrawersStack drawer;
  /**
   * 当前播放的书籍
   */
  private AudioBook currentBook;
  /**
   * 媒体播放器
   */
  private MediaPlayer player;
  /**
   * 加载媒体任务
   */
  private FluentTask<Chapter> loadMediaTask = null;

  @Override
  public void onCreated() {
    // 创建cell
    bookListView.setCellFactory(param -> new AudioBookListCell(bookContextMenu, book -> loadBook(book, true)));
    bookListView.getItems().addAll(audioBookDao.selectAll());
    bookListView.getItems().addListener((ListChangeListener<AudioBook>) c -> {
      while (c.next()) {
        c.getRemoved().forEach(book -> {
          if (book == currentBook) {
            releaseResource();
          }
          audioBookDao.delete(book);
          FileUtil.del(FileUtil.file(CACHE_FOLDER, book.getId()));
        });
        c.getAddedSubList().forEach(audioBookDao::save);
      }
    });
    // 章节列表
    tocListView.setCellFactory(param -> new TocListCell());
    EventUtils.setOnMousePrimaryClick(tocListView, e -> {
      if (!tocListView.getSelectionModel().isEmpty()) {
        currentBook.setCurrentChapterIndex(tocListView.getSelectionModel().getSelectedIndex());
        drawer.toggle(tocDrawer);
        playChapter();
      }
    });
    // 初始化进度条
    initProgress();
    // 加载第一本书
    if (!bookListView.getItems().isEmpty()) {
      loadBook(bookListView.getItems().get(0), false);
      bookListView.getSelectionModel().selectFirst();
    }
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    MixPanelHelper.event(PAGE_NAME);
    BookBundle bookBundle = bundle.get(BUNDLE_BOOK_KEY);
    if (bookBundle != null) {
      AudioBook book = AudioBook.fromBookBundle(bookBundle);
      // 编号
      int order = 1;
      for (Chapter chapter : book.getToc()) {
        chapter.setOrder(order++);
      }
      cacheBook(book);
      // 封面
      BookHelper.downloadCover(book.getCover(), book.getUrl(), FileUtil.file(CACHE_FOLDER, book.getId()), cover -> {
        book.setCover(cover);
        audioBookDao.update(book);
      });
      bookListView.getItems().add(book);
    }
  }

  @Override
  public void onDestroy() {
    if (currentBook != null) {
      // 更新当前章节缓存
      cacheBook(currentBook);
      // 更新当前进度
      audioBookDao.update(currentBook);
    }
  }

  /**
   * 添加或更新一本书到书架
   *
   * @param book 书籍
   */
  public void addOrUpdateBook(AudioBook book) {
    bookListView.getItems().stream().filter(b -> Objects.equals(book.getId(), b.getId())).findFirst().ifPresent(
      bookListView.getItems()::remove);
    bookListView.getItems().add(book);
  }

  /**
   * 缓存书籍
   *
   * @param book 书籍
   */
  private void cacheBook(AudioBook book) {
    BookHelper.cache(new BookCache(book.getRule(), book.getToc()), FileUtil.file(CACHE_FOLDER, book.getId()));
  }

  /**
   * 加载目录
   *
   * @param book 目录
   */
  private void loadCache(AudioBook book) {
    BookCache bookCache = BookHelper.loadCache(FileUtil.file(CACHE_FOLDER, book.getId()));
    book.setToc(bookCache.getToc());
    book.setRule(bookCache.getRule());
  }

  /**
   * 初始化进度条
   */
  private void initProgress() {
    progress.setValueFactory(slider -> Bindings.createStringBinding(() -> {
      if (player == null) {
        return INIT_TIME;
      }
      double total = player.getTotalDuration().toSeconds();
      double current = total * slider.getValue();
      return TimeUtil.secondToTime(current) + StrUtil.SLASH + TimeUtil.secondToTime(total);
    }, progress.valueProperty()));
    progress.valueChangingProperty().addListener(e -> {
      if (!progress.isValueChanging() && player != null) {
        player.seek(player.getStopTime().multiply(progress.getValue()));
      }
    });
    progress.valueProperty().addListener(e -> {
      if (!progress.isValueChanging() && player != null) {
        Duration to = player.getStopTime().multiply(progress.getValue());
        if (Math.abs(player.getCurrentTime().subtract(to).toSeconds()) > 1) {
          player.seek(to);
        }
      }
    });
  }

  /**
   * 加载一本书
   *
   * @param book 要加载的书
   */
  private void loadBook(AudioBook book, boolean play) {
    if (book == null || currentBook == book) {
      return;
    }
    if (currentBook != null) {
      cacheBook(currentBook);
      audioBookDao.update(currentBook);
    }
    currentBook = book;
    // 如果不存在，则从文件缓存中读取
    if (CollUtil.isEmpty(book.getToc())) {
      loadCache(currentBook);
    }
    titleLabel.setText(book.getName());
    playChapter(currentBook.getCurrentProgress(), play);
  }

  /**
   * 查看有声小说目录
   */
  @FXML
  private void showToc() {
    if (currentBook == null) {
      return;
    }
    tocListView.getItems().setAll(currentBook.getToc());
    tocListView.getSelectionModel().select(currentBook.getCurrentChapterIndex());
    drawer.toggle(tocDrawer);
  }

  /**
   * 移除书架
   */
  @FXML
  private void removeBook() {
    if (bookListView.getSelectionModel().isEmpty()) {
      return;
    }
    AudioBook book = bookListView.getSelectionModel().getSelectedItem();
    bookListView.getItems().remove(book);
    log.trace("从书架移除有声小说: {}", book);
  }


  /**
   * 播放上一章节
   */
  @FXML
  private void playPrevious() {
    // 上一节
    if (player != null && currentBook != null && currentBook.getCurrentChapterIndex() > 0) {
      currentBook.setCurrentChapterIndex(currentBook.getCurrentChapterIndex() - 1);
      playChapter();
    }
  }

  /**
   * 播放下一章节
   */
  @FXML
  private void playNext() {
    // 上一节
    if (player != null && currentBook != null
      && currentBook.getCurrentChapterIndex() < currentBook.getToc().size() - 1) {
      currentBook.setCurrentChapterIndex(currentBook.getCurrentChapterIndex() + 1);
      playChapter();
    }
  }


  /**
   * 播放当前章节，并自动播放
   */
  private void playChapter() {
    playChapter(0, true);
  }

  /**
   * 播放当前章节
   *
   * @param initProgress 初始进度
   * @param play         自动播放
   */
  private void playChapter(double initProgress, boolean play) {
    if (currentBook.getToc().isEmpty()) {
      return;
    }
    Chapter chapter = currentBook.getToc().get(currentBook.getCurrentChapterIndex());
    loading.setVisible(true);
    // 设置当前章节
    this.chapterLabel.setText(chapter.getName());
    this.currentBook.setCurrentChapterName(chapter.getName());
    // 加载真实音频
    if (chapter.getContent() == null || UrlUtils.isHttpUrl(chapter.getContent())) {
      loadChapter(initProgress, play, chapter);
    } else {
      initPlayer(chapter, initProgress, play);
    }
    int next = currentBook.getCurrentChapterIndex() + 1;
    if (next < currentBook.getToc().size()) {
      loadChapter(currentBook, next);
    }
  }

  /**
   * 加载一个章节
   *
   * @param initProgress 初始进度
   * @param play         自动播放
   * @param chapter      章节
   */
  private void loadChapter(double initProgress, boolean play, Chapter chapter) {
    // 取消旧的任务
    if (loadMediaTask != null) {
      loadMediaTask.cancel();
    }
    loadMediaTask = new FluentTask<Chapter>(false) {
      @Override
      protected Chapter call() {
        loadChapter(currentBook, chapter);
        return chapter;
      }
    }.onSuccess(c -> initPlayer(c, initProgress, play))
      .onFailed(e -> {
        // 自动播放才显示弹窗
        if (play) {
          Toast.error("音频获取失败");
        }
        log.error("获取音频失败：{}", chapter, e);
        loading.setVisible(false);
      });
    loadMediaTask.start();
  }


  /**
   * 异步加载一章节（预加载）
   *
   * @param book  书籍
   * @param index 章节索引
   */
  private void loadChapter(AudioBook book, int index) {
    Chapter chapter = book.getToc().get(index);
    if (chapter.getContent() != null && !UrlUtils.isHttpUrl(chapter.getContent())) {
      return;
    }
    Executor.run(() -> loadChapter(book, chapter));
  }

  /**
   * 加载一章节
   *
   * @param book    书籍
   * @param chapter 章节
   */
  private void loadChapter(AudioBook book, Chapter chapter) {
    try {
      String mediaUrl = chapter.getContent();
      if (!UrlUtils.isHttpUrl(mediaUrl)) {
        mediaUrl = new NovelSpider(book.getRule()).content(chapter.getUrl());
        chapter.setContent(mediaUrl);
        chapter.setState(ChapterState.DOWNLOADED);
      }
      log.trace("获取到音频媒体地址：{}", mediaUrl);
    } catch (Exception e) {
      log.warn("预加载有声章节失败：{}", chapter, e);
    }
  }


  /**
   * 播放/暂停
   *
   * @param event 鼠标点击事件
   */
  @FXML
  private void play(MouseEvent event) {
    Icon playBtn = (Icon) event.getSource();
    if (IconFont.PLAY.name().equalsIgnoreCase(playBtn.getValue().toString())) {
      play();
    } else {
      pause();
    }
  }

  /**
   * 播放
   */
  private void play() {
    if (player != null && currentBook != null) {
      playButton.setValue(IconFont.PLAY_PAUSE);
      player.play();
    }
  }

  /**
   * 暂停
   */
  private void pause() {
    if (player != null && currentBook != null) {
      playButton.setValue(IconFont.PLAY);
      player.pause();
    }
  }

  /**
   * 释放当前资源
   */
  private void releaseResource() {
    if (loadMediaTask != null) {
      loadMediaTask.cancel();
    }
    if (player != null) {
      pause();
      player.dispose();
    }
    currentBook = null;
  }


  /**
   * 初始化播放器
   *
   * @param chapter      章节
   * @param initProgress 初始进度
   * @param play         是否播放
   */
  private void initPlayer(Chapter chapter, double initProgress, boolean play) {
    if (chapter.getState() != ChapterState.DOWNLOADED && StringUtils.isBlank(chapter.getUrl())) {
      return;
    }
    Media media = new Media(chapter.getUrl());
    if (player != null) {
      pause();
      player.dispose();
      // 移除监听
      player.currentTimeProperty().removeListener(progressChangeListener);
    } else {
      // 创建监听器
      progressChangeListener = new ProgressChangeListener();
    }
    player = new MediaPlayer(media);
    // 进度绑定
    player.currentTimeProperty().addListener(progressChangeListener);
    player.setOnError(() -> {
      if (play) {
        Toast.error("音频播放失败：" + player.getError().getType());
      }
      loading.setVisible(false);
    });
    player.setOnReady(() -> {
      currentTime.setText(INIT_TIME);
      totalTime.setText(TimeUtil.secondToTime(player.getStopTime().toSeconds()));
      player.seek(player.getStopTime().multiply(initProgress));
      progress.setValue(initProgress);
      if (play) {
        play();
      }
      loading.setVisible(false);
    });
    player.setOnEndOfMedia(this::playNext);
  }

  /**
   * 下载
   */
  @FXML
  private void download() {
    if (bookListView.getSelectionModel().isEmpty()) {
      return;
    }
    AudioBook book = bookListView.getSelectionModel().getSelectedItem();
    loadCache(book);
    BookBundle bundle = new BookBundle(book.toNovel(), currentBook.getRule());
    bundle.getNovel().setChapters(book.getToc());
    navigation.navigate(DownloadManagerView.class,
      new SidebarNavigateBundle().put(DownloadManagerView.BUNDLE_DOWNLOAD_KEY, bundle));
  }

  /**
   * 浏览器打开书籍地址
   */
  @FXML
  private void openBrowser() {
    if (currentBook != null) {
      DesktopUtils.openBrowse(currentBook.getUrl());
    }
  }

  /**
   * 获取有声音频链接 并且回调处理
   *
   * @param audioUrlHandler  处理函数 入参为<章节链接，有声音频链接>
   * @param onSuccessHandler 成功回调 FX线程
   * @param <T>              回调返回类型
   */
  private <T> void withAudioUrl(BiFunction<String, String, T> audioUrlHandler, Consumer<T> onSuccessHandler) {
    MultipleSelectionModel<Chapter> selectionModel = tocListView.getSelectionModel();
    if (selectionModel.isEmpty()) {
      return;
    }
    AudioBook novel = bookListView.getSelectionModel().getSelectedItem();
    Chapter chapter = selectionModel.getSelectedItem();
    String url = chapter.getUrl();
    NovelSpider spider = new NovelSpider(novel.getRule());
    TaskFactory.create(() -> {
        String audioUrl = spider.content(url);
        return audioUrlHandler.apply(url, audioUrl);
      }).onSuccess(onSuccessHandler)
      .onFailed(e -> Toast.error("获取音频失败"))
      .start();
  }

  /**
   * 检测音频有效
   */
  @FXML
  private void checkAudioEffective() {
    withAudioUrl((chapterUrl, audioUrl) -> {
      AtomicBoolean validate = new AtomicBoolean(false);
      try {
        RequestParams params = RequestParams.create(audioUrl);
        params.addHeader(RequestParams.REFERER, chapterUrl);
        validate.set(Http.validate(params));
      } catch (Exception e) {
        log.warn("音频检测失败: 章节：{} 音频:{}", chapterUrl, audioUrl, e);
      }
      return validate.get();
    }, validate -> {
      if (Boolean.TRUE.equals(validate)) {
        Toast.success("音频有效");
      } else {
        Toast.error("音频无效");
      }
    });
  }

  /**
   * 浏览器打开
   */
  @FXML
  private void openChapterLinkBrowser() {
    String url = tocListView.getSelectionModel().getSelectedItem().getUrl();
    if (UrlUtils.isHttpUrl(url)) {
      DesktopUtils.openBrowse(url);
    }
  }

  /**
   * 复制音频链接
   */
  @FXML
  private void copyAudioLink() {
    withAudioUrl((chapterUrl, audioUrl) -> audioUrl, audioUrl -> {
      DesktopUtils.copy(audioUrl);
      Toast.success("复制成功");
    });
  }

  /**
   * 播放进度改变监听
   */
  private class ProgressChangeListener implements InvalidationListener {

    @Override
    public void invalidated(Observable observable) {
      if (player.getCurrentTime().lessThanOrEqualTo(player.getStopTime())) {
        double current = player.getCurrentTime().toSeconds();
        double total = player.getStopTime().toSeconds();
        currentTime.setText(TimeUtil.secondToTime(current));
        double to = total * progress.getValue();
        if (Math.abs(current - to) < 1) {
          progress.setValue(current / total);
        }
        // 更新进度
        currentBook.setCurrentProgress(progress.getValue());
      }
    }
  }
}
