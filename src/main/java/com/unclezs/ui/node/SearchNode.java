package com.unclezs.ui.node;

import com.unclezs.model.NovelInfo;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


/*
 *小说搜索结果节点
 *@author unclezs.com
 *@date 2019.07.02 17:51
 */
public class SearchNode extends HBox {
    private NovelInfo info;//小说信息
    /**
     * @param info 小说信息
     * @param url 图片地址
     */
    public SearchNode(NovelInfo info,String url){
        this.info=info;
        init(info,url);
    }
    //创建
    private void init(NovelInfo info,String url) {
        ImageView imgView=new ImageView(new Image(url));
        Label author=new Label("作者： "+info.getAuthor());
        Label src=new Label("来源： "+info.getUrl());
        Label title=new Label("书名： "+info.getTitle());
        initLabel(author,title,src);
        this.setMaxHeight(80);
        //图片宽高
        imgView.setFitWidth(60);
        imgView.setFitHeight(80);
        VBox content=new VBox();
        content.getChildren().addAll(title,author,src);
        this.getChildren().addAll(imgView,content);
    }
    private void initLabel(Label... labels){
        for(Label l:labels){
            l.setMaxHeight(16);
            l.setFont(new Font(14));
            if(!l.getText().contains("来源")){
                l.setPadding(new Insets(0,0,12,10));
            }else {
                l.setPadding(new Insets(0,0,0,10));
            }
        }
    }

    public NovelInfo getInfo() {
        return info;
    }
}
