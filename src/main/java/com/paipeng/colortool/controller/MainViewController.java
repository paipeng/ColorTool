package com.paipeng.colortool.controller;

import com.paipeng.colortool.utils.CommonUtils;
import com.paipeng.colortool.utils.ImageUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    public static Logger logger = Logger.getLogger(MainViewController.class);
    private static Stage stage;
    private static final String FXML_FILE = "/fxml/MainViewController.fxml";


    @FXML
    private Button backgroundImageButton;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private ColorPicker pixelColorPicker;

    @FXML
    private ImageView imageView;

    @FXML
    private ToggleGroup colorToggleGroup;

    @FXML
    private TextField redColorTextField;
    @FXML
    private TextField greenColorTextField;
    @FXML
    private TextField blueColorTextField;


    @FXML
    private Slider cyanColorSlider;
    @FXML
    private TextField cyanColorTextField;
    @FXML
    private Slider magentaColorSlider;
    @FXML
    private TextField magentaColorTextField;
    @FXML
    private Slider yellowColorSlider;
    @FXML
    private TextField yellowColorTextField;
    @FXML
    private Slider keyColorSlider;
    @FXML
    private TextField keyColorTextField;


    private String imagePath;

    private Color backgroundColor;

    private Color brightColor = Color.WHITE;
    private Color darkColor = Color.BLACK;

    private int selectedColorPixel;

    private BufferedImage bufferedImage;

    private double[] cmyk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("initialize");

        setRGBColorTextFields(brightColor);


        pixelColorPicker.setOnAction(event -> {
            logger.info(((ColorPicker)event.getTarget()).getValue().toString());
            if (selectedColorPixel == 0) {
                // brightColor
                brightColor = ((ColorPicker)event.getTarget()).getValue();
            } else {
                // darkColor
                darkColor = ((ColorPicker)event.getTarget()).getValue();
            }

            setRGBColorTextFields(((ColorPicker)event.getTarget()).getValue());

            changeColor(brightColor, darkColor);
        });

        FileChooser fileChooser = new FileChooser();

        backgroundImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(((Node) e.getTarget()).getScene().getWindow());
            logger.info("selected file: " + selectedFile.getAbsolutePath());
            imagePath = selectedFile.getAbsolutePath();
            //drawImage(imagePath);

            showImage(imagePath);
        });


        imagePath = this.getClass().getResource("/images/image_view_holder.png").getPath();

        showImage(imagePath);

        colorToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                logger.info(colorToggleGroup.getSelectedToggle().getUserData().toString());
                if (colorToggleGroup.getSelectedToggle() != null) {
                    try {
                        selectedColorPixel = Integer.parseInt(colorToggleGroup.getSelectedToggle().getUserData().toString());

                        if (selectedColorPixel == 0) {
                            setRGBColorTextFields(brightColor);
                        } else {
                            setRGBColorTextFields(darkColor);
                        }
                    } catch (NullPointerException e) {
                        logger.error(e.getMessage());
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        });



        cyanColorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                logger.info("cyan slider value: " + observable.getValue());
                cyanColorTextField.setText(String.valueOf(observable.getValue()));

            }
        });
        magentaColorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                logger.info("magenta slider value: " + observable.getValue());
                magentaColorTextField.setText(String.valueOf(observable.getValue()));
            }
        });
        yellowColorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                logger.info("yellow slider value: " + observable.getValue());
                yellowColorTextField.setText(String.valueOf(observable.getValue()));
            }
        });
        yellowColorSlider.setOnMouseReleased(sliderMouseReleaseEventHandler);
        keyColorSlider.setOnMouseReleased(event -> {
            logger.info("key slider value: " + ((Slider)event.getTarget()).getValue());

                });
        keyColorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                logger.info("key slider value: " + observable.getValue());
                keyColorTextField.setText(String.valueOf(observable.getValue()));
            }
        });


        setCMYKColorTextFields(brightColor);
    }


    private void setRGBColorTextFields(Color color) {

        redColorTextField.setText(String.valueOf((int)(color.getRed()*255)));
        greenColorTextField.setText(String.valueOf((int)(color.getGreen()*255)));
        blueColorTextField.setText(String.valueOf((int)(color.getBlue()*255)));
        setCMYKColorTextFields(color);
    }

    private void setCMYKColorTextFields(Color color) {
        cmyk = ImageUtils.convertRGBToCMYK(color);
        cyanColorTextField.setText(String.valueOf(cmyk[0]));
        magentaColorTextField.setText(String.valueOf(cmyk[1]));
        yellowColorTextField.setText(String.valueOf(cmyk[2]));
        keyColorTextField.setText(String.valueOf(cmyk[3]));

        cyanColorSlider.setValue(cmyk[0]);
        magentaColorSlider.setValue(cmyk[1]);
        yellowColorSlider.setValue(cmyk[2]);
        keyColorSlider.setValue(cmyk[3]);
    }

    private void showImage(String imagePath) {
        try {
            //FileInputStream input = new FileInputStream(imagePath);
            //Image image = new Image(input);
            bufferedImage = ImageIO.read(new File(imagePath));

            imageView.setImage(ImageUtils.convertBufferedImageToImage(bufferedImage));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeColor(int selectedColorPixel, Color color) {
        BufferedImage bufferedImage2 = ImageUtils.changeBufferedImageWithColor(bufferedImage, color, selectedColorPixel);
        imageView.setImage(ImageUtils.convertBufferedImageToImage(bufferedImage2));
    }

    private EventHandler sliderMouseReleaseEventHandler = new EventHandler() {
        @Override
        public void handle(Event event) {
            logger.info("sliderMouseReleaseEventHandler slider value: " + ((Slider)event.getSource()).getValue());

        }
    };



    private void changeColor(int selectedColorPixel, int red, int green, int blue) {
        logger.info("changeColor " + red + " " + green + " " + blue);
        BufferedImage bufferedImage2 = ImageUtils.changeBufferedImageWithColor(bufferedImage, selectedColorPixel, red, green, blue);
        imageView.setImage(ImageUtils.convertBufferedImageToImage(bufferedImage2));
    }


    private void changeColor(Color brightColor, Color darkColor) {
        logger.info("brightColor " + brightColor.toString());
        logger.info("darkColor " + darkColor.toString());

        BufferedImage bufferedImage2 = ImageUtils.changeBufferedImageWithColor(bufferedImage, brightColor, darkColor);
        imageView.setImage(ImageUtils.convertBufferedImageToImage(bufferedImage2));
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
