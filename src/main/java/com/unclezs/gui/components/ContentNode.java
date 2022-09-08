package com.unclezs.gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

/**
 * 阅读器章节内容与标题
 *
 * @author uncle
 * @date 2020/5/14 17:48
 */
@Getter
public class ContentNode extends VBox {
    private Label title;
    private Label content;
    private int chapterIndex;

    public ContentNode(String title, String content, int chapterIndex) {
        this.chapterIndex = chapterIndex;
        this.title = new Label(title);
        this.content = new Label(content);
        this.getChildren().addAll(this.title, this.content);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.title.setTextAlignment(TextAlignment.CENTER);
        initStyleClass();
    }

    /**
     * 更新内容
     *
     * @param title        /
     * @param content      /
     * @param chapterIndex /
     */
    public void update(String title, String content, int chapterIndex) {
        this.chapterIndex = chapterIndex;
        this.title.setText(title);
        this.content.setText(content);
    }

    private void initStyleClass() {
        title.getStyleClass().add("chapter-title");
        content.getStyleClass().addAll("chapter-content", "bg-transparent");
    }


    @Override
    public String toString() {
        return String.valueOf(chapterIndex);
    }
}
