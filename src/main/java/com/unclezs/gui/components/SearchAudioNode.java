package com.unclezs.gui.components;

import cn.hutool.core.util.StrUtil;
import com.unclezs.gui.utils.ImageLoader;
import com.unclezs.model.AudioBook;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;


/**
 * 小说搜索结果节点
 *
 * @author unclezs.com
 * @date 2019.07.02 17:51
 */
@Getter
@Setter
public class SearchAudioNode extends HBox {
    /**
     * 小说信息
     */
    private AudioBook info;
    private Label src;
    private ImageView cover;

    /**
     * @param info 小说信息
     */
    public SearchAudioNode(AudioBook info) {
        this.info = info;
        init(info);
    }

    private void init(AudioBook info) {
        //防止防盗链无法拿到封面
        if (StrUtil.isNotEmpty(info.getCover())) {
            ImageLoader.loadImage(this.info.getCover(), stream -> this.cover.setImage(new Image(stream)));
        }
        Image image = new Image(getClass().getResourceAsStream("/images/non_cover.png"));
        cover = new ImageView(image);
        src = new Label();
        src.setText("来源：" + info.getUrl());
        Label title = new Label("书名：" + (StrUtil.isNotEmpty(info.getTitle()) ? info.getTitle() : "未知"));
        Label author = new Label("作者：" + (StrUtil.isNotEmpty(info.getAuthor()) ? info.getAuthor() : "未知"));
        Label speak = new Label("播音：" + (StrUtil.isNotEmpty(info.getSpeak()) ? info.getSpeak() : "未知"));
        this.setMaxHeight(80);
        //图片宽高
        cover.setFitWidth(55);
        cover.setFitHeight(70);
        VBox content = new VBox();
        content.getChildren().addAll(title, author, speak, src);
        content.setSpacing(5);
        this.setSpacing(5);
        this.getChildren().addAll(cover, content);
    }
}
