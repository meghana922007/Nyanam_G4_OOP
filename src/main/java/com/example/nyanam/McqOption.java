package com.example.nyanam;

public class McqOption {
    private int optionId;
    private int questionId;
    private String optionText;
    private boolean isCorrect;

    public McqOption(int optionId, int questionId, String optionText, boolean isCorrect) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    // Getters
    public int getOptionId() { return optionId; }
    public int getQuestionId() { return questionId; }
    public String getOptionText() { return optionText; }
    public boolean isCorrect() { return isCorrect; }
}

