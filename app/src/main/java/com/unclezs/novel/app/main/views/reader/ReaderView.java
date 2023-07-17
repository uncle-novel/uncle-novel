package com.unclezs.novel.app.main.views.reader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import com.sun.javafx.scene.control.skin.Utils;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.SelectableButton;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.TabGroup;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.executor.DebounceTask;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.support.hotkey.HotKeyManager;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.framework.util.FontUtils;
import com.unclezs.novel.app.framework.util.ResourceUtils;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.core.loader.AbstractBookLoader;
import com.unclezs.novel.app.main.core.loader.BookLoader;
import com.unclezs.novel.app.main.core.loader.TxtLoader;
import com.unclezs.novel.app.main.db.beans.Book;
import com.unclezs.novel.app.main.db.dao.BookDao;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.config.HotKeyConfig;
import com.unclezs.novel.app.main.model.config.ReaderConfig;
import com.unclezs.novel.app.main.model.config.TTSConfig;
import com.unclezs.novel.app.main.util.MixPanelHelper;
import com.unclezs.novel.app.main.views.components.cell.TocListCell;
import com.unclezs.novel.app.main.views.home.HomeView;
import com.unclezs.novel.app.main.views.reader.player.TTSPlayer;
import com.unclezs.novel.app.main.views.reader.widgets.ReaderContextMenu;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@Slf4j
@FxView(fxml = "/layout/reader/reader.fxml")
public class ReaderView extends SceneView<StageDecorator> {


  public static final String BUNDLE_READ_BOOK_KEY = "read-book-key";
  public static final String FONT_STYLE_FORMAT = "-fx-font-size: %spx;-fx-font-family: '%s'";
  public static final double TOC_AREA = 0.05;
  public static final double PRE_PAGE_AREA = 0.25;
  public static final double NEXT_PAGE_AREA = 0.75;
  public static final String DEFAULTS_TTS_CONFIG = "assets/defaults/tts.json";
  private static final String[] NO_SHADOW_STYLE_CLASS = {"no-window-shadow", "no-header-shadow"};
  private static final String PAGE_NAME = "阅读器";
  private final String[] contents = new String[3];
  /**
   * 当前章节所有页
   */
  private final List<String> pages = new ArrayList<>();
  private final IntegerProperty currentChapterIndex = new SimpleIntegerProperty();
  private final BookDao bookDao = new BookDao();
  @FXML
  private SelectableButton speakButton;
  @FXML
  private SelectableButton playButton;
  @FXML
  private ComboBox<String> speakerSelector;
  @FXML
  private JFXSlider ttsSpeedSlider;
  @FXML
  private ScrollPane settingView;
  @FXML
  private VBox ttsSettingBox;
  @FXML
  private VBox settingBox;
  @FXML
  private JFXCheckBox showShadow;
  @FXML
  private JFXCheckBox flipAnimation;
  @FXML
  private ListView<Chapter> tocListView;
  @FXML
  private StackPane container;
  @FXML
  private ComboBox<String> fontSelector;
  @FXML
  private JFXSlider chapterSlider;
  @FXML
  private ReaderThemeView themeView;
  @FXML
  private JFXDrawer tocDrawer;
  @FXML
  private JFXDrawer settingDrawer;
  @FXML
  private JFXDrawersStack drawer;
  @FXML
  private TabGroup alignGroup;
  @FXML
  private TabGroup simpleTraditionalGroup;
  @FXML
  private JFXSlider fontSizeSlider;
  @FXML
  private JFXSlider lineSpaceSlider;
  @FXML
  private JFXSlider pageWidthSlider;
  @FXML
  private PageView currentPage;
  @FXML
  private PageView otherPage;
  private Book book;
  private AbstractBookLoader loader;
  private ReaderConfig config;
  private TTSPlayer player;
  private ReaderContextMenu contextMenu;
  private DisplayPageTask displayPageTask;
  /**
   * 翻页中
   */
  private boolean turnPaging;
  /**
   * 当前页码
   */
  private int current = -1;

