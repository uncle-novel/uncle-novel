package com.unclezs.ui.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.mapper.AudioBookMapper;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.ui.node.ProgressFrom;
import com.unclezs.ui.node.SearchAudioNode;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.ui.utils.LayoutUitl;
import com.unclezs.ui.utils.ToastUtil;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.ibatis.session.SqlSession;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

/*
 *有声书架
 *@author unclezs.com
 *@date 2019.07.08 23:56
 */
public class AudioBookSelfController implements Initializable {
    @FXML
    Label play, pre, next, catalog, nowTime, totalTime, playTitle;
    @FXML
    JFXSlider pb;//进度条
    @FXML
    Pane root, bookSelfPane;//根容器
    @FXML
    HBox playPane;//播放器容器
    @FXML
    JFXListView catalogList;
    @FXML
    JFXListView<SearchAudioNode> bookList;//播放列表

    public static AudioBook book;//播放的书
    public static MediaPlayer mediaPlayer;//播放器
    public static Media media = new Media("http://wting.info/asdb/fiction/xuanhuan/wanmeishijiex/w4m268ti.mp3");//音频媒体
    ImageView playImg = new ImageView("images/播放器/play.jpg");
    ImageView pauseImg = new ImageView("images/播放器/pause.jpg");
    private int playIndex = 0;//播放地址
    private static ObservableList<String> listNameData = FXCollections.observableArrayList();//目录列表数据
    private static ObservableList<String> listUrlData = FXCollections.observableArrayList();//目录列表数据
    private static ObservableList<SearchAudioNode> bookListData = FXCollections.observableArrayList();//书架列表数据
    private SearchAudioNode preBookNode;//上次选中的书
    private AudioNovelSpider spider = new AudioNovelSpider();//爬虫
    private static List<String> cacheList;//真实音频地址缓存
    private boolean loadError = false;//音频加载失败
    private boolean firstLoad = true;//音频加载失败
    private ContextMenu menu = new ContextMenu();//书架菜单
    private ContextMenu catalogMenu = new ContextMenu();//目录菜单

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autoSize();//自适应
        initMenu();//加载菜单
        initEventHandler();//初始化事件监听
        catalogList.setItems(listNameData);
        bookList.setItems(bookListData);
        pb.setValue(0);
        loadBooks();//加载数据库
    }

    //自适应
    private void autoSize() {
        LayoutUitl.bind(DataManager.content, root);
        LayoutUitl.bindWidth(root, bookSelfPane, playPane);
        bookList.prefWidthProperty().bind(bookSelfPane.widthProperty().subtract(2));
        bookSelfPane.prefHeightProperty().bind(root.heightProperty().subtract(playPane.heightProperty()));
        bookList.prefHeightProperty().bind(bookSelfPane.heightProperty());
        playPane.layoutYProperty().bind(bookSelfPane.layoutYProperty().add(bookSelfPane.heightProperty()));
        catalogList.prefHeightProperty().bind(bookSelfPane.heightProperty());
        catalogList.layoutXProperty().bind(bookSelfPane.layoutXProperty().add(bookSelfPane.widthProperty().subtract(catalogList.widthProperty().add(2))));
    }

    //初始化菜单
    private void initMenu() {
        initICons();//加载图标
        menu.getItems().addAll(new MenuItem("播放", new ImageView("images/播放器/播放小.jpg")),
                new MenuItem("移除书架", new ImageView("images/菜单页/删除.jpg")), new SeparatorMenuItem(),
                new MenuItem("浏览器打开", new ImageView("images/搜索页/在浏览器打开.jpg")));
        catalogMenu.getItems().addAll(new MenuItem("开始播放", new ImageView("images/播放器/播放小.jpg")), new MenuItem("复制音频", new ImageView("images/搜索页/复制链接.jpg")));
    }

    //初始化事件
    private void initEventHandler() {
        //播放暂停
        play.setOnMouseClicked(e -> playAudio());
        play.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                playAudio();//空格播放暂停
            }
        });
        //上一章
        pre.setOnMouseClicked(e -> preChapter());
        pre.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                preChapter();
            }
        });
        //下一章
        next.setOnMouseClicked(e -> nextChapter());
        next.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.RIGHT) {
                nextChapter();
            }
        });
        //目录
        catalog.setOnMouseClicked(e -> catalogList.setVisible(!catalogList.isVisible()));
        //播放一本书
        bookList.setOnMouseClicked(e -> {
            AudioBook selectedBook = bookList.getSelectionModel().getSelectedItem().getInfo();
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {//双击播放
                startPlay(selectedBook);
            } else if (e.getButton() == MouseButton.SECONDARY) {//右键菜单
                //跟随鼠标
                menu.setY(e.getScreenY());
                menu.setX(e.getScreenX());

                menu.show(DataManager.mainStage);
                //播放
                menu.getItems().get(0).setOnAction(ev -> {
                    startPlay(selectedBook);
                });
                //移除书架
                menu.getItems().get(1).setOnAction(ev -> {
                    bookListData.remove(bookList.getSelectionModel().getSelectedIndex());
                    new Thread(() -> {
                        SqlSession sqlSession = MybatisUtil.openSqlSession(true);
                        AudioBookMapper mapper = sqlSession.getMapper(AudioBookMapper.class);
                        mapper.deleteById(selectedBook.getId());
                        sqlSession.close();
                    }).start();
                });
                //浏览器打开
                menu.getItems().get(3).setOnAction(ev -> {
                    try {
                        Desktop.getDesktop().browse(new URI(selectedBook.getUrl()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        //点击目录播放
        catalogList.setOnMouseClicked(e -> {
            //双击目录播放
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                playIndex = catalogList.getSelectionModel().getSelectedIndex();
                if (mediaPlayer != null) {
                    stopPlay();
                }
                addPlay(playIndex);
                catalogList.setVisible(false);
            }
            //右键菜单
            if (e.getButton() == MouseButton.SECONDARY) {
                //跟随鼠标
                catalogMenu.setY(e.getScreenY());
                catalogMenu.setX(e.getScreenX());
                int index = catalogList.getSelectionModel().getSelectedIndex();
                //开始播放
                catalogMenu.getItems().get(0).setOnAction(ev -> {
                    playIndex = index;
                    if (mediaPlayer != null) {
                        stopPlay();
                    }
                    addPlay(index);
                    catalogList.setVisible(false);
                });
                //复制音频链接
                catalogMenu.getItems().get(1).setOnAction(ev -> {
                    Task<String> task = new Task<String>() {
                        @Override
                        protected String call() throws Exception {
                            return spider.getSrc(listUrlData.get(index));
                        }
                    };
                    ProgressFrom pf = new ProgressFrom(DataManager.mainStage,task);
                    pf.activateProgressBar();
                    task.setOnSucceeded(event -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        Map map = new HashMap();
                        map.put(DataFormat.PLAIN_TEXT, task.getValue());
                        clipboard.setContent(map);
                        pf.cancelProgressBar();
                        ToastUtil.toast("复制成功");
                    });

                });
                //显示菜单
                catalogMenu.show(DataManager.mainStage);
            }
        });

    }

    //停止播放
    private void stopPlay() {
        book.setLastLocation(pb.getValue());
        mediaPlayer.stop();
    }

    //设置播放的书
    public static void setBook(AudioBook book) {
        AudioBookSelfController.book = book;
        //更新目录信息
        if (book.getChapters() != null && book.getChapters().size() > 0) {
            listUrlData.clear();
            cacheList = new ArrayList<>(listUrlData.size());
            for (AudioChapter chapter : book.getChapters()) {
                listUrlData.add(chapter.getUrl());
                //更新缓存列表
                cacheList.add(null);
            }
            Platform.runLater(() -> {
                listNameData.clear();
                for (AudioChapter chapter : book.getChapters()) {
                    listNameData.add(chapter.getTitle());
                }
            });
        }
    }

    //添加播放
    private void addPlay(final int index) {
        book.setLastIndex(index);//记录更改，关闭时保存进度
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                //加载目录
                if (book.getChapters() == null || book.getChapters().size() == 0) {
                    book.setChapters(spider.getChapters(book.getUrl()));
                    setBook(book);
                }
                cacheChapter();//添加缓存任务
                if (cacheList.get(playIndex) == null) {
                    String audioURl = spider.getSrc(listUrlData.get(index));
                    return audioURl;
                } else {
                    return cacheList.get(playIndex);
                }
            }
        };
        ProgressFrom pf = new ProgressFrom(DataManager.mainStage,task);
        pf.activateProgressBar();
        //音频地址拿到之后开始播放
        task.setOnSucceeded(e -> {
            if (task.getValue().equals("")) {
                ToastUtil.toast("播放失败");
                return;
            }
            playTitle.setText(book.getTitle() + " " + listNameData.get(playIndex));
            mediaPlayer = new MediaPlayer(new Media(task.getValue()));
            mediaPlayer.setOnReady(() -> {
                //恢复上次位置
                if (firstLoad) {//仅该本书第一次加载时候
                    firstLoad = false;
                    pb.setValue(book.getLastLocation());
                    mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(pb.getValue() / 100));
                }
                //关闭loading
                pf.cancelProgressBar();
                play.setGraphic(pauseImg);//改变图标

                double endTime = mediaPlayer.getStopTime().toSeconds();
                totalTime.setText(seconds2Str(endTime));//总计时间
                //当前时间
                mediaPlayer.currentTimeProperty().addListener(ev -> {
                    //当前时间
                    double cTime = mediaPlayer.getCurrentTime().toSeconds() > endTime ? endTime : mediaPlayer.getCurrentTime().toSeconds();
                    nowTime.setText(seconds2Str(cTime));
                    pb.setValue(cTime / endTime * 100);//当前进度
                });
                //进度条点击
                pb.valueProperty().addListener(ev -> {
                    if (pb.isValueChanging()) {
                        mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(pb.getValue() / 100));
                    }
                });
                mediaPlayer.setOnEndOfMedia(() -> {
                    //完了加载下一集
                    addPlay(++playIndex);
                });
            });
            //自动播放音频
            mediaPlayer.setAutoPlay(true);
            //监听音频加载失败
            mediaPlayer.setOnError(() -> {
                ToastUtil.toast("播放失败");
                loadError = true;//标志加载失败，点击播放会重新加载
                pf.cancelProgressBar();
            });
            //停止事件
            mediaPlayer.setOnPaused(() -> {
                book.setLastLocation(pb.getValue());
                System.out.println(book.getLastLocation());
            });//更新播放位置

        });

    }

    //秒变mm:ss
    private String seconds2Str(Double seconds) {
        Integer count = seconds.intValue();
        Integer Hours = count / 3600;
        count = count % 3600;
        Integer Minutes = count / 60;
        count = count % 60;
        if (Hours == 0) {
            return Minutes.toString() + ":" + count.toString();
        }
        return Hours.toString() + ":" + Minutes.toString() + ":" + count.toString();
    }

    //初始化图标
    private void initICons() {
        play.setGraphic(playImg);
        pre.setGraphic(new ImageView("images/播放器/pre.jpg"));
        next.setGraphic(new ImageView("images/播放器/next.jpg"));
        catalog.setGraphic(new ImageView("images/播放器/catalog.jpg"));
    }

    //上一节
    private void preChapter() {
        if (playIndex != 0 && book != null) {
            stopPlay();
            addPlay(--playIndex);
        }
    }

    //下一节
    private void nextChapter() {
        if (playIndex != listNameData.size() - 1 && book != null) {
            stopPlay();
            addPlay(++playIndex);
        }
    }

    //添加到书架
    public static void addBookToSelf(AudioBook book) {
        //入库
        new Thread(() -> {
            SqlSession sqlSession = MybatisUtil.openSqlSession(true);
            AudioBookMapper mapper = sqlSession.getMapper(AudioBookMapper.class);
            book.setLastChapter(book.getChapters().get(book.getLastIndex()).getTitle());
            mapper.saveBook(book);
            Integer id = mapper.findLastKey();
            sqlSession.close();
            book.setId(id);
            SearchAudioNode node = new SearchAudioNode(book);
            bookListData.add(node);
        }).start();
    }

    //缓存后三章
    private void cacheChapter() {
        for (int i = 1; i <= 3; i++) {
            final int index = playIndex + i;
            if (cacheList.get(index) == null) {//没有缓存过就缓存
                new Thread(() -> {
                    cacheList.set(index, spider.getSrc(listUrlData.get(index)));
                }).start();
            }
        }
    }

    //保存信息入库
    public static void saveInfo() {
        System.out.println(book);
        if (book == null) {
            return;
        }
        book.setLastChapter(book.getChapters().get(book.getLastIndex()).getTitle());
        AudioBookMapper mapper = MybatisUtil.getMapper(AudioBookMapper.class);
        mapper.updateBook(book);
        MybatisUtil.getCurrentSqlSession().close();
    }

    //加载书架的书
    private void loadBooks() {
        Platform.runLater(() -> {
            Task<List<SearchAudioNode>> task = new Task<List<SearchAudioNode>>() {
                @Override
                protected List<SearchAudioNode> call() throws Exception {
                    SqlSession sqlSession = MybatisUtil.openSqlSession(true);
                    AudioBookMapper mapper = sqlSession.getMapper(AudioBookMapper.class);
                    List<AudioBook> books = mapper.findAll();
                    List<SearchAudioNode> nodes = new ArrayList<>();
                    for (AudioBook book : books) {
                        nodes.add(new SearchAudioNode(book));
                    }
                    sqlSession.close();
                    return nodes;
                }
            };
            new Thread(task).start();
            task.setOnSucceeded(e -> {
                bookListData.addAll(task.getValue());
            });
        });
    }

    //播放一本书
    private void startPlay(AudioBook selectedBook) {
        if (mediaPlayer != null)//在播放则停止
            stopPlay();
        //更新书架
        if (book != null && book.getChapters() != null) {
            book.setLastChapter(book.getChapters().get(book.getLastIndex()).getTitle());
            preBookNode.setInfo(book);
            preBookNode.getSrc().setText("上次听到：" + book.getLastChapter());//更新标签信息
            saveInfo();//更新数据库
        }
        preBookNode = bookList.getSelectionModel().getSelectedItem();
        setBook(selectedBook);//更新当书籍
        firstLoad = true;//换书则标记第一次加载
        playIndex = selectedBook.getLastIndex();//获取上次播放到的章节
        addPlay(playIndex);
    }

    //播放按钮
    private void playAudio() {
        if (book == null) {//未加载书籍
            return;
        }
        if (mediaPlayer == null) {//有媒体的时候
            addPlay(playIndex);
        }
        if (mediaPlayer != null) {//没有媒体的时候
            if (play.getGraphic().equals(playImg)) {
                if (loadError) {
                    addPlay(playIndex);
                } else {
                    mediaPlayer.play();
                }
                play.setGraphic(pauseImg);
            } else {
                mediaPlayer.pause();
                play.setGraphic(playImg);
            }
        }
    }
}
