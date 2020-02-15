package com.paipeng.colortool.dialog;

import com.paipeng.colortool.model.ExportImage;
import com.paipeng.colortool.model.PrintDPI;
import com.paipeng.colortool.utils.ImageUtils;
import com.paipeng.pdf.PDFUtils;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
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
            exportImage = (ExportImage) saveButton.getScene().getUserData();
            logger.info("DPI " + printDpi + " imageFormat: " + imageFormat + " colorSpace: " + colorSpace + " setColoredBufferedImage" + exportImage.getColoredBufferedImage());

            logger.info("brightColor " + exportImage.getBrightColor().toString());
            logger.info("darkColor " + exportImage.getDarkColor().toString());
            logger.info("bright cmyk: " + exportImage.getBrightCMYK());
            logger.info("dark cmyk: " + exportImage.getDarkCMYK());
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
            logger.info(((Toggle) observable.getValue()).getToggleGroup().getSelectedToggle().getUserData().toString());
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

            logger.info("brightCMYK " + exportImage.getBrightCMYK()[0] + "-" + exportImage.getBrightCMYK()[1] + "-"
                    + exportImage.getBrightCMYK()[2] + "-" + exportImage.getBrightCMYK()[3]);
            logger.info("darkCMYK " + exportImage.getDarkCMYK()[0] + "-" + exportImage.getDarkCMYK()[1] + "-"
                    + exportImage.getDarkCMYK()[2] + "-" + exportImage.getDarkCMYK()[3]);

            exportImage.setColoredBufferedImage(ImageUtils.copyBUfferedImageRGBToCMYK(exportImage.getBufferedImage(), exportImage.getBrightCMYK(), exportImage.getDarkCMYK()));
        }


        if (imageFormat == 0) {
            // tiff
            // write coloredBufferedImage into tiff desitnationFileName
            try {

                ImageUtils.saveBufferedImageToTiff(exportImage.getColoredBufferedImage(), printDpi, desitnationFileName);

                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // pdf
            // write coloredBufferedImage into desitnationFileName pdf pdfutils


            try {
                createPDF(exportImage.getColoredBufferedImage(), printDpi, desitnationFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            close();
        }


    }

    private void createPDF(BufferedImage bufferedImage, int printDpi, File desitnationFileName) throws Exception {
        PDDocument pdfDoc = new PDDocument();

        PDPage page = new PDPage(PDRectangle.A4);
        pdfDoc.addPage(page);

        PDDocumentInformation pdi = pdfDoc.getDocumentInformation();

        pdi.setAuthor("Pai Peng");
        pdi.setTitle("ColorTool");
        pdi.setCreator("Pai Peng");
        pdi.setSubject("ColorTool");
        pdi.setProducer("Pai Peng");
        pdi.setTrapped("True");
        pdi.setCustomMetadataValue("test", "value1");

        Calendar date = Calendar.getInstance();
        pdi.setCreationDate(date);
        pdi.setModificationDate(date);

        pdi.setKeywords("CMYK RGB PDF TIF");


        PDFUtils.insertImage(pdfDoc, 0, bufferedImage, 400, 200, printDpi);

        //PdfUtils.encryptPDF(pdfDoc, "12345", "123");

        pdfDoc.save(desitnationFileName.getAbsolutePath());
    }
}