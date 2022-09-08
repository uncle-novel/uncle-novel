package com.unclezs.gui.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXRadioButton;
import com.unclezs.crawl.TextNovelSpider;
import com.unclezs.crawl.WebNovelLoader;
import com.unclezs.downloader.NovelDownloader;
import com.unclezs.downloader.config.DownloadConfig;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.BookNode;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.model.Setting;
import com.unclezs.utils.BeanUtil;
import com.unclezs.utils.RequestUtil;
import com.unclezs.utils.TextUtil;
import com.unclezs.utils.UrlUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 解析下载页面
 *
 * @author unclezs.com
 * @date 2019.06.26 16:44
 */
@Slf4j
@FXController("analysis")
public class AnalysisController implements LifeCycleFxController {
    public JFXDrawersStack root;
    public JFXDrawer menu;
    public ToggleGroup ruleGroup;
    public WebView webView;
    public JFXButton analysisBtn, addToMark, downloadIt, configBtn;
    public JFXRadioButton chapterFilterUse, chapterSort, toZh, toSimple, startDynamic;
    public TextField chapterLinkTextField;
    public ListView<JFXCheckBox> chapterListView;
    public TextArea chapterContentTextArea;
    public TextArea chapterHeadText, cookiesText, userAgent, ads, chapterTailText, contentHeadText, contentTailText;
    /**
     * 解析配置
     */
    private AnalysisConfig config = new AnalysisConfig();
    /**
     * 爬虫
     */
    private TextNovelSpider spider = new TextNovelSpider(config);
    /**
     * shift多选开启标志
     */
    private int selectedIndex = 0;
    private WebEngine engine;
    private String html = null;
    private boolean webViewLoading = false;

    /**
     * shit 多选
     *
     * @param selectedIndex   /
     * @param chapterListView /
     */
    static void shiftSelect(int selectedIndex, ListView<JFXCheckBox> chapterListView) {
        int from = selectedIndex >= chapterListView.getItems().size() ? 0 : selectedIndex;
        int to = chapterListView.getSelectionModel().getSelectedIndex();
        if (to < selectedIndex) {
            from = to;
            to = selectedIndex;
        }
        for (int i = from; i <= to; i++) {
            JFXCheckBox item = chapterListView.getItems().get(i);
            item.setSelected(!item.isSelected());
        }
    }

    @Override
    public void initialize() {
        init();
        initAnalysisConfig();
    }

    @Override
    public void onShow(Dict data) {
        //自动导入剪贴板内容到输入框
        if (DataManager.application.getSetting().getAutoImportClipboardLink().get()) {
            String url = data.getStr("url");
            if (url != null) {
                chapterLinkTextField.setText(url);
                analysisChapter();
            }
        }
    }

