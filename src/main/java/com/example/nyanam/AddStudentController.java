package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddStudentController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleSave() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        try {

            DatabaseConnector.createStudent(fullName, username, password);

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Student created successfully!");


            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error: Username '" + username + "' already exists.");
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Database error: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {

        Stage stage = (Stage) fullNameField.getScene().getWindow();
        stage.close();
    }
}