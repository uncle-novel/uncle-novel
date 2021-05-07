package com.unclezs.novel.app.main.ui.home.views.widgets;

import com.unclezs.novel.app.framework.components.LoadingImageView;
import com.unclezs.novel.app.framework.util.NodeHelper;
import com.unclezs.novel.app.main.model.Book;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;

/**
 * 书籍节点
 *
 * @author blog.unclezs.com
 * @date 2021/5/7 0:49
 */
public class BookNode extends StackPane {

  private static final DataFormat BOOK_NODE_MIME_TYPE = new DataFormat("application/book-node");
  private final LoadingImageView cover = new LoadingImageView(BookListCell.NO_COVER, 95, 120);
  private final Label title = NodeHelper.addClass(new Label(), "title");
  private final Label tip = NodeHelper.addClass(new Label("发现更新"), "tip");
  @Getter
  private final Book book;

  public BookNode(Book book) {
    this.book = book;
    NodeHelper.addClass(this, "book-node");

    StackPane.setAlignment(title, Pos.BOTTOM_CENTER);
    StackPane.setAlignment(tip, Pos.TOP_RIGHT);
    cover.setImage(book.getCover());
    title.setText(book.getName());
    StackPane stackPane = NodeHelper.addClass(new StackPane(cover, title, tip), "book-node-container");
    getChildren().setAll(stackPane);

    stackPane.setOnDragDetected(e -> {
      Dragboard dragboard = this.startDragAndDrop(TransferMode.COPY_OR_MOVE);
      ClipboardContent clipboardContent = new ClipboardContent();
      SnapshotParameters snapshotParameters = new SnapshotParameters();
      WritableImage snapshot = cover.snapshot(snapshotParameters, null);
      clipboardContent.putImage(snapshot);
      clipboardContent.put(BOOK_NODE_MIME_TYPE, getParent().getChildrenUnmodifiable().indexOf(this));
      dragboard.setContent(clipboardContent);
    });

    stackPane.setOnDragOver(e -> {
      Dragboard db = e.getDragboard();
      if (db.hasContent(BOOK_NODE_MIME_TYPE)) {
        Integer from = (Integer) db.getContent(BOOK_NODE_MIME_TYPE);
        int to = getParent().getChildrenUnmodifiable().indexOf(this);
        if (from != to) {
          e.acceptTransferModes(TransferMode.MOVE);
          e.consume();
        }
      }
    });

    stackPane.setOnDragDropped(e -> {
      Dragboard db = e.getDragboard();
      if (db.hasContent(BOOK_NODE_MIME_TYPE)) {
        Pane parent = (Pane) this.getParent();
        int index = (int) db.getContent(BOOK_NODE_MIME_TYPE);
        int current = parent.getChildren().indexOf(this);
        Node node = parent.getChildren().get(index);
        parent.getChildren().remove(index);
        parent.getChildren().add(current, node);
      }
    });
  }

  private Pane parent() {
    return (Pane) getParent();
  }
}