  @Override
  public void onCreated() {
    this.config = SettingManager.manager().getReader();
    getRoot().getScene().getStylesheets().add("css/reader/reader.css");

    this.contextMenu = new ReaderContextMenu();
    // 初始化阅读器设置
    initSetting();
    // TTS 播放器
    initTTSPlayer();
    // 初始化阅读器默认行为
    initBehavior();
    // 初始化热键
    initHotkey();
    tocListView.setCellFactory(param -> new TocListCell(loader::isCached));
    EventUtils.setOnMousePrimaryClick(tocListView, e -> {
      if (!tocListView.getSelectionModel().isEmpty()) {
        toChapter(tocListView.getSelectionModel().getSelectedIndex());
        drawer.toggle(tocDrawer);
      }
    });
    // 窗口初始大小
    App.stage().setWidth(config.getStageWidth().get());
    App.stage().setHeight(config.getStageHeight().get());
    getRoot().setPrefHeight(config.getStageHeight().get());
    getRoot().setPrefWidth(config.getStageWidth().get());
    // 初始化设置面板
    setSettingView(false);
  }

  @Override
  public void onShow(SceneNavigateBundle bundle) {
    MixPanelHelper.event(PAGE_NAME);
    // 窗口置顶
    contextMenu.toggleWindowTop(config.isWindowTop());
    // 获取书籍信息
    Book bundleBook = bundle.get(BUNDLE_READ_BOOK_KEY);
    if (bundleBook != null) {
      this.book = bundleBook;
      getRoot().setTitle(book.getName());
      if (book.isLocal()) {
        loader = new TxtLoader();
      } else {
        loader = new BookLoader();
      }
      loader.setBook(book);
      tocListView.getItems().setAll(loader.toc());
      chapterSlider.setMax(loader.toc().size());
      currentChapterIndex.set(book.getCurrentChapterIndex());
      loadContent(() -> {
        contents[1] = contents(1);
        current = book.getCurrentPage();
        updateDisplayText();
      }, 1);
    }
  }

  @Override
  public void onClose(StageDecorator view, IconButton closeButton) {
    App.stage().setAlwaysOnTop(false);
    config.getStageWidth().set(App.stage().getWidth());
    config.getStageHeight().set(App.stage().getHeight());
    // 阅读进度保存
    book.setCurrentPage(current);
    book.setCurrentChapterIndex(getCurrentChapterIndex());
    bookDao.update(book);
    // 初始化
    current = -1;
    forEachPageView(pageView -> pageView.setText(null));
    // 清空页面缓存
    clearCaches();
    // 停止TTS
    stopSpeaking();
    // 回到首页
    app.navigate(HomeView.class, new SceneNavigateBundle());
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    showOperationView();
  }

