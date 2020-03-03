package com.unclezs.ui.node;

import cn.hutool.http.HttpUtil;
import com.unclezs.model.AudioBook;
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
public class SearchAudioNode extends HBox {
    private AudioBook info;//小说信息
    private Label src;

    /**
     * @param info 小说信息
     */
    public SearchAudioNode(AudioBook info) {
        this.info = info;
        init(info);
    }

    //创建
    private void init(AudioBook info) {
        Image image = new Image(HttpUtil.createGet(info.getImageUrl()).execute().bodyStream());
        if(image.isError()){
            image=new Image(getClass().getResourceAsStream("/images/搜索页/没有封面.png"));
        }
        ImageView imgView = new ImageView(image);
        src = new Label();
        if(info.getId()!=-1){
            if(info.getLastChapter()!=null&&!info.getLastChapter().equals(""))
                src.setText("上次听到："+info.getLastChapter());
            else
                src.setText("上次听到：未开始");
        }else {
            src.setText("来源：" + info.getUrl());
        }
        Label title = new Label("书名：" + info.getTitle());
        Label author = new Label(info.getAuthor());
        Label speak = new Label(info.getBroadCasting());
        initLabel(author, title, src, speak);
        this.setMaxHeight(80);
        //图片宽高
        imgView.setFitWidth(60);
        imgView.setFitHeight(80);
        VBox content = new VBox();
        content.getChildren().addAll(title, author, speak, src);
        this.getChildren().addAll(imgView, content);
    }

    private void initLabel(Label... labels) {
        for (Label l : labels) {
            l.setMaxHeight(16);
            l.setFont(new Font(12));
            if (!l.getText().contains("来源")) {
                l.setPadding(new Insets(0, 0, 6, 10));
            } else {
                l.setPadding(new Insets(0, 0, 0, 10));
            }
        }
    }

    public AudioBook getInfo() {
        return info;
    }

    public void setInfo(AudioBook info) {
        this.info = info;
    }

    public Label getSrc() {
        return src;
    }
}
