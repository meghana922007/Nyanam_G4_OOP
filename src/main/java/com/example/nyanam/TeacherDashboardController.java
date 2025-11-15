package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;

public class TeacherDashboardController {

    @FXML
    private Label statusLabel;
    @FXML
    private void handleManageExams() {
        System.out.println("Teacher selected: Manage Exams");
        try {
            Main.changeScene("ManageExams.fxml", "Manage Exams", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load exam management page.");
        }
    }

    @FXML
    private void handleUploadMaterial() {
        System.out.println("Teacher selected: Upload Material");
        try {

            Main.changeScene("ResourceManagement.fxml", "Resource Management", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load resource management page.");
        }
    }

    @FXML
    private void handleGradeExams() {
        System.out.println("Teacher selected: Grade Exams");
        try {
            Main.changeScene("GradeExams.fxml", "Grade Exams", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load grading page.");
        }
    }

    @FXML
    private void handleViewReports() {
        System.out.println("Teacher selected: View Student Reports");
        try {
            Main.changeScene("ViewReports.fxml", "Student Performance Reports", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load reports page.");
        }
    }

    @FXML
    private void handleLogout() {
        statusLabel.setText("Logging out...");
        System.out.println("Teacher selected: Logout");
        try {

            Main.changeScene("Login_view.fxml", "Nyanam - Login", 600, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}