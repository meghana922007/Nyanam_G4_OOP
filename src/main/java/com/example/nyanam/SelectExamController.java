package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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


public class SelectExamController implements Initializable {


    @FXML private TableView<ExamSummary> examTableView;
    @FXML private TableColumn<ExamSummary, String> examNameCol;
    @FXML private TableColumn<ExamSummary, Integer> durationCol;
    @FXML private TableColumn<ExamSummary, Integer> questionCountCol;
    @FXML private TableColumn<ExamSummary, Integer> totalMarksCol;


    @FXML private Button backButton;
    @FXML private Button startButton;
    @FXML private Label statusLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        examNameCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        questionCountCol.setCellValueFactory(new PropertyValueFactory<>("questionCount"));
        totalMarksCol.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));


        loadExamData();
    }


    private void loadExamData() {
        // This method already exists in your DatabaseConnector!
        List<ExamSummary> summaryList = DatabaseConnector.getAllExamSummaries();
        ObservableList<ExamSummary> observableList = FXCollections.observableArrayList(summaryList);
        examTableView.setItems(observableList);
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


    @FXML
    private void handleStartExam() {

        ExamSummary selectedExam = examTableView.getSelectionModel().getSelectedItem();


        if (selectedExam == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select an exam to start.");
            return;
        }


        try {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ExamInstructions.fxml"));
            Parent root = loader.load();

            ExamInstructionsController controller = loader.getController();
            controller.initData(selectedExam);


            Main.getPrimaryStage().getScene().setRoot(root);
            Main.getPrimaryStage().setTitle("Exam Instructions");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load exam instructions.");
        }
    }
}