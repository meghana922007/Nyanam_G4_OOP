package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Label statusLabel;

    @FXML
    private void handleAddTeacher() {
        System.out.println("Admin selected: Add Teacher");
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("AddTeacher.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Teacher");
            modalStage.setScene(new Scene(root, 400, 400));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner((Stage) statusLabel.getScene().getWindow());
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load 'Add Teacher' form.");
        }
    }

    @FXML
    private void handleAddStudent() {
        System.out.println("Admin selected: Add Student");
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("AddStudent.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Add New Student");
            modalStage.setScene(new Scene(root, 400, 400));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner((Stage) statusLabel.getScene().getWindow());
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load 'Add Student' form.");
        }
    }

    @FXML
    private void handleManageContests() {
        System.out.println("Admin selected: Manage Contests");
        try {
            Main.changeScene("ManageContests.fxml", "Manage Contests", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load contest page.");
        }
    }


    @FXML
    private void handleManageUsers() {
        System.out.println("Admin selected: Manage Users");
        try {
            Main.changeScene("ManageUsers.fxml", "Manage User Accounts", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load user management page.");
        }
    }

  
    @FXML
    private void handleLogout() {
        statusLabel.setText("Logging out...");
        System.out.println("Admin selected: Logout");
        try {
            Main.changeScene("Login_view.fxml", "Nyanam - Login", 600, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}