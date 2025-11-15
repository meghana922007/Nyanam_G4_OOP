package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the SelectExam.fxml (Student-facing) screen.
 * Fetches and displays a list of available exams for the student to take.
 */
public class SelectExamController implements Initializable {

    // FXML fields for the table
    @FXML private TableView<ExamSummary> examTableView;
    @FXML private TableColumn<ExamSummary, String> examNameCol;
    @FXML private TableColumn<ExamSummary, Integer> durationCol;
    @FXML private TableColumn<ExamSummary, Integer> questionCountCol;
    @FXML private TableColumn<ExamSummary, Integer> totalMarksCol;

    // FXML fields for buttons and status
    @FXML private Button backButton;
    @FXML private Button startButton;
    @FXML private Label statusLabel;

    /**
     * Called by JavaFX when the FXML is loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Set up the table columns
        examNameCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        questionCountCol.setCellValueFactory(new PropertyValueFactory<>("questionCount"));
        totalMarksCol.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));

        // 2. Load the data from the database
        loadExamData();
    }

    /**
     * Fetches the exam summaries from the database and populates the table.
     */
    private void loadExamData() {
        // This method already exists in your DatabaseConnector!
        List<ExamSummary> summaryList = DatabaseConnector.getAllExamSummaries();
        ObservableList<ExamSummary> observableList = FXCollections.observableArrayList(summaryList);
        examTableView.setItems(observableList);
    }

    /**
     * Handles clicking the "Back" button.
     * Returns to the main Student Dashboard.
     */
    @FXML
    private void handleBack() {
        try {
            // Your StudentDashboard.fxml file is 700x500
            Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 700, 500);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load dashboard.");
        }
    }

    /**
     * Handles clicking the "Start Selected Exam" button.
     * This will load Phase 2 (ExamInstructions) and pass the data.
     */
    @FXML
    private void handleStartExam() {
        // 1. Get the selected exam
        ExamSummary selectedExam = examTableView.getSelectionModel().getSelectedItem();

        // 2. Check if an exam is selected
        if (selectedExam == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select an exam to start.");
            return;
        }

        // 3. Navigate to the Exam Instructions screen (Phase 2)
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ExamInstructions.fxml"));
            Parent root = loader.load();

            ExamInstructionsController controller = loader.getController();
            controller.initData(selectedExam);

            // Change the scene
            Main.getPrimaryStage().getScene().setRoot(root);
            Main.getPrimaryStage().setTitle("Exam Instructions");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Error: Could not load exam instructions.");
        }
    }
}