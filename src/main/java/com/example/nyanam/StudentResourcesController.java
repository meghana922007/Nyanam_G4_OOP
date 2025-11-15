package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.awt.Desktop; // We will use this to open the file
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentResourcesController implements Initializable {


    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, String> titleCol;
    @FXML private TableColumn<Resource, String> topicCol;
    @FXML private TableColumn<Resource, String> subtopicCol;
    @FXML private TableColumn<Resource, String> typeCol;
    @FXML private Button openButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        titleCol.setCellValueFactory(new PropertyValueFactory<>("titleProperty"));
        topicCol.setCellValueFactory(new PropertyValueFactory<>("topicProperty"));
        subtopicCol.setCellValueFactory(new PropertyValueFactory<>("subtopicProperty"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeProperty"));

        loadResources();
    }

    private void loadResources() {
        try {
            List<Resource> resourceList = DatabaseConnector.getAllResources();
            ObservableList<Resource> observableList = FXCollections.observableArrayList(resourceList);
            resourceTable.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load resources.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 700, 500);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load dashboard.");
        }
    }

    @FXML
    private void handleOpenFile() {
        Resource selectedResource = resourceTable.getSelectionModel().getSelectedItem();

        if (selectedResource == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a file to open.");
            return;
        }

        try {

            String filePath = selectedResource.getFilePathProperty().get();
            File file = new File(filePath);

            if (file.exists() && Desktop.isDesktopSupported()) {

                Desktop.getDesktop().open(file);
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Opening file: " + file.getName());
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error: File not found or Desktop is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error opening file: " + e.getMessage());
        }
    }
}