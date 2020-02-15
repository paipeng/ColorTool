package com.paipeng.colortool.controller;

import com.paipeng.colortool.dialog.ExportDialogController;
import com.paipeng.colortool.model.ExportImage;
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
    private static final String FXML_FILE = "/fxml/contollers/MainViewController.fxml";


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
    public BufferedImage coloredBufferedImage;

    private double[] brightCMYK;
    private double[] darkCMYK;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("initialize");

        setRGBColorTextFields(brightColor);
        setCMYKColorTextFields(brightColor);
        updateCMYK(0, brightColor);
        updateCMYK(1, darkColor);

        if (coloredBufferedImage != null) {
            exportButton.setDisable(false);
        } else {
            exportButton.setDisable(true);
        }
        pixelColorPicker.setOnAction(event -> {
            logger.info(((ColorPicker) event.getTarget()).getValue().toString());
            if (selectedColorPixel == 0) {
                // brightColor
                brightColor = ((ColorPicker) event.getTarget()).getValue();
                updateCMYK(selectedColorPixel, brightColor);
            } else {
                // darkColor
                darkColor = ((ColorPicker) event.getTarget()).getValue();
                updateCMYK(selectedColorPixel, darkColor);
            }

            setRGBColorTextFields(((ColorPicker) event.getTarget()).getValue());

            setCMYKColorTextFields(((ColorPicker) event.getTarget()).getValue());
            changeColor(brightColor, darkColor);
        });


        backgroundImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
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
            showExportDialog();
        });
    }


    private void setRGBColorTextFields(Color color) {
        logger.info("setRGBColorTextFields " + color.toString());
        redColorTextField.setText(String.valueOf((int) (color.getRed() * 255)));
        greenColorTextField.setText(String.valueOf((int) (color.getGreen() * 255)));
        blueColorTextField.setText(String.valueOf((int) (color.getBlue() * 255)));
        //setCMYKColorTextFields(color);
    }

    private void setCMYKColorTextFields(Color color) {
        double[] cmyk = ImageUtils.convertRGBToCMYK(color);
        cyanColorTextField.setText(String.valueOf(cmyk[0]));
        magentaColorTextField.setText(String.valueOf(cmyk[1]));
        yellowColorTextField.setText(String.valueOf(cmyk[2]));
        keyColorTextField.setText(String.valueOf(cmyk[3]));

        cyanColorSlider.setValue(cmyk[0]);
        magentaColorSlider.setValue(cmyk[1]);
        yellowColorSlider.setValue(cmyk[2]);
        keyColorSlider.setValue(cmyk[3]);
    }

    private void updateCMYK(int selectedColorPixel, Color color) {
        double[] cmyk = ImageUtils.convertRGBToCMYK(color);
        if (selectedColorPixel == 0) {
            brightCMYK = cmyk;
        } else {
            darkCMYK = cmyk;
        }
    }

    private void showImage(String imagePath) {
        try {
            //FileInputStream input = new FileInputStream(imagePath);
            //Image image = new Image(input);
            bufferedImage = ImageIO.read(new File(imagePath));
            coloredBufferedImage = bufferedImage;

            imageView.setImage(ImageUtils.convertBufferedImageToImage(bufferedImage));

            if (coloredBufferedImage != null) {
                exportButton.setDisable(false);
            } else {
                exportButton.setDisable(true);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EventHandler sliderMouseReleaseEventHandler = new EventHandler() {
        @Override
        public void handle(Event event) {
            logger.info("sliderMouseReleaseEventHandler slider value: " + ((Slider) event.getSource()).getValue());
            if (cyanColorSlider == event.getSource()) {
                cyanColorTextField.setText(String.valueOf(((Slider) event.getSource()).getValue()));
            } else if (magentaColorSlider == event.getSource()) {
                magentaColorTextField.setText(String.valueOf(((Slider) event.getSource()).getValue()));
            } else if (yellowColorSlider == event.getSource()) {
                yellowColorTextField.setText(String.valueOf(((Slider) event.getSource()).getValue()));
            } else if (keyColorSlider == event.getSource()) {
                keyColorTextField.setText(String.valueOf(((Slider) event.getSource()).getValue()));
            }

            // convert to RGB
            if (selectedColorPixel == 0) {
                brightCMYK[0] = cyanColorSlider.getValue();
                brightCMYK[1] = magentaColorSlider.getValue();
                brightCMYK[2] = yellowColorSlider.getValue();
                brightCMYK[3] = keyColorSlider.getValue();
                logger.info("brightCMYK " + brightCMYK[0] + "-" + brightCMYK[1] + "-"
                        + brightCMYK[2] + "-" + brightCMYK[3]);

                brightColor = ImageUtils.convertCMYKToRGB(cyanColorSlider.getValue(),
                        magentaColorSlider.getValue(),
                        yellowColorSlider.getValue(),
                        keyColorSlider.getValue());
                setRGBColorTextFields(brightColor);
            } else {
                darkCMYK[0] = cyanColorSlider.getValue();
                darkCMYK[1] = magentaColorSlider.getValue();
                darkCMYK[2] = yellowColorSlider.getValue();
                darkCMYK[3] = keyColorSlider.getValue();
                logger.info("darkCMYK " + darkCMYK[0] + "-" + darkCMYK[1] + "-"
                        + darkCMYK[2] + "-" + darkCMYK[3]);
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

        coloredBufferedImage = ImageUtils.changeBufferedImageWithColor(bufferedImage, brightColor, darkColor);
        imageView.setImage(ImageUtils.convertBufferedImageToImage(coloredBufferedImage));
    }

    private EventHandler textViewEventHandler = new EventHandler<KeyEvent>() {
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
            Parent root = FXMLLoader.load(MainViewController.class.getResource(FXML_FILE), resources);

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
            Parent root = FXMLLoader.load(ExportDialogController.class.getResource(ExportDialogController.FXML_FILE), resources);


            /*

            FXMLLoader loader = new FXMLLoader(getClass().getResource(ExportDialogController.FXML_FILE));
            loader.setResources(resources);
            Parent root = loader.load();
            ExportDialogController controller = loader.<ExportDialogController>getController();
            controller.setColoredBufferedImage(coloredBufferedImage);


            */

            ExportImage exportImage = new ExportImage();
            exportImage.setBrightColor(brightColor);
            exportImage.setDarkColor(darkColor);
            exportImage.setBrightCMYK(brightCMYK);
            exportImage.setDarkCMYK(darkCMYK);
            exportImage.setBufferedImage(bufferedImage);
            exportImage.setColoredBufferedImage(coloredBufferedImage);
            final Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource(ExportDialogController.CSS_FILE).toExternalForm());
            scene.setUserData(exportImage);
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
}
