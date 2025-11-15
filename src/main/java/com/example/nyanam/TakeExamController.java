package com.example.nyanam;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TakeExamController {

    // FXML fields from the view
    @FXML private Label examNameLabel;
    @FXML private Label timerLabel;
    @FXML private VBox questionContainer;
    @FXML private Label questionNumberLabel;
    @FXML private Label marksLabel;
    @FXML private Label questionTextLabel;
    @FXML private VBox answerContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;
    @FXML private Label statusLabel;

    // Exam-related data
    private ExamSummary exam;
    private int studentId;
    private int attemptId;
    private List<FullQuestion> questions;
    private int currentQuestionIndex = 0;

    // Timer
    private Timeline timeline;
    private long timeRemainingInSeconds;

    // To store student's answers
    private Map<Integer, Object> studentAnswers = new HashMap<>();

    /**
     * Called by ExamInstructionsController to start the exam.
     */
    public void initData(ExamSummary exam, int studentId, int attemptId) {
        this.exam = exam;
        this.studentId = studentId;
        this.attemptId = attemptId;
        this.timeRemainingInSeconds = exam.getDuration() * 60;

        examNameLabel.setText("Exam: " + exam.getExamName());

        // Fetch all questions from DB
        this.questions = DatabaseConnector.getFullExamQuestions(exam.getExamId());

        if (questions == null || questions.isEmpty()) {
            // Handle error - no questions found
            statusLabel.setText("Error: This exam has no questions.");
            nextButton.setDisable(true);
            return;
        }

        answerContainer.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        answerContainer.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCodeCombination copy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
            KeyCodeCombination paste = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
            KeyCodeCombination cut = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);

            if (copy.match(event) || paste.match(event) || cut.match(event)) {
                event.consume();
            }
        });

        // Not tested
