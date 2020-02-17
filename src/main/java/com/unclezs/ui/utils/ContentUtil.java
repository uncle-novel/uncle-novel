package com.unclezs.ui.utils;


import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

/*
 *@author unclezs.com
 *@date 2019.07.03 11:29
 */
public class ContentUtil {
    public static Pane cachePane;
    //设置内容面板
    public static void setContent(String fxml){
        try {
            Pane node=new FXMLLoader(ContentUtil.class.getResource(fxml)).load();
            if(fxml.contains("analysis")){//缓存解析页
                cachePane=node;
            }
            node.prefHeightProperty().bind(DataManager.content.heightProperty().subtract(1));
            node.prefWidthProperty().bind(DataManager.content.widthProperty().subtract(1));
            DataManager.content.getChildren().clear();
            DataManager.content.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
