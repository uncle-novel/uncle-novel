package com.unclezs.ui.node;

import com.unclezs.model.DownloadHistory;
import com.unclezs.ui.utils.ToastUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;


/*
 *@author unclezs.com
 *@date 2019.07.06 16:26
 */
public class DownloadedNode extends HBox {
    private Label remove;
    private DownloadHistory history;
    public DownloadedNode(DownloadHistory history){
        this.history=history;
        this.setWidth(Integer.MAX_VALUE);
        this.setHeight(60);
        //缩略图
        Image image=new Image("file:"+history.getImgPath());;
        if(history.getImgPath().startsWith("http")){//网图
            image=new Image(history.getImgPath());
        }
        if(image.isError()){//图片加载失败
            image=new Image(getClass().getResourceAsStream("/images/搜索页/没有封面.png"));
        }
        ImageView imageView=new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(42);
        //书名作者信息
        Label titleLabel=new Label("书名："+history.getTitle());//标题
        Label typeLabel=new Label("类型："+history.getType());//作者
        typeLabel.setPadding(new Insets(20,0,0,0));
        VBox vBox=new VBox();
        vBox.getChildren().addAll(titleLabel,typeLabel);
        vBox.setPadding(new Insets(0,0,0,5));
        HBox.setHgrow(vBox, Priority.ALWAYS);
        //下载时间
        Label time=new Label(history.getTime());
        time.setPadding(new Insets(10,20,0,0));
        //操作
        Label downOver=new Label("完成");
        downOver.setGraphic(new ImageView("images/下载管理页/下载完成.jpg"));
        downOver.setPadding(new Insets(10,40,0,10));
        Label openDir=new Label();
        openDir.setGraphic(new ImageView("images/下载管理页/文件夹.jpg"));
        openDir.setPadding(new Insets(10,10,0,5));
        openDir.setOnMouseClicked(e->{//打开文件所在目录
            try {
                if(!new File(history.getPath()).exists()){
                    ToastUtil.toast("文件不存在");
                    return;
                }
                Desktop.getDesktop().open(new File(history.getPath()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        remove=new Label();
        remove.setGraphic(new ImageView("images/下载管理页/清除记录.jpg"));
        remove.setPadding(new Insets(10,10,0,5));
        this.getChildren().addAll(imageView,vBox,time,downOver,openDir,remove);
    }

    public Label getRemove() {
        return remove;
    }
    private void resize(ImageView node, double w, double h){
        node.prefWidth(w);
        node.prefHeight(h);
        node.maxWidth(w);
        node.minWidth(w);
        node.maxHeight(h);
        node.minHeight(h);
    }

    public DownloadHistory getHistory() {
        return history;
    }
}
