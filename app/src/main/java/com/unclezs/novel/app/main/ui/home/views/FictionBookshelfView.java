package com.unclezs.novel.app.main.ui.home.views;

import cn.hutool.core.io.FileUtil;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTabPane;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.annotation.FxView;
import com.unclezs.novel.app.framework.appication.SceneNavigateBundle;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.components.sidebar.SidebarNavigateBundle;
import com.unclezs.novel.app.framework.components.sidebar.SidebarView;
import com.unclezs.novel.app.framework.core.AppContext;
import com.unclezs.novel.app.framework.util.EventUtils;
import com.unclezs.novel.app.main.dao.BookDao;
import com.unclezs.novel.app.main.manager.ResourceManager;
import com.unclezs.novel.app.main.model.Book;
import com.unclezs.novel.app.main.model.BookBundle;
import com.unclezs.novel.app.main.model.BookCache;
import com.unclezs.novel.app.main.ui.home.HomeView;
import com.unclezs.novel.app.main.ui.home.views.widgets.BookNode;
import com.unclezs.novel.app.main.ui.reader.ReaderView;
import com.unclezs.novel.app.main.util.BookHelper;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import lombok.EqualsAndHashCode;

/**
 * 小说书架
 *
 * @author blog.unclezs.com
 * @date 2021/4/25 9:40
 */
@FxView(fxml = "/layout/home/views/fiction-bookshelf.fxml")
@EqualsAndHashCode(callSuper = true)
public class FictionBookshelfView extends SidebarView<StackPane> {

  public static final String BUNDLE_BOOK_KEY = "bundle-book-key";
  public static final String GROUP_ALL = "全部";
  private static final String CACHE_FOLDER_NAME = "book";
  public static final File CACHE_FOLDER = ResourceManager.cacheFile(CACHE_FOLDER_NAME);
  private final ObservableList<BookNode> bookNodes = FXCollections.observableArrayList();
  private final BookDao bookDao = new BookDao();
  private final ObservableSet<String> groups = FXCollections.observableSet();
  @FXML
  private JFXTabPane groupPanel;
  @FXML
  private JFXMasonryPane bookPanel;
  @FXML
  private ContextMenu bookNodeContextMenu;
  @FXML
  private ContextMenu groupTabContextMenu;

  @Override
  public void onCreated() {
    // 分组初始化
    groups.addListener((SetChangeListener<String>) change -> {
      if (change.wasAdded()) {
        addGroup(change.getElementAdded());
      }
      if (change.wasRemoved()) {
        deleteGroup(change.getElementRemoved());
      }
    });
    groups.add(GROUP_ALL);
    // 查库获取书籍列表
    bookDao.selectAll().forEach(book -> {
      this.addBook(book);
      if (book.getGroup() != null) {
        this.groups.add(book.getGroup());
      }
    });
    // 书籍节点变化监听
    bookNodes.addListener((ListChangeListener<BookNode>) c -> {
      while (c.next()) {
        c.getRemoved().forEach(bookNode -> {
          bookPanel.getChildren().remove(bookNode);
          bookDao.delete(bookNode.getBook());
        });
        c.getAddedSubList().forEach(bookNode -> {
          bookDao.save(bookNode.getBook());
          String currentGroup = groupPanel.getTabs().stream().filter(Tab::isSelected).map(Tab::getText).findFirst().orElse(null);
          if (bookNode.getBook().getGroup() != null) {
            groups.add(bookNode.getBook().getGroup());
          } else if (Objects.equals(currentGroup, GROUP_ALL) || Objects.equals(currentGroup, bookNode.getBook().getGroup())) {
            bookPanel.getChildren().add(bookNode);
          }
        });
      }
      bookPanel.requestLayout();
    });
    // 初始化节点
    bookPanel.getChildren().setAll(bookNodes);
  }

