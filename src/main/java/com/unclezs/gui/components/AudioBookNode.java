package com.unclezs.gui.components;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.model.AudioBook;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;


/**
 * 有声小说书架节点
 *
 * @author unclezs.com
 * @date 2019.07.02 17:51
 */
@Getter
@Setter
public class AudioBookNode extends HBox {
    /**
     * 小说信息
     */
    private AudioBook info;
    private Label record;
    private ImageView cover;
    private Label title;
    private Label author;
    private Label speak;

    /**
     * @param info 小说信息
     */
    public AudioBookNode(AudioBook info) {
        this.info = info;
        init(info);
    }

    private void init(AudioBook info) {
        if (StrUtil.isNotEmpty(info.getCover()) && FileUtil.exist(info.getCover())) {
            cover = new ImageView(new Image("file:" + info.getCover()));
        } else {
            cover = new ImageView(new Image(getClass().getResourceAsStream("/images/non_cover.png")));
        }
        record = new Label();
        if (StrUtil.isNotEmpty(info.lastChapterName())) {
            record.setText("上次听到：" + info.lastChapterName());
        } else {
            record.setText("上次听到：未开始");
        }
        title = new Label(info.getTitle() + " - " + info.getAuthor());
        author = new Label();
        speak = new Label("播音：" + info.getSpeak());
        this.setMaxHeight(80);
        cover.setFitWidth(55);
        cover.setFitHeight(70);
        VBox content = new VBox();
        content.getChildren().addAll(title, speak, record);
        content.setSpacing(12);
        this.setSpacing(5);
        this.getChildren().addAll(cover, content);
    }

    public void setLastChapter(String chapterName) {
        this.record.setText("上次听到：" + chapterName);
    }
}
