package com.paipeng.colortool.controller;

import com.paipeng.colortool.utils.CommonUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    public static Logger logger = Logger.getLogger(MainViewController.class);
    private static Stage stage;
    private static final String FXML_FILE = "/fxml/MainViewController.fxml";


    private String imagePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("initialize");
/*
        backgroundColorPicker.setOnAction(event -> {
            backgroundColor = backgroundColorPicker.getValue();
            GraphicsContext gc = labelCanvas.getGraphicsContext2D();
            gc.setFill(backgroundColorPicker.getValue());
            gc.fillRect(0, 0, labelCanvas.getWidth(), labelCanvas.getHeight());
        });

        FileChooser fileChooser = new FileChooser();

        backgroundImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(((Node) e.getTarget()).getScene().getWindow());
            logger.info("selected file: " + selectedFile.getAbsolutePath());
            imagePath = selectedFile.getAbsolutePath();
            drawImage(imagePath);
        });

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.GRAY);
        labelCanvas.setEffect(ds);


        labelWidthTextField.setText("300");
        labelWidthTextField.setOnKeyReleased(eventHandler);
        labelHeightTextField.setText("600");
        labelHeightTextField.setOnKeyReleased(eventHandler);


        drawShapes();
        imagePath = this.getClass().getResource("/images/timg.jpeg").getPath();
        drawImage(imagePath);
        */
    }

    private EventHandler eventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                logger.info("eventHandler target: " + event.getSource() + " value: " + ((TextField) event.getSource()).getText());
                /*
                if (event.getSource() == labelWidthTextField) {
                    labelCanvas.setWidth(Integer.valueOf(((TextField) event.getSource()).getText()));
                } else if (event.getSource() == labelHeightTextField) {
                    labelCanvas.setHeight(Integer.valueOf(((TextField) event.getSource()).getText()));
                }
                drawImage(imagePath);
                */
            }
        }
    };


    public static void start() {
        logger.info("start");
        try {
            ResourceBundle resources = ResourceBundle.getBundle("bundles.languages", CommonUtils.getCurrentLanguageLocale());
            Parent root = FXMLLoader.load(MainViewController.class.getResource(FXML_FILE),resources);

            //FXMLLoader fxmlLoader = new FXMLLoader();
            //fxmlLoader.setResources(resources);
            //Parent root = fxmlLoader.load(MainViewController.class.getResource(FXML_FILE).openStream());

            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setTitle(resources.getString("title"));
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(true);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void drawShapes() {
        /*
        GraphicsContext gc = labelCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, labelCanvas.getWidth(), labelCanvas.getHeight());
        */
    }

    private void drawImage(String imagePath) {
        /*
        GraphicsContext gc = labelCanvas.getGraphicsContext2D();

        try {
            FileInputStream input = new FileInputStream(imagePath);
            Image image = new Image(input);

            // Draw the Image
            gc.drawImage(image, 0, 0, labelCanvas.getWidth(), labelCanvas.getHeight());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */
    }
}
