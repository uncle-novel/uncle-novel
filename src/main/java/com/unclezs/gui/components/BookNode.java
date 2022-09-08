package com.unclezs.gui.components;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.gui.controller.BookShelfController;
import com.unclezs.gui.utils.ContentUtil;
import com.unclezs.model.Book;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

import java.io.IOException;

/**
 * 书架书节点
 *
 * @author unclezs.com
 * @date 2019.06.22 09:54
 */
@Getter
public class BookNode extends VBox {
    private BookShelfController controller;
    private Book book;
    private ImageView cover;
    private Label title;

    public BookNode(Book book) {
        this.book = book;
        if (StrUtil.isNotEmpty(book.getCover()) && FileUtil.exist(book.getCover())) {
            cover = new ImageView(new Image("file:" + book.getCover()));
        } else {
            cover = new ImageView(new Image(getClass().getResourceAsStream("/images/non_cover.png")));
        }
        cover.setFitHeight(120);
        cover.setFitWidth(95);
        cover.setSmooth(true);
        Rectangle clip = new Rectangle(95, 120);
        clip.setArcHeight(10);
        clip.setArcWidth(10);
        cover.setClip(clip);
        title = new Label(book.getName());
        this.getChildren().addAll(cover, title);
        initStyleClass();
        this.setOnMouseClicked(this::onClick);
        this.setOnContextMenuRequested(this::handleContextMenu);
    }

    private void initStyleClass() {
        this.getStyleClass().add("book-node");
        cover.getStyleClass().add("book-node-cover");
        title.getStyleClass().add("book-node-title");
    }

    private void handleContextMenu(ContextMenuEvent event) {
        if (controller != null) {
            controller.contextMenu.hide();
            controller.contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        }
    }

    private void onClick(MouseEvent e) {
        if (controller == null) {
            try {
                controller = ContentUtil.getController(BookShelfController.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        controller.selectedBook = book;
        controller.selectedNode = this;
        if (e.getButton() == MouseButton.PRIMARY) {
            controller.open();
        }
    }
}
