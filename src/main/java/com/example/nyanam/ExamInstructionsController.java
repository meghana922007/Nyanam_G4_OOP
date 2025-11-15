package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ExamInstructionsController {

    @FXML private Label titleLabel;
    @FXML private Label examNameLabel;
    @FXML private Label detailsLabel;
    @FXML private Label countLabel;
    @FXML private Label statusLabel;
    @FXML private Button beginButton;
    @FXML private Button cancelButton;

    private ExamSummary selectedExam;
    private int studentId = Main.getActiveSession().getUserId();


    public void initData(ExamSummary exam) {
        this.selectedExam = exam;
        examNameLabel.setText("Exam: " + selectedExam.getExamName());
        detailsLabel.setText("Duration: " + selectedExam.getDuration() + " minutes");
        countLabel.setText("Number of Questions: " + selectedExam.getQuestionCount());
    }

    @FXML
    private void handleCancel() {
        try {

            Main.changeScene("SelectExam.fxml", "Select an Exam", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load selection screen.");
        }
    }

    @FXML
    private void handleBeginTest() {
        System.out.println("Beginning test: " + selectedExam.getExamName());

        try {

            int attemptId = DatabaseConnector.startExamAttempt(selectedExam.getExamId(), studentId);

            if (attemptId == -1) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error: Could not create exam attempt in database.");
                return;
            }

            // Load the new FXML screen (Phase 3)
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("TakeExam.fxml"));
            Parent root = loader.load();

            // Get the controller and pass in all the data it needs to run
            TakeExamController controller = loader.getController();
            controller.initData(selectedExam, studentId, attemptId);


            Scene scene = new Scene(root);
            Main.getPrimaryStage().setScene(scene);
            Main.getPrimaryStage().setTitle("Taking Exam: " + selectedExam.getExamName());
            Main.getPrimaryStage().setMaximized(true); // Take over the screen

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load the exam.");
        }
    }
}

