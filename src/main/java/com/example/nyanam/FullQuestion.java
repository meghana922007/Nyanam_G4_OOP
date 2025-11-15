package com.example.nyanam;

import java.util.List;


public class FullQuestion {
    private Question question;
    private List<McqOption> options; // This will be null or empty for descriptive questions

    // Constructor
    public FullQuestion(Question question, List<McqOption> options) {
        this.question = question;
        this.options = options;
    }

    // Getters
    public Question getQuestion() { return question; }
    public List<McqOption> getOptions() { return options; }
    public boolean isMcq() {
        return "MCQ".equalsIgnoreCase(question.getQuestionType());
    }
}

