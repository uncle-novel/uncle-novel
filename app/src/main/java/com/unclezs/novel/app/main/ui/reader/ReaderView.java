package com.unclezs.novel.app.main.ui.reader;

import cn.hutool.core.text.CharSequenceUtil;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import com.sun.javafx.scene.control.skin.Utils;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.SelectableButton;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.TabGroup;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.framework.executor.TaskFactory;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.App;
import com.unclezs.novel.app.main.dao.BookDao;
import com.unclezs.novel.app.main.loader.AbstractBookLoader;
import com.unclezs.novel.app.main.loader.BookLoader;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.ReaderConfig;
import com.unclezs.novel.app.main.ui.home.views.TocListCell;
import com.unclezs.novel.app.main.ui.reader.views.PageView;
import com.unclezs.novel.app.main.ui.reader.views.ReaderThemeView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@FxView(fxml = "/layout/reader/reader.fxml")
public class ReaderView extends SceneView<StageDecorator> {


  public static final String BUNDLE_READ_BOOK_KEY = "read-book-key";
  public static final String FONT_STYLE_FORMAT = "-fx-font-size: %spx;-fx-font-family: '%s'";
  public static final String TITLE_STYLE_CLASS = "show-title";
  private final String[] contents = new String[3];
  /**
   * 当前章节所有页
   */
  private final List<String> pages = new ArrayList<>();
  public ListView<Chapter> tocListView;
  public Button hide;
  public Button show;
  public Button preChapter;
  public Button nextChapter;
  public Button pre;
  public Button next;
  public Button showToc;
  public Label title;
  private int current = -1;
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
  private Book book;
  private AbstractBookLoader loader;
  private IntegerProperty currentChapterIndex = new SimpleIntegerProperty();
  private ReaderConfig config;
  /**
   * 翻页中
   */
  private boolean turnPaging;
  /**
   * 页面
   */
  private PageView currentPage;
  private PageView otherPage;

  @Override
  public void onCreated() {
    System.out.println("ReaderView created");
    this.config = SettingManager.manager().getReader();
    getRoot().getScene().getStylesheets().add("css/reader/reader.css");

    this.currentPage = new PageView(container);
    this.otherPage = new PageView(container);
    container.getChildren().add(0, otherPage);
    container.getChildren().add(1, currentPage);

    initSetting();
    tocListView.setCellFactory(param -> new TocListCell());
    EventUtils.setOnMousePrimaryClick(tocListView, e -> {
      if (!tocListView.getSelectionModel().isEmpty()) {
        toChapter(tocListView.getSelectionModel().getSelectedIndex());
        drawer.toggle(tocDrawer);
      }
    });
    show.setOnAction(e -> getRoot().showHeader());
    hide.setOnAction(event -> getRoot().hideHeader());
  }

  @Override
  public void onShow(SceneNavigateBundle bundle) {
    App.stage().setWidth(config.getStageWidth().get());
    App.stage().setHeight(config.getStageHeight().get());
    Book bundleBook = bundle.get(BUNDLE_READ_BOOK_KEY);
    List<Book> books = new BookDao().selectAll();
    if (books.isEmpty()) {
      return;
    }
    bundleBook = books.get(0);
    if (bundleBook != null && book != bundleBook) {
      this.book = bundleBook;
      loader = new BookLoader(book);
      tocListView.getItems().setAll(loader.toc());
      getRoot().setTitle(book.getName());
      contents[1] = loader.loadContent(book.getCurrentChapterIndex());
      chapterSlider.setMax(loader.toc().size());
      current = 0;
    }
  }

  @Override
  public void onHidden() {
    System.out.println("ReaderView hidden");
  }

  @Override
  public void onClose(StageDecorator view, IconButton closeButton) {
    config.getStageWidth().set(App.stage().getWidth());
    config.getStageHeight().set(App.stage().getHeight());
    super.onClose(view, closeButton);
    // 回到首页
    // app.navigate(HomeView.class, new SceneNavigateBundle().put("data", "reader"));
  }

  @Override
  public void onSetting(StageDecorator view, IconButton settingButton) {
    drawer.toggle(settingDrawer);
  }

  @Override
  public void onDestroy() {
    System.out.println("ReaderView destroy");
  }

  private void initSetting() {
    // 设置主题
    themeView.changeTheme(config.getThemeName().get());
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
      forEachPageView(content -> content.setTextAlignment(TextAlignment.valueOf(align)));
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
    forEachPageView(content -> content.setLineSpacing(lineSpaceSlider.getValue()));
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
    Transition transition = null;
    switch (type) {
      case NEXT:
        showView = otherPage;
        container.getChildren().remove(otherPage);
        container.getChildren().add(0, otherPage);
        transition = currentPage.getNextTransition();
        break;
      case PRE:
        showView = otherPage;
        container.getChildren().remove(otherPage);
        container.getChildren().add(1, otherPage);
        transition = otherPage.getPreTransition();
        break;
      case NONE:
      default:
        showView = currentPage;
    }
    // 设置显示页面的文字
    showView.setText(pages.get(page));
    // 第一页标题处理
    if (page == 0) {
      String titleText = loader.toc().get(getCurrentChapterIndex()).getName();
      showView.setTitle(titleText);
    } else {
      showView.setTitle(null);
    }
    if (transition != null) {
      otherPage.setTranslateX(0);
      currentPage.setTranslateX(0);
      Transition finalTransition = transition;
      Executor.runFx(() -> {
        PageView tmp = currentPage;
        currentPage = otherPage;
        otherPage = tmp;
        finalTransition.play();
      });
      finalTransition.setOnFinished(e -> turnPaging = false);
    } else {
      turnPaging = false;
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
  private void preChapter() {
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
  private void nextChapter() {
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
    contents[0] = null;
    contents[1] = null;
    contents[2] = null;
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
      if (contents[index] == null && !loader.isCached(getCurrentChapterIndex() + index - 1)) {
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
    double heightWithTitle = currentPage.getHeight() - currentPage.getTitle().getLayoutBounds().getHeight();
    double height = currentPage.getHeight();

    System.out.println("页面宽度：" + width);
    System.out.println("页面高度：" + height);
    System.out.println("带标题页面高度：" + heightWithTitle);
    int index = 0;
    do {
      // 首页区分标题高度
      double pageHeight = index == 0 ? heightWithTitle : height;
      String page = Utils.computeClippedWrappedText(font, text.substring(index), width, pageHeight, lineSpacing, OverrunStyle.CLIP, CharSequenceUtil.EMPTY, TextBoundsType.LOGICAL_VERTICAL_CENTER);
      pageList.add(page);
      index += page.length();
    } while (index < text.length());
//    for (String page : pageList) {
//      System.out.println(page.substring(0, 10));
//      System.out.println(page.substring(page.length() - 10));
//      System.out.println("=================");
//    }
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
