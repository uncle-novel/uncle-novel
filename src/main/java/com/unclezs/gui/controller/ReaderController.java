package com.unclezs.gui.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.scene.control.skin.ColorPickerSkin;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import com.unclezs.constrant.ChineseFont;
import com.unclezs.crawl.NovelLoader;
import com.unclezs.gui.app.App;
import com.unclezs.gui.app.Reader;
import com.unclezs.gui.components.AbstractLoadingTask;
import com.unclezs.gui.components.ContentNode;
import com.unclezs.gui.extra.FXController;
import com.unclezs.gui.utils.ApplicationUtil;
import com.unclezs.gui.utils.DataManager;
import com.unclezs.gui.utils.DesktopUtil;
import com.unclezs.gui.utils.ThemeUtil;
import com.unclezs.gui.utils.ToastUtil;
import com.unclezs.mapper.BookMapper;
import com.unclezs.model.Book;
import com.unclezs.model.Chapter;
import com.unclezs.utils.FileUtil;
import com.unclezs.utils.MSTTSSpeech;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.Glyph;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;


/**
 * 阅读器
 *
 * @author unclezs.com
 * @date 2019.06.22 16:09
 */
@FXController("reader")
public class ReaderController {
    public static final String EYE_COLOR = "#ceebce";
    private static final double CHANGE_PAGE_AREA_WIDTH = 0.2;
    private static final String NIGHT_COLOR = "#080C10";
    private static final String TMP_AUDIO = "tmp/r.wav";
    /**
     * 内容章节NODE数量
     */
    private static final int CACHE_PAGE = 3;
    /**
     * 第一次加载
     */
    static boolean firstLoad = true;
    public JFXDrawersStack root;
    public ListView<Chapter> catalogListView;
    public JFXDrawer catalogDrawer, settingDrawer;
    public VBox contentBox;
    public ToggleGroup colorGroup;
    public BorderPane settingBox;
    public JFXSlider fontSizeSlider, lineSpaceSlider, pageWidthSlider, chapterSlider;
    public ComboBox<String> fonts;
    public ToggleButton nightTheme, eye, speakBtn;
    public ColorPicker colorPicker;
    public MenuItem hiddenBtn;
    public ToggleGroup alignGroup;
    public ScrollPane contentScrollPane;
    private ScrollPaneSkin scrollPaneSkin;
    private Glyph plusIcon = new Glyph("FontAwesome", '\uf067');
    private StringProperty styleFont = new SimpleStringProperty();
    private StringProperty styleTileFont = new SimpleStringProperty();
    /**
     * 语音朗读
     */
    private MSTTSSpeech speech;
    private MediaPlayer player;
    /**
     * 内容加载器
     */
    private NovelLoader loader;
    /**
     * 当前阅读得书籍
     */
    private Book book;
    private IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    private ChangeListener<Number> currentIndexListener;
    private Stack<ContentNode> cacheNodes = new Stack<>();

