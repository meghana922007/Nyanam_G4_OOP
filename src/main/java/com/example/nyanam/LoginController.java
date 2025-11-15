package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;
import com.example.nyanam.UserSession; // Import the UserSession class

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private RadioButton studentRadio;
    @FXML
    private RadioButton teacherRadio;
    @FXML
    private RadioButton adminRadio;
    @FXML
    private ToggleGroup roleToggleGroup;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        RadioButton selectedRoleRadio = (RadioButton) roleToggleGroup.getSelectedToggle();

        if (username.isEmpty() || password.isEmpty() || selectedRoleRadio == null) {
            errorLabel.setText("Please fill in all fields and select a role.");
            return;
        }

        String role;
        String roleId = selectedRoleRadio.getId();
        if ("studentRadio".equals(roleId)) {
            role = "student";
        } else if ("teacherRadio".equals(roleId)) {
            role = "teacher";
        } else if ("adminRadio".equals(roleId)) {
            role = "admin";
        } else {
            errorLabel.setText("Invalid role selected.");
            return;
        }


        UserSession session = DatabaseConnector.validateLogin(username, password, role);

        if (session == null) {
            errorLabel.setText("Invalid username, password, or role.");
            return;
        }
        Main.setActiveSession(session);
        System.out.println("Login successful. Role: " + session.getRole() + ", ID: " + session.getUserId() + ", Name: " + session.getUsername());

        try {
            switch (session.getRole()) {
                case "student":
                    Main.changeScene("StudentDashboard.fxml", "Nyanam - Student Dashboard", 800, 600);
                    break;
                case "teacher":
                    Main.changeScene("TeachersDashboard.fxml", "Nyanam - Teacher Dashboard", 800, 600);
                    break;
                case "admin":
                    Main.changeScene("AdminDashboard.fxml", "Nyanam - Admin Dashboard", 800, 600);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard. Please try again.");
        }
    }
}