    private void init() {
        //双击章节显示章节内容
        chapterListView.setOnMouseClicked(event -> {
            //shift多选
            if (event.isShiftDown()) {
                shiftSelect(selectedIndex, chapterListView);
            } else if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                showChapterContent();
            }
            selectedIndex = chapterListView.getSelectionModel().getSelectedIndex();
        });
        //ctrl+a全选
        chapterListView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A && e.isControlDown()) {
                selectAll();
            }
        });
        chapterContentTextArea.visibleProperty().bind(chapterListView.visibleProperty());
        engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener(
            (ov, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    ThreadUtil.execute(() -> {
                        ThreadUtil.sleep(5000);
                        Platform.runLater(() -> {
                            html = engine.executeScript("document.documentElement.outerHTML").toString();
                            webViewLoading = false;
                        });
                    });
                }
            });
        engine.setJavaScriptEnabled(true);
        engine.setUserAgent(RequestUtil.USER_AGENT);
        engine.setOnError(e -> webViewLoading = false);
    }

    private void getHtmlByWebView(String url) {
        webViewLoading = true;
        if (StrUtil.isNotEmpty(config.getCookies().get())) {
            try {
                Map<String, List<String>> headers = new LinkedHashMap<>();
                headers.put("Set-Cookie",
                    Arrays.stream(config.getCookies().get().split(";")).collect(Collectors.toList()));
                java.net.CookieHandler.getDefault().put(URI.create(url), headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        engine.load(url);
    }

    /**
     * 显示章节内容
     */
    public void showChapterContent() {
        JFXCheckBox item = chapterListView.getSelectionModel().getSelectedItem();
        chapterContentTextArea.setText("正在获取章节：" + item.getText());
        String url = item.getUserData().toString();
        if (config.getStartDynamic().get()) {
            getHtmlByWebView(url);
        }
        AbstractLoadingTask<String> task = new AbstractLoadingTask<String>() {
            @Override
            protected String call() throws Exception {
                if (config.getStartDynamic().get()) {
                    while (webViewLoading) {
                        ThreadUtil.sleep(100);
                    }
                    return spider.contentByHtml(html);
                }
                return spider.content(url);
            }
        };
        task.setSuccessHandler(e -> {
            String text = task.getValue();
            if (text == null) {
                ToastUtil.warning("没有匹配到正文，可以换个匹配规则试试");
                chapterContentTextArea.setText("没有匹配到正文，可以换个匹配规则试试");
            } else {
                text = TextUtil.removeTitle(text, item.getText());
                chapterContentTextArea.setText(text);
            }
        });
        ThreadUtil.execute(task);
    }

    /**
     * 全选
     */
    public void selectAll() {
        chapterListView.getItems().forEach(e -> e.setSelected(true));
    }

    /**
     * 全不选
     */
    public void selectNotAll() {
        chapterListView.getItems().forEach(e -> e.setSelected(false));
    }

    /**
     * 反选
     */
    public void selectBack() {
        chapterListView.getItems().forEach(e -> e.setSelected(!e.isSelected()));
    }

    /**
     * 解析章节目录
     */
    public void analysisChapter() {
        String url = this.chapterLinkTextField.getText().trim();
        if (!UrlUtil.isHttpUrl(url)) {
            ToastUtil.warning("请先输入正确的小说目录地址");
            return;
        }
        //清除原有的
        chapterListView.getItems().clear();
        selectedIndex = 0;
        if (config.getStartDynamic().get()) {
            getHtmlByWebView(url);
        }
        AbstractLoadingTask<ObservableList<JFXCheckBox>> task = new AbstractLoadingTask<ObservableList<JFXCheckBox>>() {
            @Override
            protected ObservableList<JFXCheckBox> call() throws Exception {
                List<Chapter> chapters;
                //爬取章节列表
                if (config.getStartDynamic().get()) {
                    while (webViewLoading) {
                        ThreadUtil.sleep(100);
                    }
                    chapters = spider.chaptersByHtml(html, url);
                } else {
                    chapters = spider.chapters(url);
                }
                return chapters.stream().map(c -> {
                    JFXCheckBox cb = new JFXCheckBox(c.getName());
                    cb.setUserData(c.getUrl());
                    cb.setSelected(true);
                    return cb;
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));
            }
        };
        task.setSuccessHandler(e -> {
            if (task.getValue().size() > 0) {
                chapterListView.setVisible(true);
            } else {
                chapterListView.setVisible(false);
                ToastUtil.success("未解析到章节,开启模拟浏览器说不定有奇效哦~");
            }
            chapterListView.setItems(task.getValue());
        });
        ThreadUtil.execute(task);
    }

    /**
     * 添加到书架
     */
    public void addToBookSelf() {
        if (chapterListView.getItems().size() == 0) {
            ToastUtil.warning("请先解析目录后再添加！");
            return;
        }
        if (config.getStartDynamic().get()) {
            ToastUtil.warning("模拟浏览器解析的小说不支持在线阅读");
            return;
        }
        AbstractLoadingTask<BookNode> task = new AbstractLoadingTask<BookNode>() {
            @Override
            protected BookNode call() {
                String name = spider.getTitle();
                String homeUrl = chapterLinkTextField.getText();
                //封面下载
                String cover = "";
                try {
                    cover = ApplicationUtil.saveImage(TextNovelSpider.getCover(name), name);
                } catch (Exception ignore) {
                }
                //保存书籍信息
                Book book = new Book(name, homeUrl, cover);
                //赛选出选中的条目
                List<Chapter> selectedChapters = new ArrayList<>();
                List<String> blackChapters = new ArrayList<>();
                chapterListView.getItems().forEach(e -> {
                    if (e.isSelected()) {
                        selectedChapters.add(new Chapter(e.getText(), e.getUserData().toString()));
                    } else {
                        blackChapters.add(e.getUserData().toString());
                    }
                });
                WebNovelLoader loader = new WebNovelLoader();
                loader.load(book, blackChapters, selectedChapters, BeanUtil.copy(config, AnalysisConfig.class));
                return new BookNode(book);
            }
        };
        task.setSuccessHandler(e -> {
            try {
                ContentUtil.getController(BookShelfController.class).addBook(task.getValue());
                ToastUtil.success("添加成功!");
            } catch (IOException ex) {
                ToastUtil.error("添加失败!");
                log.error("添加小说失败: {}", ex.getMessage());
                ex.printStackTrace();
            }
        });
        ThreadUtil.execute(task);
    }

    /**
     * 初始化解析配置
     */
    private void initAnalysisConfig() {
        ruleGroup.selectedToggleProperty().addListener(
            l -> config.getRule().set(Integer.parseInt(ruleGroup.getSelectedToggle().getUserData().toString())));
        ads.textProperty().bindBidirectional(config.getAdStr());
        userAgent.textProperty().bindBidirectional(config.getUserAgent());
        cookiesText.textProperty().bindBidirectional(config.getCookies());
        chapterHeadText.textProperty().bindBidirectional(config.getChapterHead());
        chapterTailText.textProperty().bindBidirectional(config.getChapterTail());
        contentHeadText.textProperty().bindBidirectional(config.getContentHead());
        contentTailText.textProperty().bindBidirectional(config.getContentTail());
        chapterFilterUse.selectedProperty().bindBidirectional(config.getChapterFilter());
        chapterSort.selectedProperty().bindBidirectional(config.getChapterSort());
        toZh.selectedProperty().bindBidirectional(config.getNcrToZh());
        toSimple.selectedProperty().bindBidirectional(config.getTraToSimple());
        startDynamic.selectedProperty().bindBidirectional(config.getStartDynamic());
    }

    /**
     * 自动导入剪贴板章节目录链接
     */
    public void importClipboardUrl() {
        if (DataManager.application.getSetting().getAutoImportClipboardLink().get()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String url = clipboard.getString();
            if (UrlUtil.isHttpUrl(url)) {
                chapterLinkTextField.setText(url);
                chapterLinkTextField.selectEnd();
                chapterLinkTextField.deselect();
                //清空剪贴板
                clipboard.clear();
            }
        }
    }

    /**
     * 下载本书
     */
    public void downloadBook() throws IOException {
        if (chapterListView.getItems().size() <= 1) {
            ToastUtil.warning("请先解析目录！");
            return;
        }
        Setting setting = DataManager.application.getSetting();
        if (!FileUtil.exist(setting.getSavePath().get())) {
            ToastUtil.error("保存路径不存在，请在设置里面修改");
            return;
        }
        List<Chapter> selectedChapters = chapterListView.getItems().stream().filter(CheckBox::isSelected).map(
            i -> new Chapter(i.getText(), i.getUserData().toString())).collect(Collectors.toList());
        NovelDownloader downloader =
            new NovelDownloader(selectedChapters, new DownloadConfig(DataManager.application.getSetting()),
                this.spider.getTitle(), BeanUtil.copy(config, AnalysisConfig.class));
        ContentUtil.getController(DownloadController.class).addTask(downloader);
        ToastUtil.success("添加下载任务成功！");
    }


    /**
     * 重命名章节序号
     */
    public void renameChapterNames() {
        //赛选出选中的条目
        int index = 1;
        for (int i = 0; i < chapterListView.getItems().size(); i++) {
            if (chapterListView.getItems().get(i).isSelected()) {
                String s = chapterListView.getItems().get(i).getText();
                s = TextUtil.remove(s, "[0-9]", "第.*?章");
                String newName = "第" + (index++) + "章  " + s;
                this.chapterListView.getItems().get(i).setText(newName);
            }
        }
    }

    /**
     * 显示配置项
     */
    public void showConfig() {
        root.toggle(menu);
    }

    /**
     * 保存配置
     */
    public void saveConfig() {
        spider.setConfig(config);
    }
}
