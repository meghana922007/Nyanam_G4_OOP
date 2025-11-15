package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AddQuestionsController {

    @FXML private Label examTitleLabel;
    @FXML private ChoiceBox<String> questionTypeChoiceBox;
    @FXML private Spinner<Integer> marksSpinner;
    @FXML private TextArea questionTextArea;
    @FXML private VBox mcqOptionsVBox;
    @FXML private TextField option1Field, option2Field, option3Field, option4Field;
    @FXML private Label statusLabel;


    @FXML private RadioButton option1Radio;
    @FXML private RadioButton option2Radio;
    @FXML private RadioButton option3Radio;
    @FXML private RadioButton option4Radio;

    private ToggleGroup mcqToggleGroup;

    private int currentExamId;
    private String currentExamName;

    public void initData(int examId, String examName) {
        this.currentExamId = examId;
        this.currentExamName = examName;
        examTitleLabel.setText("Add Questions to: " + examName);
    }


    @FXML
    public void initialize() {

        mcqToggleGroup = new ToggleGroup();

        option1Radio.setToggleGroup(mcqToggleGroup);
        option2Radio.setToggleGroup(mcqToggleGroup);
        option3Radio.setToggleGroup(mcqToggleGroup);
        option4Radio.setToggleGroup(mcqToggleGroup);

        questionTypeChoiceBox.setItems(FXCollections.observableArrayList("MCQ", "Descriptive"));
        questionTypeChoiceBox.setValue("Descriptive");

        mcqOptionsVBox.setVisible(false);
        mcqOptionsVBox.setManaged(false);


        questionTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isMCQ = "MCQ".equals(newVal);
            mcqOptionsVBox.setVisible(isMCQ);
            mcqOptionsVBox.setManaged(isMCQ);
        });
    }

    @FXML
    private void handleAddQuestion() {
        String type = questionTypeChoiceBox.getValue();
        String questionText = questionTextArea.getText();
        int marks = marksSpinner.getValue();

        // --- Input Validation ---
        if (questionText.isBlank()) {
            statusLabel.setText("Error: Question text cannot be empty.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        if ("MCQ".equals(type)) {

            if (option1Field.getText().isBlank() || option2Field.getText().isBlank() ||
                    option3Field.getText().isBlank() || option4Field.getText().isBlank()) {
                statusLabel.setText("Error: All 4 MCQ options must be filled.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }

            if (mcqToggleGroup.getSelectedToggle() == null) {
                statusLabel.setText("Error: You must select one correct answer.");
                statusLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }
        }

        int newQuestionId = DatabaseConnector.addQuestion(currentExamId, questionText, type, marks);

        if (newQuestionId != -1) {

            if ("MCQ".equals(type)) {
                DatabaseConnector.addMcqOption(newQuestionId, option1Field.getText(), option1Radio.isSelected());
                DatabaseConnector.addMcqOption(newQuestionId, option2Field.getText(), option2Radio.isSelected());
                DatabaseConnector.addMcqOption(newQuestionId, option3Field.getText(), option3Radio.isSelected());
                DatabaseConnector.addMcqOption(newQuestionId, option4Field.getText(), option4Radio.isSelected());
            }

            statusLabel.setText("Successfully added question!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);


            questionTextArea.clear();
            option1Field.clear();
            option2Field.clear();
            option3Field.clear();
            option4Field.clear();
            mcqToggleGroup.selectToggle(null);

        } else {
            statusLabel.setText("Error: Failed to save question to database.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleFinish() {
        try {

            Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not return to dashboard.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}