  @Override
  public void onShow(SidebarNavigateBundle bundle) {
    BookBundle bookBundle = bundle.get(BUNDLE_BOOK_KEY);
    if (bookBundle != null) {
      Book book = Book.fromBookBundle(bookBundle);
      // 封面
      BookHelper.downloadCover(book.getCover(), book.getUrl(), FileUtil.file(CACHE_FOLDER, book.getId()), cover -> {
        book.setCover(cover);
        bookDao.update(book);
      });
      // 缓存章节
      cacheBook(book);
      addBook(book);
    }
  }

  /**
   * 添加一本书到书架
   *
   * @param book 书籍
   */
  private void addBook(Book book) {
    BookNode bookNode = new BookNode(book);
    bookNode.setOnContextMenuRequested(e -> {
      bookNodeContextMenu.show(bookNode, e.getScreenX(), e.getScreenY());
      e.consume();
    });
    EventUtils.setOnMousePrimaryClick(bookNode, e -> {
      SceneNavigateBundle bundle = new SceneNavigateBundle();
      bundle.put(ReaderView.BUNDLE_READ_BOOK_KEY, book);
      AppContext.getView(HomeView.class).getApp().navigate(ReaderView.class, bundle);
    });
    bookNodes.add(bookNode);
  }

  /**
   * 缓存书籍
   *
   * @param book 书籍
   */
  private void cacheBook(Book book) {
    BookHelper.cache(new BookCache(book.getRule(), book.getToc()), FileUtil.file(CACHE_FOLDER, book.getId()));
  }

  /**
   * 添加分组tab
   *
   * @param name 分组名字
   */
  private void addGroup(String name) {
    if (name == null) {
      return;
    }
    Tab tab = new Tab(name);
    // 切换分组
    tab.setOnSelectionChanged(e -> {
      if (tab.isSelected()) {
        if (GROUP_ALL.equals(name)) {
          bookPanel.getChildren().setAll(bookNodes);
        } else {
          List<BookNode> nodes = this.bookNodes.stream()
            .filter(bookNode -> name.equals(bookNode.getBook().getGroup()))
            .collect(Collectors.toList());
          bookPanel.getChildren().setAll(nodes);
        }
        tab.setContent(bookPanel);
      } else {
        tab.setContent(null);
      }
    });
    if (!GROUP_ALL.equals(name)) {
      // 上下文菜单
      tab.setContextMenu(groupTabContextMenu);
    } else {
      tab.setContent(bookPanel);
    }
    groupPanel.getTabs().add(tab);
  }

  /**
   * 设置分组
   */
  @FXML
  private void setGroup() {
    BookNode node = (BookNode) bookNodeContextMenu.getOwnerNode();
    Book book = node.getBook();
    ComboBox<String> groupSelector = new ComboBox<>();
    groupSelector.setMaxWidth(Double.MAX_VALUE);
    groupSelector.setEditable(true);
    groupSelector.setValue(book.getGroup());
    groupSelector.getItems().addAll(groups);
    ModalBox.confirm(save -> {
      if (Boolean.TRUE.equals(save)) {
        String group = groupSelector.getValue();
        if (StringUtils.isNotBlank(group)) {
          groups.add(group);
        } else {
          group = null;
        }
        // 改名后是否还属于当前的分组
        if (!Objects.equals(book.getName(), group) && book.getGroup() != null) {
          bookPanel.getChildren().remove(node);
        }
        node.getBook().setGroup(group);
        bookDao.update(book);
      }
    }).title("设置分组").body(groupSelector).show();
  }

  /**
   * 上下文菜单触发删除
   */
  @FXML
  private void deleteGroup() {
    String group = ((Label) groupTabContextMenu.getOwnerNode()).getText();
    groups.remove(group);
  }

  /**
   * 删除分组 tab、book的分组
   *
   * @param group 分组
   */
  private void deleteGroup(String group) {
    if (group == null) {
      return;
    }
    bookNodes.forEach(bookNode -> {
      if (group.equals(bookNode.getBook().getGroup())) {
        bookNode.getBook().setGroup(null);
      }
    });
    groupPanel.getTabs().removeIf(tab -> group.equals(tab.getText()));
  }
}
