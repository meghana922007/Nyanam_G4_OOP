package com.example.nyanam;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TakeContestController {

    @FXML private Label contestNameLabel;
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

    private int contestId;
    private int contestAttemptId;
    private String anonymousName;
    private List<FullQuestion> questions;
    private int currentQuestionIndex = 0;


    private Timeline timeline;
    private long timeRemainingInSeconds;


    private Map<Integer, Object> studentAnswers = new HashMap<>();


    public void initData(int contestId, int contestAttemptId, String anonymousName) {
        this.contestId = contestId;
        this.contestAttemptId = contestAttemptId;
        this.anonymousName = anonymousName;


        Map<String, Object> details = DatabaseConnector.getContestDetails(contestId);
        if (details.isEmpty()) {
            statusLabel.setText("Error: Could not load contest details.");
            return;
        }

        contestNameLabel.setText("Contest: " + details.get("contest_name"));


        Timestamp endTime = (Timestamp) details.get("end_time");
        long endTimeMillis = endTime.getTime();
        long nowMillis = System.currentTimeMillis();
        this.timeRemainingInSeconds = (endTimeMillis - nowMillis) / 1000;

        if (this.timeRemainingInSeconds <= 0) {
            statusLabel.setText("Error: This contest has already ended.");
            return;
        }

        this.questions = DatabaseConnector.getFullContestQuestions(contestId);

        if (questions == null || questions.isEmpty()) {
            statusLabel.setText("Error: This contest has no questions.");
            nextButton.setDisable(true);
            return;
        }


        displayQuestion(currentQuestionIndex);
        updateNavigationButtons();
        startTimer();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingInSeconds--;

            if (timeRemainingInSeconds < 0) {
                timeRemainingInSeconds = 0;
            }

            long hours = timeRemainingInSeconds / 3600;
            long minutes = (timeRemainingInSeconds % 3600) / 60;
            long seconds = timeRemainingInSeconds % 60;
            timerLabel.setText(String.format("Time Left: %02d:%02d:%02d", hours, minutes, seconds));

            if (timeRemainingInSeconds <= 300) {
                timerLabel.setTextFill(Color.RED);
            }

            if (timeRemainingInSeconds <= 0) {
                timeline.stop();
                autoSubmit();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void displayQuestion(int index) {
        FullQuestion fq = questions.get(index);
        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        marksLabel.setText("(" + fq.getQuestion().getMarks() + " Marks)");
        questionTextLabel.setText(fq.getQuestion().getQuestionText());

        answerContainer.getChildren().clear();

        if (fq.isMcq()) {
            ToggleGroup toggleGroup = new ToggleGroup();
            for (McqOption option : fq.getOptions()) {
                RadioButton rb = new RadioButton(option.getOptionText());
                rb.setUserData(option.getOptionId());
                rb.setToggleGroup(toggleGroup);
                rb.setStyle("-fx-font-size: 16px; -fx-wrap-text: true;");

                Integer savedAnswerId = (Integer) studentAnswers.get(fq.getQuestion().getQuestionId());
                if (savedAnswerId != null && savedAnswerId == option.getOptionId()) {
                    rb.setSelected(true);
                }
                answerContainer.getChildren().add(rb);
            }
        } else {
            TextArea ta = new TextArea();
            ta.setPromptText("Enter your answer here...");
            ta.setWrapText(true);
            ta.setPrefHeight(200);
            ta.setStyle("-fx-font-size: 16px;");

            String savedAnswerText = (String) studentAnswers.get(fq.getQuestion().getQuestionId());
            if (savedAnswerText != null) {
                ta.setText(savedAnswerText);
            }
            answerContainer.getChildren().add(ta);
        }
    }

    private void saveCurrentAnswer() {
        FullQuestion fq = questions.get(currentQuestionIndex);
        int qId = fq.getQuestion().getQuestionId();

        if (fq.isMcq()) {
            ToggleGroup group = ((RadioButton) answerContainer.getChildren().get(0)).getToggleGroup();
            RadioButton selectedRadio = (RadioButton) group.getSelectedToggle();
            if (selectedRadio != null) {
                Integer selectedOptionId = (Integer) selectedRadio.getUserData();
                studentAnswers.put(qId, selectedOptionId);
                DatabaseConnector.saveContestAnswer(contestAttemptId, qId, selectedOptionId, null);
            }
        } else {
            TextArea ta = (TextArea) answerContainer.getChildren().get(0);
            String answerText = ta.getText();
            if (answerText != null && !answerText.trim().isEmpty()) {
                studentAnswers.put(qId, answerText);
                DatabaseConnector.saveContestAnswer(contestAttemptId, qId, null, answerText);
            }
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
            updateNavigationButtons();
        }
    }

    @FXML
    private void handleNext() {
        if (currentQuestionIndex < questions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == questions.size() - 1);
        submitButton.setVisible(currentQuestionIndex == questions.size() - 1);
    }

    @FXML
    private void handleSubmit() {
        saveCurrentAnswer();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Submit Contest");
        alert.setHeaderText("Are you sure you want to submit?");
        alert.setContentText("You cannot make any more changes after submitting.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            submitContest();
        }
    }

    private void autoSubmit() {
        saveCurrentAnswer();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time's Up!");
        alert.setHeaderText("The contest time has expired.");
        alert.setContentText("Your contest will now be submitted automatically.");
        alert.setOnHidden(e -> submitContest());
        alert.show();
    }

    private void submitContest() {
        if (timeline != null) {
            timeline.stop();
        }
        previousButton.setDisable(true);
        nextButton.setDisable(true);
        submitButton.setDisable(true);
        statusLabel.setText("Submitting and grading... Please wait.");

        new Thread(() -> {
            try {
                int finalScore = DatabaseConnector.submitAndGradeContest(contestAttemptId);
                Platform.runLater(() -> {
                    showResultsScreen(finalScore);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Error submitting contest. Check console.");
                });
            }
        }).start();
    }

    private void showResultsScreen(int finalScore) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ContestResults.fxml"));
            Parent root = loader.load();

            ContestResultsController controller = loader.getController();
            controller.initData(contestId, finalScore, anonymousName);

            Scene scene = new Scene(root, 600, 500);
            Main.getPrimaryStage().setScene(scene);
            Main.getPrimaryStage().setTitle("Contest Results");
            Main.getPrimaryStage().setMaximized(false);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("CRITICAL ERROR: Could not load results screen.");
        }
    }
}