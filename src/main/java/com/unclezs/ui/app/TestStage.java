package com.unclezs.ui.app;

/*
 *@author unclezs.com
 *@date 2019.07.06 10:06
 */

import javafx.application.Application;
import javafx.stage.Stage;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.io.IOException;


public class TestStage extends Application{

    public static void main(String[] args) throws IOException, AWTException {
        launch(args);
    }

    public TestStage(){
    }

    @Override
    public void start(Stage primaryStage) throws IOException, AWTException, NativeHookException {
//        HotKeyUtil.bindListener();
        primaryStage.show();
    }
}

