package com.unclezs.gui.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.downloader.AudioDownloader;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.AudioBookNode;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.AudioBookMapper;
import com.unclezs.model.AudioBook;
import com.unclezs.model.Setting;
import com.unclezs.utils.MybatisUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.TimeUtil;
import com.unclezs.utils.UrlUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 有声书架
 *
 * @author unclezs.com
 * @date 2019.07.08 23:56
 */
@Slf4j
@FXController("audio_bookshelf")
public class AudioBookSelfController implements LifeCycleFxController {
    /**
     * 书架列表数据
     */
    private static ObservableList<AudioBookNode> bookList = FXCollections.observableArrayList();
    private static MediaPlayer mediaPlayer;
    public JFXListView<AudioBookNode> bookListView;
    public JFXDrawer catalogView;
    public JFXDrawersStack root;
    public ListView<Label> catalogListView;
    public ToggleButton playBtn;
    public Glyph playIcon;
    public JFXSlider progress;
    public Label chapterBox, titleBox, progressText;
    public JFXSpinner chapterLoading;
    /**
     * 当前播放的书
     */
    public AudioBook book;
    private AudioNovelSpider spider = new AudioNovelSpider();
    private int currentBookIndex = -1;
    /**
     * 当前播放的音频索引
     */
    private int currentPlayingIndex = 0;
    /**
     * 音频加载中标志
     */
    private BooleanProperty mediaLoading = new SimpleBooleanProperty(false);
    private AtomicBoolean bookFirstLoad = new AtomicBoolean(false);
    private Task<String> loadAudioLinkTask;

    /**
     * 添加到书架
     *
     * @param book /
     */
    static void addBookToSelf(AudioBook book) {
        AbstractLoadingTask<AudioBookNode> task = new AbstractLoadingTask<AudioBookNode>() {
            @Override
            protected AudioBookNode call() throws IOException {
                String cover = ApplicationUtil.saveImage(book.getCover(), book.getTitle());
                book.setCover(cover);
                MybatisUtil.execute(AudioBookMapper.class, mapper -> mapper.insert(book));
                return new AudioBookNode(book);
            }
        };
        task.setSuccessHandler(e -> {
            bookList.add(task.getValue());
            ToastUtil.success("加入书架成功");
        });
        ThreadUtil.execute(task);
    }

    @Override
    public void initialize() {
        bookListView.setItems(bookList);
        initEventHandler();
        loadBooks();
    }

    @Override
    public void onDestroyed() {
        saveInfo();
    }

