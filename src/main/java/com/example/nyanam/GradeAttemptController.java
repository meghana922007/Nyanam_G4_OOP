package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell; // Import this
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter; // Import this

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GradeAttemptController implements Initializable {


    @FXML private Label titleLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label examNameLabel;
    @FXML private TableView<DescriptiveAnswer> answersTableView;
    @FXML private TableColumn<DescriptiveAnswer, String> questionCol;
    @FXML private TableColumn<DescriptiveAnswer, String> answerCol;
    @FXML private TableColumn<DescriptiveAnswer, Integer> maxMarksCol;
    @FXML private TableColumn<DescriptiveAnswer, Integer> marksAwardedCol;
    @FXML private Label statusLabel;

    private int attemptId;
    private ObservableList<DescriptiveAnswer> answersList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
        maxMarksCol.setCellValueFactory(new PropertyValueFactory<>("maxMarks"));
        marksAwardedCol.setCellValueFactory(new PropertyValueFactory<>("marksAwarded"));

        answersTableView.setEditable(true);

        marksAwardedCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        marksAwardedCol.setOnEditCommit(event -> {
            DescriptiveAnswer answer = event.getRowValue();
            int newValue = event.getNewValue() != null ? event.getNewValue() : 0;

            if(newValue < 0) {
                answer.setMarksAwarded(0);
                statusLabel.setText("Marks must be a positive number.");
            } else if (newValue > answer.maxMarksProperty().get()) {
                answer.setMarksAwarded(answer.maxMarksProperty().get());
                statusLabel.setText("Marks cannot exceed max marks (" + answer.maxMarksProperty().get() + ")");
            } else {
                answer.setMarksAwarded(newValue);
                statusLabel.setText("");
            }
            answer.setMarksAwarded(newValue);
            answersTableView.refresh();
        });

        answersTableView.setItems(answersList);
    }

    public void initData(int attemptId, String studentName, String examName) {
        this.attemptId = attemptId;

        titleLabel.setText("Grading: " + examName);
        studentNameLabel.setText(studentName);
        examNameLabel.setText(examName);

        loadAnswers();
    }

    private void loadAnswers() {
        try {
            List<DescriptiveAnswer> answers = DatabaseConnector.getDescriptiveAnswersForAttempt(attemptId);
            answersList.setAll(answers);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load answers from the database.");
        }
    }

    @FXML
    private void handleSave() {

        if (answersTableView.getEditingCell() != null) {
            answersTableView.getSelectionModel().select(null);

        }
        Map<Integer, Integer> marksMap = new HashMap<>();
        for (DescriptiveAnswer answer : answersList) {
            marksMap.put(answer.getStudentAnswerId(), answer.getMarksAwarded());
        }

        if (marksMap.isEmpty()) {
            statusLabel.setText("No descriptive answers found to grade.");
            return;
        }

        try {

            int finalScore = DatabaseConnector.saveAndFinalizeGrades(attemptId, marksMap);

            showAlert(Alert.AlertType.INFORMATION, "Grades Saved",
                    "All marks have been saved successfully.\n" +
                            "The student's new final score is: " + finalScore);

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save marks to the database: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}