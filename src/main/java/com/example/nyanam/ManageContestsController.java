package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageContestsController implements Initializable {

    @FXML private TableView<ContestSummary> contestTableView;
    @FXML private TableColumn<ContestSummary, String> contestNameCol;
    @FXML private TableColumn<ContestSummary, String> startTimeCol;
    @FXML private TableColumn<ContestSummary, String> endTimeCol;
    @FXML private TableColumn<ContestSummary, Integer> questionCountCol;

    @FXML private Button backButton;
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Set up the table columns
        contestNameCol.setCellValueFactory(new PropertyValueFactory<>("contestName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        questionCountCol.setCellValueFactory(new PropertyValueFactory<>("questionCount"));

        // 2. Load the data
        loadContestData();
    }

    private void loadContestData() {
        try {
            List<ContestSummary> summaryList = DatabaseConnector.getAllContestSummaries();
            ObservableList<ContestSummary> observableList = FXCollections.observableArrayList(summaryList);
            contestTableView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading contests. Check console.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("AdminDashboard.fxml", "Admin Dashboard", 800, 600); // Use your admin dash size
    }

    @FXML
    private void handleAdd() throws IOException {
        // Open the new create contest screen
        Main.changeScene("CreateContest.fxml", "Create New Contest", 500, 450);
    }

    @FXML
    private void handleDelete() {
        ContestSummary selectedContest = contestTableView.getSelectionModel().getSelectedItem();

        if (selectedContest == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a contest to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contest");
        alert.setHeaderText("Are you sure you want to delete this contest?");
        alert.setContentText("Contest: " + selectedContest.contestNameProperty().get());

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = DatabaseConnector.deleteContestById(selectedContest.getContestId());

                if (success) {
                    statusLabel.setTextFill(Color.GREEN);
                    statusLabel.setText("Successfully deleted contest.");
                    loadContestData(); // Refresh table
                } else {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Error: Could not delete contest.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Database error. Check console.");
            }
        }
    }
}