package com.unclezs.ui.utils;

import javafx.scene.layout.Region;
import javafx.stage.Window;

/*
 *@author unclezs.com
 *@date 2019.06.22 10:36
 */
public class LayoutUitl {
    //绑定宽高
    public static void bind(Region parent, Region... child){
        for(Region node:child){
            node.prefHeightProperty().bind(parent.heightProperty());
            node.prefWidthProperty().bind(parent.widthProperty());
        }
    }
    //绑定宽
    public static void bindWidth(Region parent, Region... child){
        for(Region node:child){
            node.prefWidthProperty().bind(parent.widthProperty());
        }
    }
    //绑定高
    public static void bindHeight(Region parent, Region... child){
        for(Region node:child){
            node.prefHeightProperty().bind(parent.heightProperty());
        }
    }
    public static void bindStageSize(Window window,Region region){
        region.prefHeightProperty().bind(window.heightProperty());
        region.prefWidthProperty().bind(window.widthProperty());
    }
}
