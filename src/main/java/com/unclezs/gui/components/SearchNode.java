package com.unclezs.gui.components;

import cn.hutool.core.util.StrUtil;
import com.unclezs.gui.utils.ImageLoader;
import com.unclezs.model.NovelInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;


/**
 * 小说搜索结果节点
 *
 * @author unclezs.com
 * @date 2019.07.02 17:51
 */
@Getter
public class SearchNode extends HBox {
    /**
     * 小说信息
     */
    private NovelInfo info;
    private ImageView cover;

    public SearchNode(NovelInfo info) {
        this.info = info;
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        cover = new ImageView("images/non_cover.png");
        if (StrUtil.isNotEmpty(this.info.getCover())) {
            ImageLoader.loadImage(this.info.getCover(), stream -> this.cover.setImage(new Image(stream)));
        }
        Label author = new Label("作者： " + info.getAuthor());
        Label src = new Label("来源： " + info.getUrl());
        Label title = new Label("书名： " + info.getTitle());
        this.setMaxHeight(80);
        cover.setFitWidth(55);
        cover.setFitHeight(70);
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(12);
        content.getChildren().addAll(title, author, src);
        this.setSpacing(5);
        this.getChildren().addAll(cover, content);
    }
}
