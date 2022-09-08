package com.unclezs.gui.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXListView;
import com.unclezs.crawl.AudioNovelSpider;
import com.unclezs.downloader.AudioDownloader;
import com.unclezs.enmu.SearchKeyType;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.SearchAudioNode;
import com.unclezs.gui.components.SearchTextField;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.model.Setting;
import com.unclezs.model.rule.SearchAudioRule;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.UrlUtil;
import com.unclezs.utils.thead.RunAsyncUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 有声小说搜索控制类
 *
 * @author unclezs.com
 * @date 2020.04.26 10:27
 */
@Slf4j
@FXController("search_audio")
public class SearchAudioController implements LifeCycleFxController {
    public SearchTextField search;
    public JFXListView<SearchAudioNode> searchResultListView;
    public JFXListView<JFXCheckBox> chapterList;
    public JFXDrawersStack root;
    public JFXDrawer chapterView;
    /**
     * 章节数据
     */
    private List<AudioChapter> chapters;
    /**
     * 当前选中的位置
     */
    private int selectedIndex = 0;
    private AudioBook selectedBook;
    private AudioNovelSpider spider = new AudioNovelSpider();

    @Override
    public void initialize() {
        this.search.getBox().getItems().addAll(SearchKeyType.TITLE, SearchKeyType.AUTHOR, SearchKeyType.SPEAK);
        initEventHandler();
    }

    @Override
    public void onShow(Dict data) {
        RunAsyncUtil.run(() -> Platform.runLater(() -> this.search.getInput().requestFocus()), 150);
    }

