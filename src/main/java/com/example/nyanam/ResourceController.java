package com.example.nyanam; // Changed package

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class ResourceController {

    @FXML private TextField topicField;
    @FXML private TextField subtopicField;
    @FXML private TextField titleField;

    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, Number> idCol;
    @FXML private TableColumn<Resource, String> titleCol;
    @FXML private TableColumn<Resource, String> topicCol;
    @FXML private TableColumn<Resource, String> subtopicCol;
    @FXML private TableColumn<Resource, String> typeCol;
    @FXML private TableColumn<Resource, String> filePathCol;

    @FXML private Button addBtn;
    @FXML private Button deleteBtn;
    @FXML private Button refreshBtn;
    @FXML private Button backBtn;

    private ObservableList<Resource> resourceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        idCol.setCellValueFactory(data -> data.getValue().getResourceIdProperty());
        titleCol.setCellValueFactory(data -> data.getValue().getTitleProperty());
        topicCol.setCellValueFactory(data -> data.getValue().getTopicProperty());
        subtopicCol.setCellValueFactory(data -> data.getValue().getSubtopicProperty());
        typeCol.setCellValueFactory(data -> data.getValue().getTypeProperty());
        filePathCol.setCellValueFactory(data -> data.getValue().getFilePathProperty());

        resourceTable.setItems(resourceList);

        refreshBtn.setOnAction(e -> loadResources());
        addBtn.setOnAction(e -> addResource());
        deleteBtn.setOnAction(e -> deleteResource());
        backBtn.setOnAction(e -> handleBackToDashboard());

        loadResources();
    }


    private void loadResources() {
        try {

            List<Resource> list = DatabaseConnector.getAllResources();
            resourceList.setAll(list);
            System.out.println("Loaded " + list.size() + " resources from database");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load resources from database.");
        }
    }

    private void addResource() {
        String title = titleField.getText().trim();
        String topic = topicField.getText().trim();
        String subtopic = subtopicField.getText().trim();

        if (title.isEmpty() || topic.isEmpty() || subtopic.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fill in all fields (Title, Topic, Subtopic) before adding a resource!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resource File");


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov")
        );

        File file = fileChooser.showOpenDialog(addBtn.getScene().getWindow());

        if (file != null) {
            String type = getFileType(file.getName());
            String path = file.getAbsolutePath();

            try {

                DatabaseConnector.addResource(title, topic, subtopic, type, path);

                loadResources();

                clearFields();

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Resource '" + title + "' added successfully!");

                System.out.println("Resource added: " + title + " | " + path);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Failed to add resource to database: " + e.getMessage());
            }
        } else {

            System.out.println("File selection cancelled by user.");
        }
    }

    private String getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toUpperCase();
        }
        return "UNKNOWN";
    }

    private void deleteResource() {
        Resource selected = resourceTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a resource from the table to delete!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Resource?");
        confirmAlert.setContentText("Are you sure you want to delete:\n" +
                selected.getTitleProperty().get() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseConnector.deleteResource(selected.getResourceIdProperty().get());

                loadResources();

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Resource deleted successfully!");

                System.out.println("Resource deleted: ID " + selected.getResourceIdProperty().get());

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Failed to delete resource: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        titleField.clear();
        topicField.clear();
        subtopicField.clear();
    }

    private void handleBackToDashboard() {
        try {
            Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);

            System.out.println("Navigated back to Teacher Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not return to Teacher Dashboard.");
        }
    }

    // ✅ Helper method to show alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
