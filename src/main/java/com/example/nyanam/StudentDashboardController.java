package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;

public class StudentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Button takeExamButton;
    @FXML private Button viewResultsButton;
    @FXML private Button practiceButton;
    @FXML private Button competitiveButton;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;

    public void initialize() {

        welcomeLabel.setText("Welcome, Student!");
    }

    @FXML
    private void handleTakeExam() {
        System.out.println("Student selected: Take Exam");
        try {
            Main.changeScene("SelectExam.fxml", "Select an Exam", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load exam selection page.");
        }
    }

    @FXML
    private void handleViewResults() {
        System.out.println("Student selected: View Results");
        try {

            Main.changeScene("ViewResults.fxml", "My Exam Results", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load results page.");
        }
    }

    @FXML
    private void handlePractice() {
        System.out.println("Student selected: Practice");
        try {

            Main.changeScene("StudentResources.fxml", "Study Materials", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load study materials.");
        }
    }

    @FXML
    private void handleCompetitive() {
        System.out.println("Student selected: Competitive Campus");
        try {
            Main.changeScene("ContestLobby.fxml", "Competitive Campus", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load contest lobby.");
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        System.out.println("Student logging out...");
        Main.changeScene("Login_view.fxml", "Nyanam Login", 600, 500);
    }
}