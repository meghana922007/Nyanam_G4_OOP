package com.example.nyanam;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // Import this
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL; // Import this
import java.util.Optional;
import java.util.ResourceBundle; // Import this

// Implement Initializable to load the path on startup
public class BackupRestoreController implements Initializable {

    @FXML private Label mysqlBinPathLabel; // Was TextField
    @FXML private Button backupButton;
    @FXML private Button restoreButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private String mysqlBinPath; // Private variable to store the path

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the path from the database
        mysqlBinPath = DatabaseConnector.getConfigValue("MYSQL_BIN_PATH");

        if (mysqlBinPath == null || mysqlBinPath.isEmpty()) {
            mysqlBinPathLabel.setText("Path not set. Please set 'MYSQL_BIN_PATH' in System Config.");
            mysqlBinPathLabel.setTextFill(Color.RED);
            // Disable buttons if path is not set
            backupButton.setDisable(true);
            restoreButton.setDisable(true);
        } else {
            mysqlBinPathLabel.setText(mysqlBinPath);
            mysqlBinPathLabel.setTextFill(Color.BLACK);
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("AdminDashboard.fxml", "Admin Dashboard", 800, 600);
    }

    @FXML
    private void handleBackup() {
        if (!isPathValid()) return; // Check if path is valid

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Database Backup");
        fileChooser.setInitialFileName("nyanam_backup.sql");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));

        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) {
            statusLabel.setTextFill(Color.ORANGE);
            statusLabel.setText("Backup cancelled by user.");
            return;
        }

        statusLabel.setTextFill(Color.BLUE);
        statusLabel.setText("Backup in progress... Please wait.");

        new Thread(() -> {
            try {
                // Use the class variable for the path
                boolean success = DatabaseConnector.backupDatabase(mysqlBinPath, file.getAbsolutePath());

                Platform.runLater(() -> {
                    if (success) {
                        statusLabel.setTextFill(Color.GREEN);
                        statusLabel.setText("Database backup successful!");
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Database backup saved to:\n" + file.getAbsolutePath());
                    } else {
                        statusLabel.setTextFill(Color.RED);
                        statusLabel.setText("Backup failed. Check console for errors.");
                        showAlert(Alert.AlertType.ERROR, "Backup Failed", "Could not back up the database.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Backup failed: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleRestore() {
        if (!isPathValid()) return; // Check if path is valid

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Backup File to Restore");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));

        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) {
            statusLabel.setTextFill(Color.ORANGE);
            statusLabel.setText("Restore cancelled by user.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Restore");
        confirmAlert.setHeaderText("RESTORE DATABASE?");
        confirmAlert.setContentText("This will OVERWRITE the current database with the data from the backup file.\n\nThis action cannot be undone. Are you sure you want to proceed?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            statusLabel.setTextFill(Color.ORANGE);
            statusLabel.setText("Restore cancelled by user.");
            return;
        }

        statusLabel.setTextFill(Color.BLUE);
        statusLabel.setText("Restore in progress... Please wait. The application may freeze.");

        new Thread(() -> {
            try {
                // Use the class variable for the path
                boolean success = DatabaseConnector.restoreDatabase(mysqlBinPath, file.getAbsolutePath());

                Platform.runLater(() -> {
                    if (success) {
                        statusLabel.setTextFill(Color.GREEN);
                        statusLabel.setText("Database restore successful!");
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Database has been restored from:\n" + file.getAbsolutePath());
                    } else {
                        statusLabel.setTextFill(Color.RED);
                        statusLabel.setText("Restore failed. Check console for errors.");
                        showAlert(Alert.AlertType.ERROR, "Restore Failed", "Could not restore the database.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Restore failed: " + e.getMessage());
                });
            }
        }).start();
    }

    // Helper to check if the path was loaded
    private boolean isPathValid() {
        if (mysqlBinPath == null || mysqlBinPath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Path Not Set", "The MySQL 'bin' path is not set. Please set 'MYSQL_BIN_PATH' in the System Config screen.");
            return false;
        }
        if (!new File(mysqlBinPath).exists()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Path", "The MySQL 'bin' path found in System Config is invalid or does not exist:\n" + mysqlBinPath);
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) backButton.getScene().getWindow();
    }
}