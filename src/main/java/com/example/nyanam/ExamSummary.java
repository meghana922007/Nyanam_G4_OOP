package com.example.nyanam;


public class ExamSummary {

    private final int examId;
    private final String examName;
    private final int duration;
    private final int questionCount;
    private final int totalMarks;

    public ExamSummary(int examId, String examName, int duration, int questionCount, int totalMarks) {
        this.examId = examId;
        this.examName = examName;
        this.duration = duration;
        this.questionCount = questionCount;
        this.totalMarks = totalMarks;
    }


    public int getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public int getDuration() {
        return duration;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public int getTotalMarks() {
        return totalMarks;
    }
}

