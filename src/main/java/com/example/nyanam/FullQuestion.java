package com.example.nyanam;

import java.util.List;


public class FullQuestion {
    private Question question;
    private List<McqOption> options;

    public FullQuestion(Question question, List<McqOption> options) {
        this.question = question;
        this.options = options;
    }

    public Question getQuestion() { return question; }
    public List<McqOption> getOptions() { return options; }
    public boolean isMcq() {
        return "MCQ".equalsIgnoreCase(question.getQuestionType());
    }
}

