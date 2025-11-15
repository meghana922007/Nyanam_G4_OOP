package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Import
import javafx.scene.Parent;    // Import
import javafx.scene.Scene;    // Import
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateContestController {

    @FXML private TextField contestNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Label statusLabel;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private void handleCancel() throws IOException {
        Main.changeScene("ManageContests.fxml", "Manage Contests", 800, 600);
    }

    @FXML
    private void handleSave() {
        String contestName = contestNameField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (contestName.isEmpty() || startDate == null || endDate == null ||
                startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        try {
            LocalTime startTime = LocalTime.parse(startTimeField.getText().trim(), timeFormatter);
            LocalTime endTime = LocalTime.parse(endTimeField.getText().trim(), timeFormatter);

            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("End time must be after start time.");
                return;
            }

            int adminId = Main.getActiveSession().getUserId();

            int newContestId = DatabaseConnector.createContest(contestName, startTimestamp, endTimestamp, adminId);

            if (newContestId == -1) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error: Could not save contest to database.");
                return;
            }

            System.out.println("New contest created with ID: " + newContestId);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("AddContestQuestions.fxml"));
            Parent root = loader.load();

            AddContestQuestionsController addQuestionsController = loader.getController();
            addQuestionsController.initData(newContestId, contestName);

            Main.getPrimaryStage().setScene(new Scene(root, 700, 600));
            Main.getPrimaryStage().setTitle("Add Questions to: " + contestName);

        } catch (DateTimeParseException e) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Invalid time format. Please use HH:mm (e.g., 14:00).");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}