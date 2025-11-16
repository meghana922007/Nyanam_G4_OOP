package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageUsersController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private TableView<UserAccount> studentsTable;
    @FXML private TableColumn<UserAccount, String> studentNameCol;
    @FXML private TableColumn<UserAccount, String> studentUsernameCol;
    @FXML private TableColumn<UserAccount, String> studentStatusCol;

    @FXML private TableView<UserAccount> teachersTable;
    @FXML private TableColumn<UserAccount, String> teacherNameCol;
    @FXML private TableColumn<UserAccount, String> teacherUsernameCol;
    @FXML private TableColumn<UserAccount, String> teacherStatusCol;

    @FXML private Label statusLabel;

    private ObservableList<UserAccount> studentList = FXCollections.observableArrayList();
    private ObservableList<UserAccount> teacherList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        studentUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        studentsTable.setItems(studentList);


        teacherNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        teacherUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        teacherStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        teachersTable.setItems(teacherList);


        loadAllUsers();
    }

    private void loadAllUsers() {
        loadStudents();
        loadTeachers();
    }

    private void loadStudents() {
        try {
            List<UserAccount> students = DatabaseConnector.getAllStudents();
            studentList.setAll(students);
        } catch (Exception e) {
            statusLabel.setText("Error loading students: " + e.getMessage());
        }
    }

    private void loadTeachers() {
        try {
            List<UserAccount> teachers = DatabaseConnector.getAllTeachers();
            teacherList.setAll(teachers);
        } catch (Exception e) {
            statusLabel.setText("Error loading teachers: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("AdminDashboard.fxml", "Admin Dashboard", 800, 600);
    }

    @FXML
    private void handleActivate() {
        updateSelectedUserStatus("Active");
    }

    @FXML
    private void handleDeactivate() {
        updateSelectedUserStatus("Deactivated");
    }

    private void updateSelectedUserStatus(String newStatus) {
        UserAccount selectedUser = getSelectedUser();
        String role = getSelectedRole();

        if (selectedUser == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a user from the list.");
            return;
        }

        if (selectedUser.getStatus().equals(newStatus)) {
            statusLabel.setTextFill(Color.ORANGE);
            statusLabel.setText("User is already " + newStatus + ".");
            return;
        }

        try {
            DatabaseConnector.updateUserStatus(role, selectedUser.getId(), newStatus);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("User " + newStatus.toLowerCase() + " successfully.");
            loadAllUsers(); // Refresh both tables
        } catch (Exception e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        UserAccount selectedUser = getSelectedUser();
        String role = getSelectedRole();

        if (selectedUser == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a user to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("PERMANENTLY DELETE USER?");
        alert.setContentText("Are you sure you want to permanently delete " + selectedUser.fullNameProperty().get() + "?\n\n" +
                "This action cannot be undone and may delete associated data!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseConnector.deleteUser(role, selectedUser.getId());
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("User permanently deleted.");
                loadAllUsers(); // Refresh both tables
            } catch (Exception e) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private String getSelectedRole() {
        return tabPane.getSelectionModel().getSelectedIndex() == 0 ? "student" : "teacher";
    }


    private UserAccount getSelectedUser() {
        if (getSelectedRole().equals("student")) {
            return studentsTable.getSelectionModel().getSelectedItem();
        } else {
            return teachersTable.getSelectionModel().getSelectedItem();
        }
    }
}