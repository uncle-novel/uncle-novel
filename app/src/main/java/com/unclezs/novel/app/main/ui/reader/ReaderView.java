package com.unclezs.novel.app.main.ui.reader;

import cn.hutool.core.text.CharSequenceUtil;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.sun.javafx.scene.control.skin.Utils;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.appication.SceneView;
import com.unclezs.novel.app.framework.components.StageDecorator;
import com.unclezs.novel.app.framework.components.Toast;
import com.unclezs.novel.app.framework.components.icon.IconButton;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.dao.BookDao;
import com.unclezs.novel.app.main.loader.AbstractBookLoader;
import com.unclezs.novel.app.main.loader.BookLoader;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.ui.home.HomeView;
import com.unclezs.novel.app.main.ui.home.views.TocListCell;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextBoundsType;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 12:13
 */
@FxView(fxml = "/layout/reader/reader.fxml")
public class ReaderView extends SceneView<StageDecorator> {


  public static final String BUNDLE_READ_BOOK_KEY = "read-book-key";
  public JFXDrawer tocDrawer;
  public JFXDrawersStack drawer;
  public ListView<Chapter> tocListView;
  public Button hide;
  public Button show;
  public StackPane container;
  public Label content;
  public Button preChapter;
  public Button nextChapter;
  public Button pre;
  public Button next;
  public Button showToc;
  List<String> pages = new ArrayList<>();
  int current = 0;
  private Book book;
  private AbstractBookLoader loader;
  private String[] contents = new String[3];
  private int currentChapterIndex = 0;

  @Override
  public void onCreated() {
    System.out.println("ReaderView created");
    tocListView.setCellFactory(param -> new TocListCell());
    EventUtils.setOnMousePrimaryClick(tocListView, e -> {
      if (!tocListView.getSelectionModel().isEmpty()) {
        loadChapter(tocListView.getSelectionModel().getSelectedIndex());
        drawer.toggle(tocDrawer);
      }
    });
    show.setOnAction(e -> {
      getRoot().showHeader();
    });
    hide.setOnAction(event -> {
      getRoot().hideHeader();
    });
    getRoot().hideHeader();
    content.setStyle("-fx-background-color: #808A87");
    container.widthProperty().addListener(e -> {
      System.out.println("width:" + container.getWidth());
      System.out.println("height:" + container.getHeight());
      pages = computePages(contents[1]);
      loadPage(current);
    });
    container.heightProperty().addListener(e -> {
      System.out.println("width:" + container.getWidth());
      System.out.println("height:" + container.getHeight());
      pages = computePages(contents[1]);
      loadPage(current);
    });
  }

  @Override
  public void onShow(SceneNavigateBundle bundle) {
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
      currentChapterIndex = book.getCurrentChapterIndex();
//      contents[1] = loader.loadContent(40);
//      content.setText(contents(1));
    }
  }

  public void toHome() {
    app.navigate(HomeView.class, new SceneNavigateBundle().put("data", "reader"));
  }

  @Override
  public void onTheme(StageDecorator view, IconButton themeButton) {
    System.out.println("主题被点击1");
  }

  @Override
  public void onHidden() {
    System.out.println("ReaderView hidden");
  }

  @Override
  public void onClose(StageDecorator view, IconButton closeButton) {
//    toHome();
  }

  @Override
  public void onDestroy() {
    System.out.println("ReaderView destroy");
  }

  private void loadPage(int page) {
    while (page >= pages.size() && page != 0) {
      page--;
    }
    content.setText(pages.get(page));
  }

  @FXML
  private void prePage() {
    if (current > 0) {
      loadPage(--current);
    } else {
      preChapter();
    }
  }

  @FXML
  private void nextPage() {
    if (current < pages.size() - 1) {
      loadPage(++current);
    } else {
      nextChapter();
    }
  }

  @FXML
  private void preChapter() {
    if (currentChapterIndex == 0) {
      Toast.success("没有上一页了~");
      return;
    }
    contents[2] = contents[1];
    contents[1] = contents(0);
    contents[0] = null;
    currentChapterIndex--;
    if (currentChapterIndex > 0) {
      contents[0] = contents(0);
    }
    pages = computePages(contents[1]);
    loadPage(0);
  }

  @FXML
  private void nextChapter() {
    if (currentChapterIndex == loader.toc().size() - 1) {
      Toast.success("没有下一页了~");
      return;
    }
    contents[0] = contents[1];
    contents[1] = contents(2);
    contents[2] = null;
    currentChapterIndex++;
    if (currentChapterIndex < loader.toc().size() - 1) {
      contents[2] = contents(2);
    }
    pages = computePages(contents[1]);
    loadPage(0);
  }

  private void loadChapter(int index) {
    contents[0] = null;
    contents[1] = null;
    contents[2] = null;
    currentChapterIndex = index;
    contents[1] = contents(1);
    pages = computePages(contents[1]);
    loadPage(0);
  }

  public String contents(int index) {
    if (contents[index] == null) {
      contents[index] = loader.loadContent(currentChapterIndex + index - 1);
    }
    return contents[index];
  }

  /**
   * 计算当前能够显示多少字
   *
   * @param text 章节内容
   * @return 分页后列表
   */
  public List<String> computePages(String text) {
    List<String> pageList = new ArrayList<>();
    int index = 0;
    do {
      String p = Utils.computeClippedWrappedText(content.getFont(), text.substring(index), container.getWidth(), container.getHeight(), content.getLineSpacing(),
        content.getTextOverrun(), CharSequenceUtil.EMPTY, TextBoundsType.LOGICAL_VERTICAL_CENTER);
      pageList.add(p);
      index += p.length();
    } while (index < text.length());
    for (String page : pageList) {
      System.out.println(page);
      System.out.println("=================");
    }
    return pageList;
  }

  @FXML
  private void showToc() {
    tocListView.getSelectionModel().select(currentChapterIndex);
    drawer.toggle(tocDrawer);
  }
}