    /**
     * 事件初始化
     */
    private void initEventHandler() {
        //点击搜索
        search.setOnSubmit(this::search);
        searchResultListView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showChapters();
            }
        });
        chapterList.setOnMouseClicked(event -> {
            if (chapterList.getItems().size() == 0) {
                return;
            }
            //shift多选
            if (event.isShiftDown()) {
                AnalysisController.shiftSelect(selectedIndex, chapterList);
            }
            selectedIndex = chapterList.getSelectionModel().getSelectedIndex();
        });
        //ctrl+a全选
        chapterList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A && e.isControlDown()) {
                selectAll();
            }
        });
    }


    /**
     * 搜索书籍
     */
    private void search(String keyword, SearchKeyType type) {
        //清空上次搜索结果
        this.searchResultListView.getItems().clear();
        AbstractLoadingTask task = new AbstractLoadingTask() {
            @Override
            protected Object call() {
                List<SearchAudioRule> rules =
                    DataManager.application.getAudioRules().stream().filter(SearchAudioRule::isEnabled).collect(
                        Collectors.toList());
                int finishedCount = 0;
                for (SearchAudioRule r : rules) {
                    //todo 应该抛出异常来处理
                    List<AudioBook> books = spider.search(keyword, r, type);
                    ObservableList<SearchAudioNode> nodes = books.stream().map(SearchAudioNode::new).collect(
                        Collectors.toCollection(FXCollections::observableArrayList));
                    Platform.runLater(() -> {
                        if (!isCancelled()) {
                            searchResultListView.getItems().addAll(nodes);
                        }
                    });
                    if (++finishedCount == 1) {
                        Platform.runLater(this::close);
                    }
                }
                return null;
            }
        };
        task.setSuccessHandler(e -> {
            if (searchResultListView.getItems().size() == 0) {
                ToastUtil.warning("啥都没有搜索到哎~");
            } else {
                ToastUtil.success("搜索完毕");
            }
            search.finished();
        });
        task.setCancelHandler(e -> search.finished());
        ThreadUtil.execute(task);
    }


    /**
     * 查看章节列表
     */
    public void showChapters() {
        if (searchResultListView.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        this.selectedBook = searchResultListView.getSelectionModel().getSelectedItem().getInfo();
        AbstractLoadingTask<ObservableList<JFXCheckBox>> task = new AbstractLoadingTask<ObservableList<JFXCheckBox>>() {
            @Override
            protected ObservableList<JFXCheckBox> call() throws Exception {
                AudioNovelSpider spider = new AudioNovelSpider();
                chapters = spider.getChapters(SearchAudioController.this.selectedBook.getUrl());
                SearchAudioController.this.selectedBook.setChapters(chapters);
                return chapters.stream().map(c -> {
                    JFXCheckBox box = new JFXCheckBox(c.getTitle());
                    box.setSelected(true);
                    return box;
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
            }
        };
        task.setSuccessHandler(cs -> {
            chapterList.setItems(task.getValue());
            root.toggle(chapterView);
        });
        ThreadUtil.execute(task);
    }


    /**
     * 加入书架
     */
    public void addToBookShelf() {
        AudioBook book = BeanUtil.toBean(this.selectedBook, AudioBook.class);
        AudioBookSelfController.addBookToSelf(book);
    }

    /**
     * 下载选中
     */
    public void downloadSelected() throws IOException {
        //获取下载配置
        Setting setting = DataManager.application.getSetting();
        //路径不为空的时候使用当前路径
        if (!FileUtil.exist(setting.getSavePath().get())) {
            ToastUtil.error("保存路径不存在！");
            return;
        }
        List<AudioChapter> chapters = new ArrayList<>(16);
        for (int i = 0; i < chapterList.getItems().size(); i++) {
            if (chapterList.getItems().get(i).isSelected()) {
                chapters.add(this.chapters.get(i));
            }
        }
        selectedBook.setChapters(chapters);
        AudioDownloader downloader =
            new AudioDownloader(setting.getThreadNum().get(), setting.getDelay().get(), setting.getSavePath().get(),
                selectedBook);
        ContentUtil.getController(DownloadController.class).addTask(downloader);
        //开启异步下载
        ToastUtil.success("添加下载成功！");
    }

    /**
     * 全选
     */
    public void selectAll() {
        chapterList.getItems().forEach(box -> box.setSelected(true));
    }

    /**
     * 反选
     */
    public void selectBack() {
        chapterList.getItems().forEach(box -> box.setSelected(!box.isSelected()));
    }

    /**
     * 全不选
     */
    public void selectNotAll() {
        chapterList.getItems().forEach(box -> box.setSelected(false));
    }

    /**
     * 复制音频链接
     */
    public void copyAudioLink() {
        AbstractLoadingTask<String> task = new AbstractLoadingTask<String>() {
            @Override
            protected String call() {
                return spider.getAudioLink(chapters.get(selectedIndex).getUrl());
            }
        };
        task.setSuccessHandler(e -> DesktopUtil.copyLink(task.getValue()));
        ThreadUtil.execute(task);
    }

    /**
     * 浏览器打开章节链接
     */
    public void openChapterLinkInBrowse() {
        DesktopUtil.openBrowse(chapters.get(selectedIndex).getUrl());
    }

    /**
     * 检测音频是否有效
     */
    public void checkAudioLink() {
        AbstractLoadingTask<Boolean> task = new AbstractLoadingTask<Boolean>() {
            @Override
            protected Boolean call() {
                String audioLink = spider.getAudioLink(chapters.get(selectedIndex).getUrl());
                if (UrlUtil.isHttpUrl(audioLink)) {
                    return RequestUtil.check(audioLink);
                }
                return false;
            }
        };
        task.setSuccessHandler(e -> {
            if (task.getValue()) {
                ToastUtil.success("音频有效！");
            } else {
                ToastUtil.error("无效音频！");
            }
        });
        ThreadUtil.execute(task);
    }

    /**
     * 在浏览器打开这本书
     */
    public void openBookLinkInBrowse() {
        String link = searchResultListView.getSelectionModel().getSelectedItem().getInfo().getUrl();
        DesktopUtil.openBrowse(link);
    }
}