  /**
   * 初始化阅读器行为
   */
  private void initBehavior() {
    container.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY) {
        double clickX = event.getX();
        double clickY = event.getY();
        double width = container.getWidth();
        double height = container.getHeight();
        // 显示目录
        if (clickX < width * TOC_AREA) {
          showToc();
          // 上一页
        } else if (clickX < width * PRE_PAGE_AREA || (clickY < height * PRE_PAGE_AREA
          && clickX < width * NEXT_PAGE_AREA)) {
          prePage();
          // 下一页
        } else if (clickX > width * NEXT_PAGE_AREA || (clickY > height * NEXT_PAGE_AREA
          && clickX > width * PRE_PAGE_AREA)) {
          nextPage();
        } else {
          // 显示设置
          showOperationView();
        }
        if (contextMenu.isShowing()) {
          contextMenu.hide();
        }
        event.consume();
      }
    });
    // 上下文菜单
    container.setOnContextMenuRequested(event -> contextMenu.show(getRoot(), event.getScreenX(), event.getScreenY()));
  }

  /**
   * 初始化热键
   */
  private void initHotkey() {
    HotKeyConfig hotKeyConfig = SettingManager.manager().getHotkey();
    getRoot().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (HotKeyManager.windowHotKeyMatch(hotKeyConfig.getReaderNextChapter(), event)) {
        nextChapter();
      } else if (HotKeyManager.windowHotKeyMatch(hotKeyConfig.getReaderPreChapter(), event)) {
        preChapter();
      } else if (HotKeyManager.windowHotKeyMatch(hotKeyConfig.getReaderNextPage(), event)) {
        nextPage();
      } else if (HotKeyManager.windowHotKeyMatch(hotKeyConfig.getReaderPrePage(), event)) {
        prePage();
      } else if (HotKeyManager.windowHotKeyMatch(hotKeyConfig.getReaderToc(), event)) {
        showToc();
      } else if (event.getCode() == KeyCode.ESCAPE) {
        onClose(getRoot(), null);
      }
    });
    // 滚轮翻页
    AtomicBoolean nextPage = new AtomicBoolean(false);
    DebounceTask scrollTurnPageTask = DebounceTask.build(() -> {
      // drawer显示时不触发
      if (settingDrawer.isOpened() || tocDrawer.isOpened()) {
        return;
      }
      if (nextPage.get()) {
        nextPage();
      } else {
        prePage();
      }
    }, 100L, true);
    getRoot().getScene().setOnScroll(e -> {
      nextPage.set(e.getDeltaY() < 0);
      scrollTurnPageTask.run();
    });
  }

  /**
   * 初始化设置
   */
  private void initSetting() {
    // 设置主题
    themeView.changeTheme(config.getThemeName().get());
    // 显示头部
    contextMenu.toggleHeader(!config.isShowHeader());
    // 章节切换
    chapterSlider.valueProperty().bindBidirectional(currentChapterIndex);
    chapterSlider.valueChangingProperty().addListener(e -> {
      if (!chapterSlider.isValueChanging()) {
        currentChapterIndex.set((int) chapterSlider.getValue());
        toChapter(getCurrentChapterIndex());
      }
    });
    chapterSlider.setValueFactory(
      slider -> Bindings.createStringBinding(() -> ((int) slider.getValue() + 1) + "/" + ((int) slider.getMax() + 1),
        slider.valueProperty()));
    chapterSlider.setOnMouseClicked(e -> {
      currentChapterIndex.set((int) chapterSlider.getValue());
      toChapter(getCurrentChapterIndex());
    });
    // 页面宽度
    container.widthProperty().addListener(e -> forEachPageView(view -> {
      if (view.getTranslateX() < 0) {
        view.setTranslateX(-container.getWidth());
      }
    }));
    forEachPageView(pageView -> {
      pageView.widthProperty().addListener(e -> updateDisplayText());
      pageView.heightProperty().addListener(e -> updateDisplayText());
      pageView.prefWidthProperty().bind(
        Bindings.createDoubleBinding(() -> container.getWidth() * pageWidthSlider.getValue(), container.widthProperty(),
          pageWidthSlider.valueProperty()));
    });
    pageWidthSlider.valueProperty().bindBidirectional(config.getPageWidth());
    pageWidthSlider.setValueFactory(
      slider -> Bindings.createStringBinding(() -> ((int) (slider.getValue() * 100)) + "%", slider.valueProperty()));
    // 简体与繁体
    simpleTraditionalGroup.setOnSelected(tabButton -> {
      forEachPageView(pageView -> updateDisplayText());
      config.getSimpleTraditional().set(simpleTraditionalGroup.getTabs().indexOf(tabButton));
    });
    Integer simpleToTraIndex = config.getSimpleTraditional().get();
    simpleTraditionalGroup.getTabs().get(simpleToTraIndex == null ? 0 : simpleToTraIndex).setSelected(true);
    // 对齐方式
    alignGroup.setOnSelected(tabButton -> {
      String align = tabButton.getUserData().toString();
      forEachPageView(pageView -> pageView.setTextAlignment(TextAlignment.valueOf(align)));
      config.getAlign().set(align);
    });
    alignGroup.findTab(config.getAlign().get()).setSelected(true);
    // 字体选择器
    fontSelector.getItems().setAll(FontUtils.getAllFontFamilies());
    fontSelector.valueProperty().bindBidirectional(config.getFontFamily());
    fontSelector.valueProperty().addListener(e -> updateFont());
    // 字体大小选择器
    fontSizeSlider.valueProperty().bindBidirectional(config.getFontSize());
    fontSizeSlider.valueProperty().addListener(e -> updateFont());
    // 初始化字体
    updateFont();
    // 行间距选择器
    lineSpaceSlider.valueProperty().bindBidirectional(config.getLineSpacing());
    lineSpaceSlider.valueProperty().addListener(e -> {
      forEachPageView(content -> content.setLineSpacing(lineSpaceSlider.getValue()));
      updateDisplayText();
    });
    forEachPageView(pageView -> pageView.setLineSpacing(lineSpaceSlider.getValue()));
    // 窗口阴影
    showShadow.setSelected(config.isShowShadow());
    showShadow.selectedProperty().addListener(e -> updateShadow());
    updateShadow();
    // 翻页动画
    flipAnimation.setSelected(config.getFlipAnimation().get());
    flipAnimation.selectedProperty().bindBidirectional(config.getFlipAnimation());
  }

  /**
   * 初始化TTS播放器
   */
  private void initTTSPlayer() {
    // TTS配置
    List<TTSConfig> ttsConfigs = GsonUtils.me().fromJson(IoUtil.readUtf8(ResourceUtils.stream(DEFAULTS_TTS_CONFIG)),
      new TypeToken<List<TTSConfig>>() {
      }.getType());
    speakerSelector.getItems().setAll(ttsConfigs.stream().map(TTSConfig::getName).collect(Collectors.toList()));
    speakerSelector.getSelectionModel().select(config.getSpeaker().get());
    speakerSelector.valueProperty().addListener(e -> {
      int index = speakerSelector.getSelectionModel().getSelectedIndex();
      config.getSpeaker().set(index);
      player.setConfig(ttsConfigs.get(index));
      playButton.setText("暂停");
      playButton.setSelected(true);
    });
    // TTS朗读速度
    ttsSpeedSlider.setValueFactory(
      slider -> Bindings.createStringBinding(() -> String.valueOf((int) (slider.getValue() * 10)),
        slider.valueProperty()));
    ttsSpeedSlider.setValue(config.getSpeed().get());
    ttsSpeedSlider.valueProperty().addListener(e -> {
      player.setSpeed(ttsSpeedSlider.getValue());
      config.getSpeed().set(ttsSpeedSlider.getValue());
    });
    int speckIndex = config.getSpeaker().get();
    if (speckIndex < 0 || speckIndex >= ttsConfigs.size()) {
      speckIndex = 0;
    }
    player = new TTSPlayer(ttsConfigs.get(speckIndex), this::nextPage);
  }

  /**
   * 处理每一个页面
   *
   * @param handler 处理器
   */
  private void forEachPageView(Consumer<PageView> handler) {
    handler.accept(currentPage);
    handler.accept(otherPage);
  }

  /**
   * 更新字体
   */
  private void updateFont() {
    forEachPageView(content -> {
      content.setStyle(String.format(FONT_STYLE_FORMAT, fontSizeSlider.getValue(), fontSelector.getValue()));
      content.getTitle().setStyle(
        String.format(FONT_STYLE_FORMAT, fontSizeSlider.getValue() + 12, fontSelector.getValue()));
    });
    updateDisplayText();
  }

  /**
   * 更新阴影
   */
  private void updateShadow() {
    if (showShadow.isSelected()) {
      getRoot().getStyleClass().removeAll(NO_SHADOW_STYLE_CLASS);
    } else {
      getRoot().getStyleClass().addAll(NO_SHADOW_STYLE_CLASS);
    }
    config.setShowShadow(showShadow.isSelected());
  }

  /**
   * 更新当前显示的文字
   */
  private void updateDisplayText() {
    if (currentPage.getWidth() > 0 && currentPage.getHeight() > 0 && current >= 0) {
      computePages(contents[1]);
      displayPage(current, TurnPageType.NONE);
    }
  }

  /**
   * 显示一页 防抖100ms
   *
   * @param page 页码
   */
  private void displayPage(int page, TurnPageType type) {
    if (displayPageTask == null) {
      displayPageTask =
        new DisplayPageTask(() -> this.displayPageByDebounce(displayPageTask.page, displayPageTask.type), 100L);
    }
    displayPageTask.init(page, type);
    displayPageTask.run();
  }

  /**
   * 显示一页
   *
   * @param page 页码
   */
  private void displayPageByDebounce(int page, TurnPageType type) {
    if (turnPaging) {
      return;
    }
    turnPaging = true;
    while (page >= pages.size() && page != 0) {
      page--;
    }
    current = page;
    PageView showView;
    // 是否启用翻页动画
    if (!flipAnimation.isSelected()) {
      type = TurnPageType.NONE;
    }
    // 翻页动画处理
    if (type != TurnPageType.NONE) {
      showView = otherPage;
      container.getChildren().remove(otherPage);
      Transition transition;
      if (type == TurnPageType.NEXT) {
        // 另一页在下方，先复位
        otherPage.setTranslateX(0);
        transition = currentPage.getNextTransition();
        transition.play();
        container.getChildren().add(0, otherPage);
      } else {
        // 另一页在上方，先将隐藏，然后滑出
        otherPage.setTranslateX(-container.getWidth());
        transition = otherPage.getPreTransition();
        transition.play();
        container.getChildren().add(1, otherPage);
      }
      PageView tmp = currentPage;
      currentPage = otherPage;
      otherPage = tmp;
      transition.setOnFinished(e -> turnPaging = false);
    } else {
      showView = currentPage;
      turnPaging = false;
    }
    // 设置显示页面的文字
    showView.setText(currentPageText());
    // 第一页标题处理
    if (page == 0) {
      String titleText = loader.toc().get(getCurrentChapterIndex()).getName();
      showView.setTitle(titleText);
    } else {
      showView.setTitle(null);
    }
    // 停止朗读
    if (speakButton.isSelected()) {
      playTTS(true);
    }
  }

  /**
   * 显示操作面板
   */
  private void showOperationView() {
    drawer.toggle(settingDrawer);
  }

  /**
   * 切换设置面板
   *
   * @param tts 是否未tts
   */
  private void setSettingView(boolean tts) {
    settingView.setContent(tts ? ttsSettingBox : settingBox);
    settingDrawer.setDefaultDrawerSize(tts ? 150 : 320);
  }

  /**
   * 显示目录
   */
  @FXML
  private void showToc() {
    tocListView.getSelectionModel().select(getCurrentChapterIndex());
    tocListView.refresh();
    drawer.toggle(tocDrawer);
  }

  /**
   * 上一页
   */
  @FXML
  private void prePage() {
    if (current > 0) {
      displayPage(current - 1, TurnPageType.PRE);
    } else {
      preChapter(true);
    }
  }

  /**
   * 下一页
   */
  @FXML
  private void nextPage() {
    if (current < pages.size() - 1) {
      displayPage(current + 1, TurnPageType.NEXT);
    } else {
      nextChapter();
    }
  }

  /**
   * 上一章，直接跳转首页
   */
  @FXML
  public void preChapter() {
    preChapter(false);
  }

  /**
   * 上一章
   *
   * @param lastPage 是否直接跳转首页
   */
  private void preChapter(boolean lastPage) {
    if (turnPaging) {
      return;
    }
    if (getCurrentChapterIndex() == 0) {
      Toast.success("没有上一页了~");
      return;
    }
    contents[2] = contents[1];
    loadContent(() -> {
      contents[1] = contents(0);
      contents[0] = null;
      currentChapterIndex.set(getCurrentChapterIndex() - 1);
      computePages(contents[1]);
      // 是否跳转最后一页
      if (lastPage) {
        displayPage(pages.size() - 1, TurnPageType.PRE);
      } else {
        displayPage(0, TurnPageType.PRE);
      }
    }, 0);

  }

  /**
   * 下一章
   */
  @FXML
  public void nextChapter() {
    if (turnPaging) {
      return;
    }
    if (getCurrentChapterIndex() == loader.toc().size() - 1) {
      Toast.success("没有下一页了~");
      return;
    }
    contents[0] = contents[1];
    contents[2] = null;
    loadContent(() -> {
      contents[1] = contents(2);
      currentChapterIndex.set(getCurrentChapterIndex() + 1);
      computePages(contents[1]);
      displayPage(0, TurnPageType.NEXT);
    }, 2);
  }

  /**
   * 跳转章节，跳转到第几章
   *
   * @param index 第几章
   */
  private void toChapter(int index) {
    clearCaches();
    currentChapterIndex.set(index);
    loadContent(() -> {
      contents[1] = contents(1);
      computePages(contents[1]);
      displayPage(0, TurnPageType.NONE);
    }, 1);
  }

  /**
   * 获取正文，不存在则抓取
   *
   * @param index 缓存索引
   * @return 正文
   */
  public String contents(int index) {
    if (contents[index] == null) {
      contents[index] = loader.loadContent(getCurrentChapterIndex() + index - 1);
    }
    return contents[index];
  }

  /**
   * 加载正文
   *
   * @param onSuccess 成功回调
   * @param indexes   加载的索引
   */
  public void loadContent(Runnable onSuccess, int... indexes) {
    boolean needLoad = false;
    for (int index : indexes) {
      if (!loader.isCached(getCurrentChapterIndex() + index - 1)) {
        needLoad = true;
        break;
      }
    }
    if (needLoad) {
      TaskFactory.create(() -> {
          for (int index : indexes) {
            contents(index);
          }
          return null;
        }).onSuccess(s -> onSuccess.run())
        .onFailed(e -> Toast.error(getRoot(), "加载失败"))
        .start();
    } else {
      onSuccess.run();
    }
  }

  /**
   * 计算当前能够显示多少字
   *
   * @param text 章节内容
   */
  public void computePages(String text) {
    if (text == null) {
      return;
    }
    List<String> pageList = new ArrayList<>();
    // 分页相关数据
    double lineSpacing = (Double) config.getLineSpacing().get();
    double fontsize = (Double) config.getFontSize().get();
    String fontFamily = config.getFontFamily().get();
    Font font = Font.font(fontFamily, fontsize);
    double width = currentPage.getWidth();
    Insets padding = currentPage.getLabelPadding();
    double height = currentPage.getHeight() - currentPage.snappedBottomInset() - currentPage.snappedTopInset()
      - currentPage.snapSizeY(padding.getTop()) - currentPage.snapSizeY(padding.getBottom());
    double heightWithTitle =
      height - currentPage.getTitle().getLayoutBounds().getHeight() - currentPage.getTitle().getGraphicTextGap();
    do {
      // 首页区分标题高度
      double pageHeight = pageList.isEmpty() ? heightWithTitle : height;
      String page = Utils.computeClippedWrappedText(font, text, width, pageHeight, lineSpacing, OverrunStyle.CLIP,
        CharSequenceUtil.EMPTY, TextBoundsType.LOGICAL_VERTICAL_CENTER);
      pageList.add(page);
      text = text.substring(page.length()).trim();
    } while (text.length() > 0);
    // 更新页码
    this.pages.clear();
    this.pages.addAll(pageList);
  }

  /**
   * 获取当前章节索引
   *
   * @return 索引
   */
  public int getCurrentChapterIndex() {
    return currentChapterIndex.get();
  }

  /**
   * 当前页的文字
   *
   * @return 文字
   */
  private String currentPageText() {
    if (current >= 0 && current < pages.size()) {
      String result = pages.get(current);
      return transformationSimpleTraditional(result);
    }
    return null;
  }

  /**
   * 简体与繁体互相转换
   *
   * @param src 原始字符
   * @return 转换后的
   */
  private String transformationSimpleTraditional(String src) {
    if (StringUtils.isBlank(src)) {
      return src;
    }
    // 简体与繁体互转
    Integer mode = config.getSimpleTraditional().get();
    mode = mode == null ? 0 : mode;
    if (mode == 1) {
      src = ZhConverterUtil.toSimple(src);
    } else if (mode == 2) {
      src = ZhConverterUtil.toTraditional(src);
    }
    return src;
  }

  /**
   * 清空页面缓存
   */
  private void clearCaches() {
    pages.clear();
    Arrays.fill(contents, null);
  }

  @FXML
  public void closeSetting() {
    settingDrawer.close();
  }

  /**
   * 朗读按钮点击
   */
  @FXML
  private void onSpeakClicked() {
    if (speakButton.isSelected()) {
      stopSpeaking();
    } else {
      startSpeaking();
    }
    settingDrawer.close();
  }

  /**
   * 播放/暂停点击
   */
  @FXML
  private void onPlayClicked() {
    if (playButton.isSelected()) {
      pauseTTS();
    } else {
      playTTS(false);
    }
    settingDrawer.close();
  }

  /**
   * 开始朗读
   */
  private void startSpeaking() {
    speakButton.setSelected(true);
    speakButton.setText("停止");
    playTTS(true);
    setSettingView(true);
    playButton.setVisible(true);
    playButton.setManaged(true);
  }

  /**
   * 停止朗读
   */
  private void stopSpeaking() {
    if (!speakButton.isSelected()) {
      return;
    }
    pauseTTS();
    speakButton.setSelected(false);
    speakButton.setText("朗读");
    player.dispose();
    setSettingView(false);
    playButton.setVisible(false);
    playButton.setManaged(false);
  }

  /**
   * 播放 TTS
   */
  private void playTTS(boolean isNew) {
    if (speakButton.isSelected()) {
      playButton.setSelected(true);
      playButton.setText("暂停");
      if (isNew) {
        String pageText = currentPageText();
        // 标题
        if (current == 0) {
          String titleText = loader.toc().get(getCurrentChapterIndex()).getName();
          pageText = titleText + StringUtils.LF + pageText;
        }
        player.setText(pageText);
        player.speak();
      } else {
        player.play();
      }
    }
  }

  /**
   * 暂停 TTS
   */
  private void pauseTTS() {
    if (speakButton.isSelected() && playButton.isSelected()) {
      playButton.setSelected(false);
      log.debug("暂停播放");
      playButton.setText("播放");
      player.pause();
    }
  }

  /**
   * 翻页类型
   */
  private enum TurnPageType {
    /**
     * 上一页
     */
    PRE,
    /**
     * 下一页
     */
    NEXT,
    /**
     * 未翻页
     */
    NONE
  }


  /**
   * 展示页码任务 防抖
   */
  static class DisplayPageTask extends DebounceTask {

    private int page = 0;
    private TurnPageType type = TurnPageType.NONE;

    public DisplayPageTask(Runnable runnable, Long delay) {
      super(runnable, delay);
    }

    public void init(int page, TurnPageType type) {
      this.page = page;
      this.type = type;
    }
  }
}
