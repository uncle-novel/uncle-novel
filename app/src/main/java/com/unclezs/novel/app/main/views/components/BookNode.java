package com.unclezs.novel.app.main.views.components;

import com.jfoenix.controls.JFXSpinner;
import com.unclezs.novel.app.framework.components.LoadingImageView;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.db.beans.Book;
import com.unclezs.novel.app.main.manager.SettingManager;
import com.unclezs.novel.app.main.views.components.cell.BookListCell;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.Getter;

/**
 * 书籍节点
 *
 * @author blog.unclezs.com
 * @since 2021/5/7 0:49
 */
public class BookNode extends StackPane {

  public static final String SHOW_TITLE_STYLE_CSS = "show-title";
  private final LoadingImageView cover = new LoadingImageView(BookListCell.NO_COVER, 95, 120);
  private final Label title = NodeHelper.addClass(new Label(), "title");
  private final Label tip = NodeHelper.addClass(new Label("发现更新"), "tip");
  @Getter
  private final Book book;
  private final StackPane container;
  private JFXSpinner updating;

  public BookNode(Book book) {
    this.book = book;
    NodeHelper.addClass(this, "book-node");
    StackPane.setAlignment(title, Pos.BOTTOM_CENTER);
    StackPane.setAlignment(tip, Pos.TOP_RIGHT);
    cover.setImage(book.getCover());
    title.setText(book.getName());
    tip.setVisible(book.isUpdate());
    container = NodeHelper.addClass(new StackPane(cover, title, tip), "book-node-container");
    getChildren().setAll(container);
    showTitle(SettingManager.manager().getBookShelf().getAlwaysShowBookTitle().get());
  }

  /**
   * 设置标题
   *
   * @param title 名称
   */
  public void setTitle(String title) {
    book.setName(title);
    this.title.setText(title);
  }

  /**
   * 设置封面
   *
   * @param cover 封面
   */
  public void setCover(String cover) {
    book.setCover(cover);
    this.cover.setImage(cover);
  }

  /**
   * 显示更新提示
   *
   * @param update 更新了
   */
  public void setUpdate(boolean update) {
    tip.setVisible(update);
    book.setUpdate(update);
  }

  /**
   * 设置更新状态
   *
   * @param running 状态
   */
  public void setUpdateTaskState(boolean running) {
    if (updating == null) {
      updating = new JFXSpinner();
      updating.setRadius(5);
      StackPane.setAlignment(updating, Pos.TOP_LEFT);
      container.getChildren().add(updating);
    }
    updating.setVisible(running);
  }

  /**
   * 总是显示标题
   *
   * @param show true显示
   */
  public void showTitle(boolean show) {
    title.getStyleClass().remove(SHOW_TITLE_STYLE_CSS);
    if (show) {
      title.getStyleClass().add(SHOW_TITLE_STYLE_CSS);
    }
  }
}
