package com.paipeng.colortool.dialog;

import com.paipeng.colortool.model.ExportImage;
import com.paipeng.colortool.model.PrintDPI;
import com.paipeng.colortool.utils.ImageUtils;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ResourceBundle;

public class ExportDialogController implements Initializable {
    public static Logger logger = Logger.getLogger(ExportDialogController.class);
    public static final String FXML_FILE = "/fxml/dialogs/ExportDialogController.fxml";
    public static final String CSS_FILE = "/css/dialogs/ExportDialogController.css";

    @FXML
    private ToggleGroup imageFormatToggleGroup;


    @FXML
    private ToggleGroup colorSpaceToggleGroup;

    @FXML
    private ComboBox<PrintDPI> printDpiComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Integer printDpi;
    private int imageFormat;
    private int colorSpace;
    private ExportImage exportImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        printDpiComboBox.setItems(FXCollections.observableArrayList(
                new PrintDPI("600", 600),
                new PrintDPI("800", 800),
                new PrintDPI("1200", 1200),
                new PrintDPI("1400", 1400),
                new PrintDPI("1600", 1600),
                new PrintDPI("1800", 1800),
                new PrintDPI("2000", 2000))
        );

        printDpiComboBox.setConverter(new StringConverter<PrintDPI>() {
            @Override
            public String toString(PrintDPI object) {
                return object.getName();
            }

            @Override
            public PrintDPI fromString(String string) {
                return null;
            }
        });

        printDpi = 600;
        printDpiComboBox.getSelectionModel().selectFirst();
        printDpiComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("DPI of the " + newVal.getName() + " is : " + newVal.getDpi());
            printDpi = newVal.getDpi();
        });



        saveButton.setOnAction(e -> {
            exportImage  = (ExportImage) saveButton.getScene().getUserData();
            logger.info("DPI " + printDpi + " imageFormat: " + imageFormat + " colorSpace: " + colorSpace + " setColoredBufferedImage" + exportImage.getColoredBufferedImage());

            logger.info("brightColor " + exportImage.getBrightColor().toString());
            logger.info("darkColor " + exportImage.getDarkColor().toString());
            logger.info("cmyk: " + exportImage.getCmyk());
            logger.info("bufferedImage: " + exportImage.getBufferedImage());

            FileChooser fileChooser = new FileChooser();

            //Set extension filter for text files
            String description = "TIFF files (*.tiff)";
            String extension = "*.tif";
            if (imageFormat == 1) {
                description = "PDF files (*.pdf)";
                extension = "*.pdf";

            }
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(description, extension);
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(((Node) e.getTarget()).getScene().getWindow());

            if (file != null) {

                exportImage(exportImage, file);
            }

        });

        cancelButton.setOnAction(e -> {
            close();
        });



        imageFormatToggleGroup.selectedToggleProperty().addListener(toggleGroupChangeListener);
        colorSpaceToggleGroup.selectedToggleProperty().addListener(toggleGroupChangeListener);
    }

    private ChangeListener toggleGroupChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue observable, Toggle oldValue, Toggle newValue) {
            logger.info(((Toggle)observable.getValue()).getToggleGroup().getSelectedToggle().getUserData().toString());
            try {
                if (imageFormatToggleGroup == ((Toggle) observable.getValue()).getToggleGroup()) {
                    imageFormat = Integer.valueOf(newValue.getUserData().toString()).intValue();
                } else if (colorSpaceToggleGroup == ((Toggle) observable.getValue()).getToggleGroup()) {
                    colorSpace = Integer.valueOf(newValue.getUserData().toString()).intValue();
                }
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
            }

        }
    };

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


    private void exportImage(ExportImage exportImage, File desitnationFileName) {
        if (colorSpace == 0) {
            // do nothing, using coloredBufferedImage directly

        } else if (colorSpace == 1) {
            // convert buffered image to cmyk buffered image and store into coloredBufferedImage  twelve monkey


            exportImage.setColoredBufferedImage(ImageUtils.copyBUfferedImageRGBToCMYK(exportImage.getBufferedImage()));
        }


        if (imageFormat == 0) {
            // tiff
            // write coloredBufferedImage into tiff desitnationFileName
            try {

                final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(desitnationFileName);

                if (imageOutputStream == null) {
                    throw new IOException("Unable to obtain an output stream for file: " + desitnationFileName);
                }


                // set the byte order to little endian
                // our various imaging systems downstream will not be able to support the
                // big endian (motorola) byte order


                imageOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

                final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("tif");
                if (imageWriters == null) {
                    throw new IOException("Unable to obtain an image writer for the tif file format!");
                }


                // select the first writer available


                final ImageWriter imageWriter = imageWriters.next();
                imageWriter.setOutput(imageOutputStream);
                final ImageWriteParam parameters = imageWriter.getDefaultWriteParam();
                //parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                //parameters.setCompressionType("CCITT T.6"); // Group 4 Compression
                //parameters.setCompressionQuality(0.2f);

                final List<Entry> entries = new ArrayList<Entry>();
                entries.add(new TIFFEntry(TIFF.TAG_X_RESOLUTION, new Rational(printDpi)));
                entries.add(new TIFFEntry(TIFF.TAG_Y_RESOLUTION, new Rational(printDpi)));

                final IIOMetadata tiffImageMetadata = new TIFFImageMetadata(entries);

                //imageWriter.write(tiffImageMetadata, new IIOImage(exportImage.getColoredBufferedImage(), null, null), parameters);

                imageWriter.write(null, new IIOImage(exportImage.getColoredBufferedImage(), null, tiffImageMetadata), parameters);
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // pdf
            // write coloredBufferedImage into desitnationFileName pdf pdfutils
        }
    }

}
