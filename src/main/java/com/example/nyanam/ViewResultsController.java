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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewResultsController implements Initializable {


    @FXML private TableView<StudentAttemptSummary> resultsTableView;
    @FXML private TableColumn<StudentAttemptSummary, String> examNameCol;
    @FXML private TableColumn<StudentAttemptSummary, String> submittedAtCol;
    @FXML private TableColumn<StudentAttemptSummary, String> scoreCol;
    @FXML private TableColumn<StudentAttemptSummary, String> statusCol;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private int studentId = Main.getActiveSession().getUserId();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        examNameCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        submittedAtCol.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadResults();
    }

    private void loadResults() {
        try {
            List<StudentAttemptSummary> results = DatabaseConnector.getAttemptsForStudent(studentId);
            ObservableList<StudentAttemptSummary> observableList = FXCollections.observableArrayList(results);
            resultsTableView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load results from database.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 700, 500);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load dashboard.");
        }
    }
}