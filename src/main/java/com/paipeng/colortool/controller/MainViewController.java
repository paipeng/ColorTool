package com.paipeng.colortool.controller;

import com.paipeng.colortool.dialog.ExportDialogController;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

    @FXML
    private Button exportButton;


    private String imagePath;


    private Color brightColor = Color.WHITE;
    private Color darkColor = Color.BLACK;

    private int selectedColorPixel;

    private BufferedImage bufferedImage;

    private double[] cmyk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("initialize");

        setRGBColorTextFields(brightColor);
        setCMYKColorTextFields(brightColor);

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

            setCMYKColorTextFields(((ColorPicker)event.getTarget()).getValue());
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

        imagePath = "/Users/paipeng/Downloads/NanoGrid_0165000000040001.tif";
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

                            setCMYKColorTextFields(brightColor);
                        } else {
                            setRGBColorTextFields(darkColor);
                            setCMYKColorTextFields(darkColor);
                        }
                    } catch (NullPointerException e) {
                        logger.error(e.getMessage());
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        });


        cyanColorSlider.setOnMouseReleased(sliderMouseReleaseEventHandler);
        magentaColorSlider.setOnMouseReleased(sliderMouseReleaseEventHandler);
        yellowColorSlider.setOnMouseReleased(sliderMouseReleaseEventHandler);
        keyColorSlider.setOnMouseReleased(sliderMouseReleaseEventHandler);


        setCMYKColorTextFields(brightColor);

        exportButton.setOnAction(e -> {
            /*
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + 1 + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                //do stuff
            }
            */
            showExportDialog();
        });
    }


    private void setRGBColorTextFields(Color color) {
        logger.info("setRGBColorTextFields " + color.toString());
        redColorTextField.setText(String.valueOf((int)(color.getRed()*255)));
        greenColorTextField.setText(String.valueOf((int)(color.getGreen()*255)));
        blueColorTextField.setText(String.valueOf((int)(color.getBlue()*255)));
        //setCMYKColorTextFields(color);
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

    private EventHandler sliderMouseReleaseEventHandler = new EventHandler() {
        @Override
        public void handle(Event event) {
            logger.info("sliderMouseReleaseEventHandler slider value: " + ((Slider)event.getSource()).getValue());
            if (cyanColorSlider == event.getSource()) {
                cyanColorTextField.setText(String.valueOf(((Slider)event.getSource()).getValue()));
            } else if (magentaColorSlider == event.getSource()) {
                magentaColorTextField.setText(String.valueOf(((Slider)event.getSource()).getValue()));
            } else if (yellowColorSlider == event.getSource()) {
                yellowColorTextField.setText(String.valueOf(((Slider)event.getSource()).getValue()));
            } else if (keyColorSlider == event.getSource()) {
                keyColorTextField.setText(String.valueOf(((Slider)event.getSource()).getValue()));
            }

            // convert to RGB
            if (selectedColorPixel == 0) {
                brightColor = ImageUtils.convertCMYKToRGB(cyanColorSlider.getValue(),
                        magentaColorSlider.getValue(),
                        yellowColorSlider.getValue(),
                        keyColorSlider.getValue());
                setRGBColorTextFields(brightColor);
            } else {
                darkColor = ImageUtils.convertCMYKToRGB(cyanColorSlider.getValue(),
                        magentaColorSlider.getValue(),
                        yellowColorSlider.getValue(),
                        keyColorSlider.getValue());
                setRGBColorTextFields(darkColor);
            }

            changeColor(brightColor, darkColor);

        }
    };



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


    private void showExportDialog() {


        try {
            ResourceBundle resources = ResourceBundle.getBundle("bundles.languages", CommonUtils.getCurrentLanguageLocale());
            Parent root = FXMLLoader.load(ExportDialogController.class.getResource(ExportDialogController.FXML_FILE),resources);

            final Scene scene = new Scene(root, 250, 150);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initOwner(exportButton.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