    public void initialize() {
        initListener();
        //章节列表样式
        catalogListView.setCellFactory((ListView<Chapter> l) -> new ListCell<Chapter>() {
            @Override
            protected void updateItem(Chapter item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    Glyph icon = new Glyph("FontAwesome", '\uf097');
                    Glyph cacheIcon = new Glyph("FontAwesome", '\uf058');
                    BorderPane box = new BorderPane();
                    box.getStyleClass().add("chapter-cell");
                    box.setLeft(new Label(item.getName(), icon));
                    if (StrUtil.isNotEmpty(item.getContentPath())) {
                        box.setRight(cacheIcon);
                    }
                    setGraphic(box);
                    setPadding(new Insets(8));
                } else {
                    setGraphic(null);
                }
            }
        });
        //显示的时候自动滚动到上次的位置
        Reader.stage.setOnShown(e -> {
            if (firstLoad) {
                initOnFirstShow();
                firstLoad = false;
                AbstractLoadingTask task = new AbstractLoadingTask() {
                    @Override
                    protected Object call() {
                        while (true) {
                            if (contentBox.getHeight() > 0) {
                                ThreadUtil.sleep(100);
                                break;
                            }
                        }
                        return null;
                    }
                };
                task.setSuccessHandler(ex -> contentScrollPane.setVvalue(book.getLocation()));
                ThreadUtil.execute(task);
            } else {
                Platform.runLater(() -> contentScrollPane.setVvalue(book.getLocation()));
            }
        });
    }


    /**
     * 监听器绑定
     */
    private void initListener() {
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            String color = JFXNodeUtils.colorToHex(newValue);
            changeBgColor(color);
            eye.setSelected(false);
        });
        //夜间模式
        nightTheme.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                nightTheme.setText("日间");
                ((Glyph) nightTheme.getGraphic()).setIcon('\uf185');
                changeBgColor(NIGHT_COLOR);
            } else {
                nightTheme.setText("夜间");
                ((Glyph) nightTheme.getGraphic()).setIcon('\uf186');
                changeBgColor("#FFF");
            }
        });
        //正文对齐
        alignGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String align = newValue.getUserData().toString();
                DataManager.application.getReaderConfig().getAlign().set(align);
            }
        });
        //滚动
        contentScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (firstLoad) {
                return;
            }
            //滚动到上一章
            if (newValue.doubleValue() == contentScrollPane.getVmin()) {
                previousSmooth();
            } else if (newValue.doubleValue() == contentScrollPane.getVmax()) {
                nextSmooth();
            }
        });
        //字体大小更改
        fontSizeSlider.valueProperty().bindBidirectional(DataManager.application.getReaderConfig().getFontSize());
        fontSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fontSizeSlider.setValue(newValue.doubleValue());
            changeContentStyle();
        });
        //行间距
        Platform.runLater(() -> lineSpaceSlider.valueProperty().bindBidirectional(
            DataManager.application.getReaderConfig().getLineSpacing()));
        //中文字体
        String[] strings = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        //List<String> families = Arrays.stream(strings).filter(f -> f.matches("[\\u4e00-\\u9fa5]+")).collect(Collectors.toList());
        List<String> families = Arrays.stream(strings).collect(Collectors.toList());
        fonts.getItems().setAll(families);
        fonts.valueProperty().bindBidirectional(DataManager.application.getReaderConfig().getFontFamily());
        fonts.valueProperty().addListener((observable, oldValue, newValue) -> {
            fonts.setValue(newValue);
            changeContentStyle();
        });
        //页面宽度
        Platform.runLater(() -> pageWidthSlider.valueProperty().bindBidirectional(
            DataManager.application.getReaderConfig().getPageWidth()));
        pageWidthSlider.setValueFactory(
            slider -> Bindings.createStringBinding(() -> (int) slider.getValue() + "%", slider.valueProperty()));
        //焦点获取
        catalogDrawer.setOnDrawerClosed(e -> contentScrollPane.requestFocus());
        //章节切换
        chapterSlider.setValueFactory(slider -> Bindings.createStringBinding(
            () -> ((int) slider.getValue() + 1) + "/" + ((int) slider.getMax() + 1), slider.valueProperty()));
        currentIndexListener = (observable, oldValue, newValue) -> {
            String name = loader.chapters().get(currentIndex.get()).getName();
            Reader.stage.setTitle(name);
            changeChapter(newValue.intValue(), oldValue.intValue() > newValue.intValue());
            chapterSlider.adjustValue(newValue.intValue());
        };
        chapterSlider.valueChangingProperty().addListener(e -> {
            if (!chapterSlider.isValueChanging()) {
                if (currentIndex.get() != (int) chapterSlider.getValue()) {
                    clearNodes();
                    currentIndex.set((int) chapterSlider.getValue());
                    toTop();
                }
            }
        });
        chapterSlider.setOnMouseClicked(e -> {
            if (currentIndex.get() != (int) chapterSlider.getValue()) {
                clearNodes();
                currentIndex.set((int) chapterSlider.getValue());
                toTop();
            }
        });
    }

    /**
     * 渲染后初始化
     */
    public void initLater() {
        showHeader(DataManager.application.getReaderConfig().isHeader());
        //更改字体
        changeContentStyle();
    }

    /**
     * 第一次显示的时候初始化
     */
    private void initOnFirstShow() {
        //更改背景
        if (StrUtil.isNotEmpty(DataManager.application.getReaderConfig().getBgImage().get())) {
            setBgImage();
        } else {
            changeBgColor(DataManager.application.getReaderConfig().getBgColor().get());
        }
        //color-picker样式自定义
        //获取滚动面板皮肤
        scrollPaneSkin = (ScrollPaneSkin) contentScrollPane.getSkin();
        Label colorLabel = (Label) ((ColorPickerSkin) colorPicker.getSkin()).getDisplayNode();
        colorLabel.setAlignment(Pos.CENTER);
        colorLabel.setGraphic(plusIcon);
        colorLabel.getStyleClass().add("gradient-bg");
        plusIcon.getStyleClass().add("theme-color-icon");
    }


    /**
     * 创建章节内容节点
     *
     * @param title   章节标题
     * @param content 章节内容
     * @return /
     */
    private ContentNode createChapterNodes(String title, String content, int chapterIndex) {
        ContentNode node;
        if (cacheNodes.empty()) {
            node = new ContentNode(title, content, chapterIndex);
            //字体
            node.getContent().styleProperty().bind(styleFont);
            node.getTitle().styleProperty().bind(styleTileFont);
            //行间距
            node.getContent().lineSpacingProperty().bind(lineSpaceSlider.valueProperty());
            //页宽
            node.getContent().maxWidthProperty().bind(Bindings.createObjectBinding(() -> {
                Platform.runLater(() -> contentBox.requestLayout());
                return contentBox.getWidth() * pageWidthSlider.getValue() / 100;
            }, pageWidthSlider.valueProperty(), contentBox.widthProperty()));
            //对齐
            node.getContent().textAlignmentProperty().bind(Bindings.createObjectBinding(() -> {
                String align = DataManager.application.getReaderConfig().getAlign().get();
                return TextAlignment.valueOf(align);
            }, DataManager.application.getReaderConfig().getAlign()));
        } else {
            node = cacheNodes.pop();
            node.update(title, content, chapterIndex);
        }
        return node;
    }


    /**
     * 加载一本书
     *
     * @param loader 加载器
     */
    void loadBook(NovelLoader loader) {
        clearNodes();
        catalogListView.getItems().setAll(loader.chapters());
        chapterSlider.setMax(loader.chapters().size() - 1);
        this.loader = loader;
        this.book = loader.getBook();
        this.currentIndex.set(book.getChapterIndex());
        this.chapterSlider.adjustValue(book.getChapterIndex());
        Reader.stage.setTitle(book.getName());
        //恢复到上次的位置
        String[] nodesIndex = this.book.getReadingChapter().split(",");
        for (int i = Integer.parseInt(nodesIndex[0]);
             i < Integer.parseInt(nodesIndex[0]) + Integer.parseInt(nodesIndex[1]); i++) {
            Chapter chapter = loader.chapters().get(i);
            contentBox.getChildren().add(createChapterNodes(chapter.getName(), loader.content(i), i));
        }
        this.contentScrollPane.requestFocus();
        this.currentIndex.addListener(this.currentIndexListener);
    }

    /**
     * 切换章节
     *
     * @param chapterIndex /
     */
    private void changeChapter(int chapterIndex, boolean previous) {
        //重置播放器
        resetPlayer();
        if (chapterIndex < 0 || chapterIndex >= loader.chapters().size()) {
            return;
        }
        if (book.isWeb() && StrUtil.isEmpty(loader.chapters().get(chapterIndex).getContentPath())) {
            AbstractLoadingTask<String> task = new AbstractLoadingTask<String>() {
                @Override
                protected String call() {
                    return loader.content(chapterIndex);
                }
            };
            task.setSuccessHandler(e -> changeChapter(chapterIndex, previous, task.getValue()));
            ThreadUtil.execute(task);
        } else {
            changeChapter(chapterIndex, previous, loader.content(chapterIndex));
        }
    }

    /**
     * 切换章节
     *
     * @param chapterIndex 章节索引
     * @param previous     是否上一章
     * @param content      章节内容
     */
    private void changeChapter(int chapterIndex, boolean previous, String content) {
        //上一章
        if (previous) {
            if (contentBox.getChildren().size() >= CACHE_PAGE) {
                ContentNode node = (ContentNode) contentBox.getChildren().remove(contentBox.getChildren().size() - 1);
                cacheNodes.push(node);
            }
            contentBox.getChildren().add(0,
                createChapterNodes(loader.chapters().get(chapterIndex).getName(), content, chapterIndex));
        } else {
            if (contentBox.getChildren().size() >= CACHE_PAGE) {
                double height = contentBox.getHeight();
                ContentNode node = (ContentNode) contentBox.getChildren().remove(0);
                cacheNodes.push(node);
                ThreadUtil.execute(() -> {
                    while (true) {
                        if (height != contentBox.getHeight()) {
                            break;
                        }
                    }
                    Platform.runLater(() -> {
                        contentScrollPane.setVvalue(1);
                        contentBox.getChildren().add(
                            createChapterNodes(loader.chapters().get(chapterIndex).getName(), content, chapterIndex));
                    });
                });
            } else {
                contentBox.getChildren().add(
                    createChapterNodes(loader.chapters().get(chapterIndex).getName(), content, chapterIndex));
            }
        }
    }


    /**
     * 清空页面
     */
    private void clearNodes() {
        cacheNodes.addAll(contentBox.getChildren().stream().map(i -> (ContentNode) i).collect(Collectors.toList()));
        contentBox.getChildren().clear();
    }

    /**
     * 上一章 到顶部
     */
    public void previous() {
        if (currentIndex.get() == 0) {
            return;
        }
        clearNodes();
        currentIndex.set(Math.max(currentIndex.get() - 1, 0));
        toTop();
    }

    /**
     * 上一章 上下衔接
     */
    private void previousSmooth() {
        if (currentIndex.get() == 0) {
            return;
        }
        double height = contentBox.getLayoutBounds().getHeight();
        currentIndex.set(((ContentNode) contentBox.getChildren().get(0)).getChapterIndex() - 1);
        //FIXME 等待渲染完成 总觉得有什么地方可以监听的，但是懒得挖了。。 requestLayout无效
        ThreadUtil.execute(() -> {
            while (true) {
                if (height != contentBox.getLayoutBounds().getHeight()) {
                    break;
                }
            }
            Platform.runLater(() -> contentScrollPane.setVvalue(
                contentBox.getChildren().get(1).getLayoutY() / contentBox.getLayoutBounds().getHeight()));
        });

    }

    /**
     * 下一章
     */
    public void next() {
        if (currentIndex.get() + 1 == loader.chapters().size()) {
            return;
        }
        clearNodes();
        currentIndex.set(Math.min(currentIndex.get() + 1, loader.chapters().size()));
        toTop();
    }

    /**
     * 下一章 上下相接
     */
    private void nextSmooth() {
        currentIndex.set(
            ((ContentNode) contentBox.getChildren().get(contentBox.getChildren().size() - 1)).getChapterIndex() + 1);
    }

    /**
     * 显示目录
     */
    public void showCatalog() {
        settingDrawer.close();
        catalogListView.getSelectionModel().select(currentIndex.get());
        catalogListView.scrollTo(currentIndex.get());
        catalogListView.refresh();
        root.toggle(catalogDrawer);
    }

    /**
     * 显示设置面板
     */
    private void showSetting() {
        catalogDrawer.close();
        root.toggle(settingDrawer);
    }

    /**
     * 更换背景色
     */
    private void changeBgColor(String fill) {
        DataManager.application.getReaderConfig().getBgImage().set("");
        Reader.root.setStyle("");
        if (fill.equals(EYE_COLOR)) {
            eye.setSelected(true);
        } else if (fill.equals(NIGHT_COLOR)) {
            nightTheme.setSelected(true);
        }
        ThemeUtil.setCss(Dict.create().set("bgColor", fill),
            Reader.stage.getScene(), "css/reader.ftl", "/theme/reader.css");
        DataManager.application.getReaderConfig().getBgColor().set(fill);
    }

    /**
     * 护眼模式
     */
    public void eyeMode() {
        if (eye.isSelected()) {
            changeBgColor(EYE_COLOR);
        } else {
            changeBgColor("#FFFFFF");
        }
    }

    /**
     * 更改内容样式
     */
    private void changeContentStyle() {
        styleFont.set(String.format("-fx-font-size: %spx;-fx-font-family: '%s'",
            DataManager.application.getReaderConfig().getFontSize().get(),
            ChineseFont.getFont(DataManager.application.getReaderConfig().getFontFamily().get())));
        styleTileFont.set(String.format("-fx-font-size: %spx;-fx-font-family: '%s'",
            DataManager.application.getReaderConfig().getFontSize().get() + 12,
            DataManager.application.getReaderConfig().getFontFamily().get()));
    }

    /**
     * 选择图片
     */
    public void selectImage() {
        DesktopUtil.selectImage(path -> {
            DataManager.application.getReaderConfig().getBgColor().set("");
            DataManager.application.getReaderConfig().getBgImage().set(path);
            setBgImage();
        });
    }

    /**
     * 设置背景图
     */
    private void setBgImage() {
        Reader.root.setStyle(String.format("-fx-background-image: url('%s')",
            DataManager.application.getReaderConfig().getBgImage().get()));
    }

    public void exit() {
        Reader.stage.hide();
        App.stage.show();
    }

    /**
     * 返回主页
     */
    public void onHidden() {
        //取消监听
        currentIndex.removeListener(currentIndexListener);
        //初始化朗读
        resetPlayer();
        //保存阅读数据
        double y = contentBox.getLayoutBounds().getHeight() * contentScrollPane.getVvalue();
        for (Node e : contentBox.getChildren()) {
            if (y > e.getLayoutY()) {
                ContentNode node = (ContentNode) e;
                book.setChapterIndex(node.getChapterIndex());
            } else {
                break;
            }
        }
        this.book.setLocation(contentScrollPane.getVvalue());
        this.book.setReadingChapter(
            String.format("%s,%s", ((ContentNode) contentBox.getChildren().get(0)).getChapterIndex(),
                contentBox.getChildren().size()));
        currentIndex.set(-1);
        //保存入库
        ThreadUtil.execute(() -> {
            ApplicationUtil.storeConfig();
            MybatisUtil.execute(BookMapper.class, mapper -> mapper.updateById(book));
            if (book.isWeb()) {
                loader.store();
            }
        });
        //保存宽高
        DataManager.application.getReaderConfig().getStageWidth().set(Reader.stage.getWidth());
        DataManager.application.getReaderConfig().getStageHeight().set(Reader.stage.getHeight());
        //显示主窗口
        catalogDrawer.close();
        settingDrawer.close();
    }


    /**
     * 显示隐藏头部
     */
    public void toggleHeader() {
        if (Reader.root.header().getChildren().isEmpty()) {
            hiddenBtn.setText("隐藏状态栏");
            Reader.root.showHeader();
            DataManager.application.getReaderConfig().setHeader(true);
        } else {
            Reader.root.hiddenHeader();
            hiddenBtn.setText("显示状态栏");
            DataManager.application.getReaderConfig().setHeader(false);
        }
    }

    /**
     * 显示隐藏头部
     */
    private void showHeader(boolean show) {
        if (show) {
            hiddenBtn.setText("隐藏状态栏");
            Reader.root.showHeader();
        } else {
            Reader.root.hiddenHeader();
            hiddenBtn.setText("显示状态栏");
        }
    }

    /**
     * 滚动到顶部
     */
    private void toTop() {
        contentScrollPane.setVvalue(0.00001);
    }

    /**
     * 朗读
     */
    public void speak() {
        if (!SystemUtil.getOsInfo().isWindows()) {
            ToastUtil.warning("语音朗读只支持Windows操作系统");
            speakBtn.setSelected(false);
            return;
        }
        if (speakBtn.isSelected()) {
            speakBtn.setText("停止");
            if (player == null) {
                AbstractLoadingTask task = new AbstractLoadingTask() {
                    @Override
                    protected Object call() {
                        if (speech == null) {
                            speech = new MSTTSSpeech();
                        }
                        play();
                        return null;
                    }
                };
                task.setSuccessHandler(e -> player.play());
                ThreadUtil.execute(task);
            } else {
                player.play();
            }
        } else {
            speakBtn.setText("朗读");
            if (player != null) {
                player.pause();
            }
        }
    }

    /**
     * 朗读音频
     */
    private void play() {
        String content = loader.content(currentIndex.get());
        String title = loader.chapters().get(currentIndex.get()).getName();
        File audio = FileUtil.currentDirFile(TMP_AUDIO);
        speech.saveToWav(title + "\r\n" + content, audio.getAbsolutePath());
        player = new MediaPlayer(new Media(audio.toURI().toString()));
        player.setAutoPlay(true);
        player.setOnEndOfMedia(() -> {
            next();
            ThreadUtil.execute(this::play);
        });
    }

    /**
     * 重置播放器
     */
    private void resetPlayer() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
        speakBtn.setSelected(false);
        speakBtn.setText("朗读");
        FileUtil.deleteForce(FileUtil.currentDirFile(TMP_AUDIO));
    }

    /**
     * scroll面板按键处理
     */
    public void handleSpKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case LEFT:
                previous();
                break;
            case RIGHT:
                next();
                break;
            case SPACE:
            case PAGE_DOWN: {
                double v =
                    (contentScrollPane.getViewportBounds().getHeight() / contentBox.getLayoutBounds().getHeight()) * 0.9
                        + contentScrollPane.getVvalue();
                if (v > 1) {
                    contentScrollPane.setVvalue(1);
                    break;
                } else {
                    contentScrollPane.setVvalue(v);
                }
                break;
            }
            case PAGE_UP: {
                double v = contentScrollPane.getVvalue()
                    - (contentScrollPane.getViewportBounds().getHeight() / contentBox.getLayoutBounds().getHeight())
                    * 0.9;
                if (v < 0) {
                    contentScrollPane.setVvalue(0);
                    break;
                } else {
                    contentScrollPane.setVvalue(v);
                }
                break;
            }
            case F11:
                Reader.stage.setFullScreen(true);
                break;
            case ESCAPE:
                exit();
                break;
            default:
                break;
        }
    }

    /**
     * scroll面板点击处理
     */
    public void handleSpClick(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            double x = e.getX();
            double width = contentBox.getWidth();
            if (x < CHANGE_PAGE_AREA_WIDTH * width) {
                scrollPaneSkin.vsbPageDecrement();
            } else if (x > width - CHANGE_PAGE_AREA_WIDTH * width) {
                scrollPaneSkin.vsbPageIncrement();
            } else {
                showSetting();
            }
        }
    }

    /**
     * 章节目录点击
     */
    public void handleCatalogClick() {
        int index = catalogListView.getSelectionModel().getSelectedIndex();
        if (index != currentIndex.get()) {
            clearNodes();
            currentIndex.set(index);
            toTop();
        }
        catalogDrawer.close();
    }

    /**
     * 背景颜色点击
     */
    public void handleChangeBgColor(MouseEvent event) {
        ToggleButton child = (ToggleButton) event.getSource();
        if (!child.isSelected()) {
            child.setSelected(true);
        }
        changeBgColor(child.getUserData().toString());
    }
}
