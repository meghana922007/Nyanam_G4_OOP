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


public class ViewExamsController implements Initializable {


    @FXML private TableView<ExamSummary> examTableView;
    @FXML private TableColumn<ExamSummary, String> examNameCol;
    @FXML private TableColumn<ExamSummary, Integer> durationCol;
    @FXML private TableColumn<ExamSummary, Integer> questionCountCol;
    @FXML private TableColumn<ExamSummary, Integer> totalMarksCol;

    @FXML private Button backButton;
    @FXML private Button deleteButton;
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

        List<ExamSummary> summaryList = DatabaseConnector.getAllExamSummaries();


        ObservableList<ExamSummary> observableList = FXCollections.observableArrayList(summaryList);

        examTableView.setItems(observableList);
    }


    @FXML
    private void handleDelete() {

        ExamSummary selectedExam = examTableView.getSelectionModel().getSelectedItem();


        if (selectedExam == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select an exam to delete.");
            return;
        }


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Exam");
        alert.setHeaderText("Are you sure you want to delete this exam?");
        alert.setContentText("Exam: " + selectedExam.getExamName() + "\n\n" +
                "This action is permanent and will delete all associated questions.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                boolean success = DatabaseConnector.deleteExamById(selectedExam.getExamId());

                if (success) {
                    statusLabel.setTextFill(Color.GREEN);
                    statusLabel.setText("Successfully deleted exam: " + selectedExam.getExamName());

                    loadExamData();
                } else {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Error: Could not delete the exam from the database.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Database error. See console for details.");
            }
        } else {

            statusLabel.setTextFill(Color.BLACK);
            statusLabel.setText("Delete operation cancelled.");
        }
    }
    @FXML
    private void handleBack() {
        try {
            Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load menu.");
        }
    }
}

