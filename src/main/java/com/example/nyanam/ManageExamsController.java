package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;


public class ManageExamsController {

    @FXML
    private Label statusLabel;


    @FXML
    private void handleCreateExam() {
        System.out.println("Navigating to Create Exam screen...");
        try {
            Main.changeScene("CreateExamDetails.fxml", "Create New Exam", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load create exam page.");
        }
    }


    @FXML
    private void handleViewExams() {
        System.out.println("Navigating to View Exams screen...");
        try {
            Main.changeScene("ViewExam.fxml", "View All Exams", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load view exams page.");
        }
    }


    @FXML
    private void handleBack() {
        statusLabel.setText("Returning to Dashboard...");
        try {

            Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
