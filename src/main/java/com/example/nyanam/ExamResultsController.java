package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class ExamResultsController {

    @FXML private Label examNameLabel;
    @FXML private Label scoreLabel;
    @FXML private Label gradingStatusLabel;
    @FXML private Button closeButton;


    public void initData(ExamSummary exam, int finalScore, int totalQuestions) {
        examNameLabel.setText(exam.getExamName());


        scoreLabel.setText(finalScore + " / " + exam.getTotalMarks());

        if (finalScore < exam.getTotalMarks()) {
            gradingStatusLabel.setText("Your final score may change after descriptive questions are graded by your teacher.");
        } else {
            gradingStatusLabel.setText("All questions have been auto-graded.");
        }
    }

    @FXML
    private void handleClose() {
        try {

            Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 700, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

