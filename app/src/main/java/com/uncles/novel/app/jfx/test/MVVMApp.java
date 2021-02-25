/*
 * Copyright 2016 Bekwam, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uncles.novel.app.jfx.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author carl
 */
public class MVVMApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        EmploymentRequestView view = new EmploymentRequestView();

        Scene scene = new Scene(view);

        primaryStage.setTitle("MVVM App");
        primaryStage.setScene( scene );
        primaryStage.setWidth( 480 );
        primaryStage.setHeight( 320 );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
