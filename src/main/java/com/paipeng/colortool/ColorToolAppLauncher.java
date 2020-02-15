package com.paipeng.colortool;


import com.paipeng.colortool.controller.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ColorToolAppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainViewController.start();
    }
}
