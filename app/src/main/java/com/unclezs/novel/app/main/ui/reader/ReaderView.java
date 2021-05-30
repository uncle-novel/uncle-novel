package com.unclezs.novel.app.main.ui.reader;

import cn.hutool.core.text.CharSequenceUtil;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import com.sun.javafx.scene.control.skin.Utils;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.SelectableButton;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.TabGroup;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.dao.BookDao;
import com.unclezs.novel.app.main.loader.AbstractBookLoader;
import com.unclezs.novel.app.main.loader.BookLoader;
import com.unclezs.novel.app.main.loader.TxtLoader;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.config.ReaderConfig;
import com.unclezs.novel.app.main.model.config.TTSConfig;
import com.unclezs.novel.app.main.ui.home.HomeView;
import com.unclezs.novel.app.main.ui.home.views.TocListCell;
import com.unclezs.novel.app.main.ui.reader.player.TTSPlayer;
import com.unclezs.novel.app.main.ui.reader.views.PageView;
import com.unclezs.novel.app.main.ui.reader.views.ReaderThemeView;
import com.unclezs.novel.app.main.ui.reader.views.widgets.ReaderContextMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import lombok.extern.slf4j.Slf4j;

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
  public static final double PRE_PAGE_AREA = 0.35;
  public static final double NEXT_PAGE_AREA = 0.65;
  private final String[] contents = new String[3];
  /**
   * 当前章节所有页
   */
  private final List<String> pages = new ArrayList<>();
  private final IntegerProperty currentChapterIndex = new SimpleIntegerProperty();
  private final BookDao bookDao = new BookDao();
  @FXML
  private ListView<Chapter> tocListView;
  @FXML
  private SelectableButton speakButton;
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
    System.out.println("ReaderView created");
    this.config = SettingManager.manager().getReader();
    getRoot().getScene().getStylesheets().add("css/reader/reader.css");
    // todo 增加shadow隐藏设置
    // getRoot().getStyleClass().add("no-window-shadow");
    // getRoot().getStyleClass().add("no-header-shadow");

    this.contextMenu = new ReaderContextMenu();
    initSetting();
    // 初始化阅读器默认行为
    initBehavior();
    tocListView.setCellFactory(param -> new TocListCell(loader::isCached));
    EventUtils.setOnMousePrimaryClick(tocListView, e -> {
      if (!tocListView.getSelectionModel().isEmpty()) {
        toChapter(tocListView.getSelectionModel().getSelectedIndex());
        drawer.toggle(tocDrawer);
      }
    });
    App.stage().setWidth(config.getStageWidth().get());
    App.stage().setHeight(config.getStageHeight().get());
  }

  @Override
  public void onShow(SceneNavigateBundle bundle) {
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
    // 回到首页
    app.navigate(HomeView.class, new SceneNavigateBundle());
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    drawer.toggle(settingDrawer);
  }

  @Override
  public void onDestroy() {
    System.out.println("ReaderView destroy");
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
        } else if (clickX < width * PRE_PAGE_AREA || (clickY < height * PRE_PAGE_AREA && clickX < width * NEXT_PAGE_AREA)) {
          prePage();
          // 下一页
        } else if (clickX > width * NEXT_PAGE_AREA || (clickY > height * NEXT_PAGE_AREA && clickX > width * PRE_PAGE_AREA)) {
          nextPage();
        } else {
          // 显示设置
          drawer.toggle(settingDrawer);
        }
        event.consume();
      }
      if (contextMenu.isShowing()) {
        contextMenu.hide();
      }
    });
    // 上下文菜单
    container.setOnContextMenuRequested(event -> contextMenu.show(getRoot(), event.getScreenX(), event.getScreenY()));
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
    chapterSlider.setValueFactory(slider -> Bindings.createStringBinding(() -> ((int) slider.getValue() + 1) + "/" + ((int) slider.getMax() + 1), slider.valueProperty()));
    chapterSlider.setOnMouseClicked(e -> {
      currentChapterIndex.set((int) chapterSlider.getValue());
      toChapter(getCurrentChapterIndex());
    });
    // 页面宽度
    container.widthProperty().addListener(e -> {
      forEachPageView(view -> {
        if (view.getTranslateX() < 0) {
          view.setTranslateX(-container.getWidth());
        }
      });
    });
    forEachPageView(pageView -> {
      pageView.widthProperty().addListener(e -> updateDisplayText());
      pageView.heightProperty().addListener(e -> updateDisplayText());
      pageView.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> container.getWidth() * pageWidthSlider.getValue(), container.widthProperty(), pageWidthSlider.valueProperty()));
    });
    pageWidthSlider.valueProperty().bindBidirectional(config.getPageWidth());
    pageWidthSlider.setValueFactory(slider -> Bindings.createStringBinding(() -> ((int) (slider.getValue() * 100)) + "%", slider.valueProperty()));
    // 对齐方式
    alignGroup.setOnSelected(tabButton -> {
      String align = tabButton.getUserData().toString();
      forEachPageView(pageView -> pageView.setTextAlignment(TextAlignment.valueOf(align)));
      config.getAlign().set(align);
    });
    alignGroup.findTab(config.getAlign().get()).setSelected(true);
    // 字体选择器
    fontSelector.getItems().setAll(Font.getFamilies());
    fontSelector.valueProperty().bindBidirectional(config.getFontFamily());
    fontSelector.valueProperty().addListener(e -> setFont());
    // 字体大小选择器
    fontSizeSlider.valueProperty().bindBidirectional(config.getFontSize());
    fontSizeSlider.valueProperty().addListener(e -> setFont());
    // 初始化字体
    setFont();
    // 行间距选择器
    lineSpaceSlider.valueProperty().bindBidirectional(config.getLineSpacing());
    lineSpaceSlider.valueProperty().addListener(e -> {
      forEachPageView(content -> content.setLineSpacing(lineSpaceSlider.getValue()));
      updateDisplayText();
    });
    forEachPageView(pageView -> pageView.setLineSpacing(lineSpaceSlider.getValue()));
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

  private void setFont() {
    forEachPageView(content -> {
      content.setStyle(String.format(FONT_STYLE_FORMAT, fontSizeSlider.getValue(), fontSelector.getValue()));
      content.getTitle().setStyle(String.format(FONT_STYLE_FORMAT, fontSizeSlider.getValue() + 12, fontSelector.getValue()));
    });
    updateDisplayText();
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
   * 显示一页
   *
   * @param page 页码
   */
  private void displayPage(int page, TurnPageType type) {
    if (turnPaging) {
      return;
    }
    turnPaging = true;
    while (page >= pages.size() && page != 0) {
      page--;
    }
    current = page;
    PageView showView;
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
  }

  /**
   * 显示目录
   */
  @FXML
  private void showToc() {
    tocListView.getSelectionModel().select(getCurrentChapterIndex());
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
    double height = currentPage.getHeight() - currentPage.snappedBottomInset() - currentPage.snappedTopInset() - currentPage.snapSizeY(padding.getTop()) - currentPage.snapSizeY(padding.getBottom());
    double heightWithTitle = height - currentPage.getTitle().getLayoutBounds().getHeight() - currentPage.getTitle().getGraphicTextGap();

    do {
      // 首页区分标题高度
      double pageHeight = pageList.isEmpty() ? heightWithTitle : height;
      String page = Utils.computeClippedWrappedText(font, text, width, pageHeight, lineSpacing, OverrunStyle.CLIP, CharSequenceUtil.EMPTY, TextBoundsType.LOGICAL_VERTICAL_CENTER);
      pageList.add(page);
      text = text.substring(page.length()).trim();
    } while (text.length() > 0);
    // 更新页码
    this.pages.clear();
    this.pages.addAll(pageList);
  }

  public int getCurrentChapterIndex() {
    return currentChapterIndex.get();
  }

  public void speak(ActionEvent event) {
    SelectableButton button = (SelectableButton) event.getSource();
    button.setSelected(!button.isSelected());
    if (button.isSelected()) {
      getPlayer().speak(currentPageText());
    } else {
      getPlayer().pause();
    }
  }

  /**
   * TTS播放器
   *
   * @return TTS播放器
   */
  private TTSPlayer getPlayer() {
    if (player == null) {
      TTSConfig config = new TTSConfig();
      RequestParams params = RequestParams.create("http://tts.baidu.com/text2audio");
      params.setBody("tex={{text}}&per=4007&cuid=baidu_speech_demo&idx=1&cod=2&lan=zh&ctp=1&pdt=160&vol=5&aue=3&pit=5&_res_tag_=audio");
      params.setMethod("POST");
      config.setParams(params);
      config.setName("台湾女声");
      player = new TTSPlayer(config, () -> {
        nextPage();
        player.speak(currentPageText());
      });
    }
    return player;
  }

  /**
   * 当前页的文字
   *
   * @return 文字
   */
  private String currentPageText() {
    if (current >= 0 && current < pages.size()) {
      return pages.get(current);
    }
    return null;
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
}