    /**
     * 初始化事件
     */
    private void initEventHandler() {
        //章节加载loading
        chapterLoading.visibleProperty().bind(mediaLoading);
        //播放按钮变化
        playBtn.selectedProperty().addListener(e -> {
            if (playBtn.isSelected()) {
                playIcon.setIcon('\uf28b');
            } else {
                playIcon.setIcon('\uf144');
            }
        });
        //列表播放
        bookListView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                AudioBookNode bookNode = bookListView.getSelectionModel().getSelectedItem();
                if (bookNode != null) {
                    setBook(bookNode.getInfo(), true);
                }
            }
        });
        catalogListView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                int index = catalogListView.getSelectionModel().getSelectedIndex();
                catalogView.close();
                initPlayer(index);
            }
        });
    }

    /**
     * 加载书架的书
     */
    private void loadBooks() {
        long startTime = System.currentTimeMillis();
        Task<List<AudioBookNode>> task = new Task<List<AudioBookNode>>() {
            @Override
            protected List<AudioBookNode> call() {
                List<AudioBookNode> bookNodes = MybatisUtil.execute(AudioBookMapper.class, mapper -> mapper.selectList(
                    Wrappers.<AudioBook>lambdaQuery().orderByDesc(AudioBook::getUpdateTime))).stream().map(
                    AudioBookNode::new).collect(Collectors.toList());
                if (bookNodes.size() > 0) {
                    book = bookNodes.get(0).getInfo();
                }
                return bookNodes;
            }
        };
        task.setOnSucceeded(e -> {
            bookList.setAll(task.getValue());
            if (book != null) {
                bookListView.getSelectionModel().select(0);
                setBook(book, false);
            }
            log.info("有声书架初始化完成,耗时：{}ms", System.currentTimeMillis() - startTime);
        });
        ThreadUtil.execute(task);
    }

    /**
     * 显示目录
     */
    public void showCatalog() {
        root.toggle(catalogView);
        if (book != null && book.getChapters() != null) {
            //重复打开不再重复渲染
            if (catalogListView.getItems().size() > 0 && book.getChapters().size() > 0) {
                if (book.getChapters().get(0).getTitle().equals(catalogListView.getItems().get(0).getText())) {
                    catalogListView.getSelectionModel().select(this.currentPlayingIndex);
                    catalogListView.scrollTo(this.currentPlayingIndex);
                    return;
                }
            }
            catalogListView.getItems().clear();
            catalogListView.getItems().add(new Label("加载章节中..."));
            Task<List<Label>> task = new Task<List<Label>>() {
                @Override
                protected List<Label> call() {
                    return book.getChapters().stream().map(e -> {
                        Label chapter = new Label(e.getTitle());
                        chapter.getStyleClass().add("font-color-theme");
                        chapter.setUserData(e.getUrl());
                        Glyph icon = new Glyph("FontAwesome", '\uf02e');
                        icon.getStyleClass().add("font-color-theme");
                        chapter.setGraphic(icon);
                        return chapter;
                    }).collect(Collectors.toList());
                }
            };
            task.setOnSucceeded(e -> {
                catalogListView.getItems().setAll(task.getValue());
                catalogListView.getSelectionModel().select(this.currentPlayingIndex);
                catalogListView.scrollTo(this.currentPlayingIndex);
            });
            ThreadUtil.execute(task);
        }
    }


    /**
     * 设置播放的书
     *
     * @param b 书信息
     */
    private void setBook(AudioBook b, boolean autoPlay) {
        if (b == null) {
            return;
        }
        if (currentBookIndex == bookListView.getSelectionModel().getSelectedIndex()) {
            return;
        }
        if (currentBookIndex != -1) {
            ThreadUtil.execute(this::saveInfo);
            bookListView.getItems().get(currentBookIndex).setLastChapter(book.getLastChapterName());
            bookListView.getItems().get(currentBookIndex).setInfo(book);
        }
        currentBookIndex = bookListView.getSelectionModel().getSelectedIndex();
        book = b;
        AbstractLoadingTask task = new AbstractLoadingTask() {
            @Override
            protected Object call() {
                //没有就抓取
                if (book.getChapters() == null) {
                    try {
                        book.setChapters(spider.getChapters(book.getUrl()));
                    } catch (IOException e) {
                        log.error("有声目录获取失败:{}", e.getMessage());
                    }
                }
                return null;
            }
        };
        task.setSuccessHandler(e -> {
            titleBox.setText(book.getTitle() + " - " + book.getAuthor());
            bookFirstLoad.set(true);
            initPlayer(book.getLastChapterIndex(), autoPlay);
        });
        ThreadUtil.execute(task);
    }

    /**
     * 初始化播放器 加载完成自动播放
     *
     * @param index 章节索引
     */
    private void initPlayer(final int index) {
        initPlayer(index, true);
    }

    /**
     * 初始化播放器
     *
     * @param autoPlay 自动播放
     * @param index    章节索引
     */
    private void initPlayer(final int index, boolean autoPlay) {
        if (book == null || index >= book.getChapters().size() || index < 0) {
            return;
        }
        mediaLoading.set(true);
        //记录更改，关闭时保存进度
        book.setLastChapterIndex(index);
        this.currentPlayingIndex = index;
        if (loadAudioLinkTask != null) {
            loadAudioLinkTask.cancel();
        }
        loadAudioLinkTask = new Task<String>() {
            @Override
            protected String call() {
                String link = spider.getAudioLink(book.getChapters().get(index).getUrl());
                if (!isCancelled() && UrlUtil.isHttpUrl(link)) {
                    return link;
                } else {
                    throw new RuntimeException();
                }
            }
        };
        //音频地址拿到之后开始播放
        loadAudioLinkTask.setOnSucceeded(e -> {
            if (StrUtil.isEmpty(loadAudioLinkTask.getValue())) {
                ToastUtil.error("解析音频地址错误");
                return;
            }
            chapterBox.setText("正在播放：" + book.getChapters().get(index).getTitle());
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            mediaPlayer = new MediaPlayer(new Media(loadAudioLinkTask.getValue()));
            mediaPlayer.setOnReady(() -> {
                playBtn.setSelected(autoPlay);
                //恢复上次位置，仅该本书第一次加载时候
                if (bookFirstLoad.getAndSet(false)) {
                    progress.setValue(book.getLastTime());
                    mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(progress.getValue()));
                }
                //进度绑定
                mediaPlayer.currentTimeProperty().addListener(ev -> {
                    if (mediaPlayer.getCurrentTime().lessThanOrEqualTo(mediaPlayer.getTotalDuration())) {
                        progressText.setText(TimeUtil.secondToTime(mediaPlayer.getCurrentTime().toSeconds()) + "/"
                            + TimeUtil.secondToTime(mediaPlayer.getTotalDuration().toSeconds()));
                        if (!progress.isValueChanging()) {
                            progress.setValue(
                                mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
                            book.setLastTime(progress.getValue());
                        }
                    }
                });
                progress.setValueFactory(slider ->
                    Bindings.createStringBinding(
                        () -> TimeUtil.secondToTime(mediaPlayer.getTotalDuration().toSeconds() * slider.getValue()),
                        slider.valueProperty()
                    )
                );
                //进度条点击
                progress.valueProperty().addListener(ev -> {
                    if (progress.isValueChanging()) {
                        mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(progress.getValue()));
                    }
                });
                mediaLoading.set(false);
            });
            //自动下一集
            mediaPlayer.setOnEndOfMedia(() -> initPlayer(index + 1));
            //自动播放音频
            mediaPlayer.setAutoPlay(autoPlay);
            //监听音频加载失败
            mediaPlayer.setOnError(() -> {
                mediaLoading.set(false);
                ToastUtil.error("播放失败");
                log.info("音频加载失败:{}", mediaPlayer.getError().getMessage());
            });
            //停止事件
            mediaPlayer.setOnPaused(() -> {
                playBtn.setSelected(false);
                book.setLastTime(progress.getValue());
            });
        });
        ThreadUtil.execute(loadAudioLinkTask);
    }

    /**
     * 上一章
     */
    public void previous() {
        initPlayer(currentPlayingIndex - 1);
    }

    /**
     * 下一章
     */
    public void next() {
        initPlayer(currentPlayingIndex + 1);
    }


    /**
     * 保存信息入库
     */
    private void saveInfo() {
        if (book == null) {
            return;
        }
        book.setUpdateTime(DateUtil.now());
        MybatisUtil.execute(AudioBookMapper.class, mapper -> mapper.updateById(book));
    }

    /**
     * 播放按钮
     */
    public void play() {
        if (book == null) {
            playBtn.setSelected(false);
            return;
        }
        if (mediaPlayer == null) {
            initPlayer(currentPlayingIndex);
        }
        if (mediaPlayer != null) {
            if (!playBtn.isSelected()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    /**
     * 浏览器打开章节目录
     */
    public void openChapterLinkInBrowse() {
        DesktopUtil.openBrowse(catalogListView.getSelectionModel().getSelectedItem().getUserData().toString());
    }

    /**
     * 复制音频链接
     */
    public void copyAudioLink() {
        String link = catalogListView.getSelectionModel().getSelectedItem().getUserData().toString();
        AbstractLoadingTask<String> task = new AbstractLoadingTask<String>() {
            @Override
            protected String call() {
                return spider.getAudioLink(link);
            }
        };
        task.setSuccessHandler(e -> {
            DesktopUtil.copyLink(task.getValue());
        });
        task.setFailedHandler(e -> {
            ToastUtil.error("音频解析失败");
        });
        ThreadUtil.execute(task);
    }

    /**
     * 检测音频有效否
     */
    public void checkAudioLink() {
        String link = catalogListView.getSelectionModel().getSelectedItem().getUserData().toString();
        AbstractLoadingTask<Boolean> task = new AbstractLoadingTask<Boolean>() {
            @Override
            protected Boolean call() {
                String audioLink = spider.getAudioLink(link);
                if (UrlUtil.isHttpUrl(audioLink)) {
                    return RequestUtil.check(audioLink);
                }
                return false;
            }
        };
        task.setSuccessHandler(e -> {
            if (task.getValue()) {
                ToastUtil.success("音频有效");
            } else {
                ToastUtil.error("无效音频");
            }
        });
        task.setFailedHandler(e -> {
            ToastUtil.error("无效音频");
        });
        ThreadUtil.execute(task);
    }

    /**
     * 下载选中的书籍
     */
    public void downloadSelected() {
        AudioBookNode item = bookListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            AudioBook selectedBook = item.getInfo();
            //获取下载配置
            Setting setting = DataManager.application.getSetting();
            if (!FileUtil.exist(setting.getSavePath().get())) {
                ToastUtil.error("保存路径不存在");
                return;
            }
            //加载章节目录
            AbstractLoadingTask task = new AbstractLoadingTask() {
                @Override
                protected Object call() {
                    if (selectedBook.getChapters() == null) {
                        try {
                            selectedBook.setChapters(spider.getChapters(selectedBook.getUrl()));
                        } catch (IOException e) {
                            log.error("有声目录获取失败:{}", e.getMessage());
                        }
                    }
                    return null;
                }
            };
            task.setSuccessHandler(e -> {
                try {
                    AudioDownloader downloader =
                        new AudioDownloader(setting.getThreadNum().get(), setting.getDelay().get(),
                            setting.getSavePath().get(), selectedBook);
                    ContentUtil.getController(DownloadController.class).addTask(downloader);
                    ToastUtil.success("添加下载成功");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            ThreadUtil.execute(task);

        }
    }

    /**
     * 打开书籍链接在浏览器
     */
    public void openBookLinkInBrowse() {
        AudioBookNode item = bookListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            DesktopUtil.openBrowse(item.getInfo().getUrl());
        }
    }

    /**
     * 移除书架
     */
    public void removeBook() {
        AudioBookNode item = bookListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (book.equals(item.getInfo()) && mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
                book = null;
                catalogListView.getItems().clear();
                titleBox.setText("");
                chapterBox.setText("");
            }
            bookListView.getItems().remove(item);
            ThreadUtil.execute(
                () -> MybatisUtil.execute(AudioBookMapper.class, mapper -> mapper.deleteById(item.getInfo().getId())));
            ToastUtil.success("已移除");
        }
    }
}
