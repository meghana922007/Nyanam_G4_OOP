package com.example.nyanam;


public class Question {
    private int questionId;
    private int examId;
    private String questionText;
    private String questionType;
    private int marks;

    public Question(int questionId, int examId, String questionText, String questionType, int marks) {
        this.questionId = questionId;
        this.examId = examId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.marks = marks;
    }


    public int getQuestionId() { return questionId; }
    public int getExamId() { return examId; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public int getMarks() { return marks; }
}

