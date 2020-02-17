package com.unclezs.ui.node;

import com.jfoenix.controls.JFXProgressBar;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/*
 *@author unclezs.com
 *@date 2019.07.06 14:21
 */
public class DownloadingNode {
    private JFXProgressBar pb;//进度条
    private Label label;//文字进度
    private HBox pbPane;//进度条pane
    private StringProperty id;
    private StringProperty title;//标题
    private Label remove;//删除

    public DownloadingNode(StringProperty id, StringProperty title) {
        this.id = id;
        this.title = title;
        this.remove=new Label("取消");
        remove.setAlignment(Pos.CENTER);
        remove.setPrefWidth(50);
        remove.setPrefHeight(20);
        remove.setStyle("-fx-background-color: #FF5722;-fx-background-radius: 10;-fx-border-radius: 10");
        remove.setTextFill(Color.WHITE);
        this.pb=new JFXProgressBar(0.5);
        pb.setPadding(new Insets(7,20,0,0));
        this.label=new Label("0/0");
        this.pbPane=new HBox();
        this.pbPane.getChildren().addAll(pb,label);
    }

    public JFXProgressBar getPb() {
        return pb;
    }

    public void setPb(JFXProgressBar pb) {
        this.pb = pb;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public Label getRemove() {
        return remove;
    }

    public void setRemove(Label remove) {
        this.remove = remove;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public HBox getPbPane() {
        return pbPane;
    }

    public void setPbPane(HBox pbPane) {
        this.pbPane = pbPane;
    }
}
