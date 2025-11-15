package com.example.nyanam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import java.io.IOException;

public class CreateExamDetailsController {

    @FXML
    private TextField examNameField;
    @FXML
    private Spinner<Integer> durationSpinner;
    @FXML
    private Label statusLabel;

    @FXML
    private void handleCancel() throws IOException {

        Main.changeScene("ManageExams.fxml", "Manage Exams", 600, 400);
    }

    @FXML
    private void handleSaveAndAddQuestions() {
        String examName = examNameField.getText();
        int duration = durationSpinner.getValue();

        if (examName == null || examName.trim().isEmpty()) {
            statusLabel.setText("Please enter an exam name.");
            return;
        }

        try {

            int newExamId = DatabaseConnector.createExam(examName, duration, 1); // Assuming teacher ID 1 for now

            if (newExamId == -1) {
                statusLabel.setText("Error: Could not save exam to database.");
                return;
            }

            System.out.println("New exam created with ID: " + newExamId);


            FXMLLoader loader = new FXMLLoader(Main.class.getResource("AddQuestions.fxml"));
            Parent root = loader.load();


            AddQuestionsController addQuestionsController = loader.getController();


            addQuestionsController.initData(newExamId, examName);

            Main.getPrimaryStage().setScene(new Scene(root, 700, 600));
            Main.getPrimaryStage().setTitle("Add Questions to: " + examName);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading question page.");
        }
    }
}
