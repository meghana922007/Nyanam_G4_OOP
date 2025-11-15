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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewReportsController implements Initializable {

    @FXML private TableView<TeacherReportSummary> reportTableView;
    @FXML private TableColumn<TeacherReportSummary, String> studentNameCol;
    @FXML private TableColumn<TeacherReportSummary, String> examNameCol;
    @FXML private TableColumn<TeacherReportSummary, String> submittedAtCol;
    @FXML private TableColumn<TeacherReportSummary, String> scoreCol;
    @FXML private TableColumn<TeacherReportSummary, String> statusCol;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        examNameCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        submittedAtCol.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadReportData();
    }


    private void loadReportData() {
        try {
            List<TeacherReportSummary> results = DatabaseConnector.getAllStudentAttemptsSummary();
            ObservableList<TeacherReportSummary> observableList = FXCollections.observableArrayList(results);
            reportTableView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load reports from database.");
        }
    }


    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);
    }
}