//        examNameLabel.sceneProperty().addListener((observable, oldScene, newScene) -> {
//            if (newScene != null) {
//                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
//                    if (newWindow != null) {
//                        newWindow.focusedProperty().addListener((prop, wasFocused, isNowFocused) -> {
//                            if (!isNowFocused && newWindow.isShowing()) {
//                                Alert alert = new Alert(Alert.AlertType.WARNING);
//                                alert.setTitle("Warning");
//                                alert.setHeaderText("Window Focus Lost");
//                                alert.setContentText("Warning: You have switched away from the exam window. This activity will be logged.");
//                                alert.show();
//                            }
//                        });
//                    }
//                });
//            }
//        });

        // Start the UI
        displayQuestion(currentQuestionIndex);
        updateNavigationButtons();
        startTimer();
    }

    /**
     * Configures and starts the countdown timer.
     */
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingInSeconds--;

            // Format time as HH:mm:ss
            long hours = timeRemainingInSeconds / 3600;
            long minutes = (timeRemainingInSeconds % 3600) / 60;
            long seconds = timeRemainingInSeconds % 60;
            timerLabel.setText(String.format("Time Left: %02d:%02d:%02d", hours, minutes, seconds));

            // Change color to red in the last 5 minutes
            if (timeRemainingInSeconds <= 300) {
                timerLabel.setTextFill(Color.RED);
            }

            // Auto-submit when time runs out
            if (timeRemainingInSeconds <= 0) {
                timeline.stop();
                autoSubmit();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Loads the question and answer options for the given index.
     */
    private void displayQuestion(int index) {
        FullQuestion fq = questions.get(index);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        marksLabel.setText("(" + fq.getQuestion().getMarks() + " Marks)");
        questionTextLabel.setText(fq.getQuestion().getQuestionText());

        // Clear previous answer options
        answerContainer.getChildren().clear();

        if (fq.isMcq()) {
            // It's an MCQ, create RadioButtons
            ToggleGroup toggleGroup = new ToggleGroup();
            for (McqOption option : fq.getOptions()) {
                RadioButton rb = new RadioButton(option.getOptionText());
                rb.setUserData(option.getOptionId()); // Store the option ID
                rb.setToggleGroup(toggleGroup);
                rb.getStyleClass().add("answer-radio-button");

                // Check if this was the student's previous answer
                Integer savedAnswerId = (Integer) studentAnswers.get(fq.getQuestion().getQuestionId());
                if (savedAnswerId != null && savedAnswerId == option.getOptionId()) {
                    rb.setSelected(true);
                }

                answerContainer.getChildren().add(rb);
            }
        } else {
            // It's Descriptive, create a TextArea
            TextArea ta = new TextArea();
            ta.setPromptText("Enter your answer here...");
            ta.setWrapText(true);
            ta.setPrefHeight(200);
            ta.getStyleClass().add("answer-text-area");

            // Check for saved answer
            String savedAnswerText = (String) studentAnswers.get(fq.getQuestion().getQuestionId());
            if (savedAnswerText != null) {
                ta.setText(savedAnswerText);
            }

            answerContainer.getChildren().add(ta);
        }
    }

    /**
     * Saves the current question's answer to the map and database.
     */
    private void saveCurrentAnswer() {
        FullQuestion fq = questions.get(currentQuestionIndex);
        int qId = fq.getQuestion().getQuestionId();

        if (fq.isMcq()) {
            ToggleGroup group = ((RadioButton) answerContainer.getChildren().get(0)).getToggleGroup();
            RadioButton selectedRadio = (RadioButton) group.getSelectedToggle();

            if (selectedRadio != null) {
                Integer selectedOptionId = (Integer) selectedRadio.getUserData();
                studentAnswers.put(qId, selectedOptionId);
                // Save to DB
                DatabaseConnector.saveStudentAnswer(attemptId, qId, selectedOptionId, null);
            }
        } else {
            TextArea ta = (TextArea) answerContainer.getChildren().get(0);
            String answerText = ta.getText();
            if (answerText != null && !answerText.trim().isEmpty()) {
                studentAnswers.put(qId, answerText);
                // Save to DB
                DatabaseConnector.saveStudentAnswer(attemptId, qId, null, answerText);
            }
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer(); // Save before moving
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
            updateNavigationButtons();
        }
    }

    @FXML
    private void handleNext() {
        if (currentQuestionIndex < questions.size() - 1) {
            saveCurrentAnswer(); // Save before moving
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
            updateNavigationButtons();
        }
    }

    /**
     * Manages the state of the Previous, Next, and Submit buttons.
     */
    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == questions.size() - 1);
        submitButton.setVisible(currentQuestionIndex == questions.size() - 1);
    }

    @FXML
    private void handleSubmit() {
        // Save the very last answer
        saveCurrentAnswer();

        // Show a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Submit Exam");
        alert.setHeaderText("Are you sure you want to submit?");
        alert.setContentText("You cannot make any more changes after submitting.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            submitExam();
        }
    }

    /**
     * Called by timer when time is up.
     */
    private void autoSubmit() {
        saveCurrentAnswer();

        // Show an info box
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time's Up!");
        alert.setHeaderText("Your time for this exam has expired.");
        alert.setContentText("Your exam will now be submitted automatically.");

        // We must run the rest on the JavaFX thread after the dialog is closed
        alert.setOnHidden(e -> submitExam());
        alert.show();
    }

    /**
     * --- (THIS METHOD IS UPDATED) ---
     * The final step. Stops timer, grades, and moves to the results screen.
     * Now includes error handling for the background thread.
     */
    private void submitExam() {
        if (timeline != null) {
            timeline.stop();
        }

        // Disable all buttons to prevent double-submission
        previousButton.setDisable(true);
        nextButton.setDisable(true);
        submitButton.setDisable(true);
        statusLabel.setText("Submitting and grading... Please wait.");

        // --- Run the grading on a separate thread so the UI doesn't freeze ---
        new Thread(() -> {
            try {
                // --- This is the database call ---
                int finalScore = DatabaseConnector.submitAndGradeExam(attemptId);

                // --- Success: Update UI on FX thread ---
                Platform.runLater(() -> {
                    showResultsScreen(finalScore);
                });

            } catch (Exception e) {
                // --- Failure: Print error and update UI ---
                e.printStackTrace(); // This will print the SQL error to your console

                Platform.runLater(() -> {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Error submitting exam. Check console for details.");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Submission Error");
                    alert.setHeaderText("A database error occurred while submitting your exam.");
                    alert.setContentText("Please contact your administrator. Details: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }

    /**
     * --- (THIS METHOD IS UPDATED) ---
     * Loads the final (Phase 4) results screen.
     * Now catches *all* Exceptions (like NullPointerException)
     * to prevent silent failures.
     */
    private void showResultsScreen(int finalScore) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ExamResults.fxml"));
            Parent root = loader.load();

            // This check is important. If the FXML file isn't found,
            // getResource() returns null, and loader.load() throws an Exception.
            if (root == null) {
                throw new IOException("FXML file not found: ExamResults.fxml");
            }

            ExamResultsController controller = loader.getController();
            controller.initData(exam, finalScore, questions.size());

            Scene scene = new Scene(root, 600, 400);
            Main.getPrimaryStage().setScene(scene);
            Main.getPrimaryStage().setTitle("Exam Results");
            Main.getPrimaryStage().setMaximized(false); // Go back to a normal window

        } catch (Exception e) { // Catch ALL exceptions
            e.printStackTrace(); // Print the full error (e.g., NullPointerException)

            // Update the UI to show a helpful error message
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("CRITICAL ERROR: Could not load results screen. Check console.");

            // Also show an alert box to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Loading Results");
            alert.setHeaderText("A critical error occurred while loading the results page.");
            alert.setContentText("This is likely a missing file (ExamResults.fxml). " +
                    "Please contact your administrator. Details: " + e.getMessage());
            alert.showAndWait();
        }
    }
}