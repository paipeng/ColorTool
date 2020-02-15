package com.paipeng.colortool.dialog;

import com.paipeng.colortool.model.PrintDPI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

public class ExportDialogController {
    public static Logger logger = Logger.getLogger(ExportDialogController.class);
    public static final String FXML_FILE = "/fxml/dialogs/ExportDialogController.fxml";
    public static final String CSS_FILE = "/css/dialogs/ExportDialogController.css";

    @FXML
    private ToggleGroup imageFormatToggleGroup;


    @FXML
    private ToggleGroup colorFormatToggleGroup;

    @FXML
    private ComboBox<PrintDPI> printDpiComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Integer printDpi;

    public void initialize() {
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


        });

        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            // do what you have to do
            stage.close();
        });



        imageFormatToggleGroup.selectedToggleProperty().addListener(toggleGroupChangeListener);
        colorFormatToggleGroup.selectedToggleProperty().addListener(toggleGroupChangeListener);
    }

    private ChangeListener toggleGroupChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue observable, Toggle oldValue, Toggle newValue) {
            logger.info(((Toggle)observable.getValue()).getToggleGroup().getSelectedToggle().getUserData().toString());

        }
    };

    public void close() {

    }
}
