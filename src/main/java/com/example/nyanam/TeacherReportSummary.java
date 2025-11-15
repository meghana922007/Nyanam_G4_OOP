package com.example.nyanam;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TeacherReportSummary {

    private final StringProperty studentName;
    private final StringProperty examName;
    private final StringProperty submittedAt;
    private final StringProperty score;
    private final StringProperty status;

    public TeacherReportSummary(String studentName, String examName, String submittedAt, int finalScore, int totalMarks, String status) {
        this.studentName = new SimpleStringProperty(studentName);
        this.examName = new SimpleStringProperty(examName);
        this.submittedAt = new SimpleStringProperty(submittedAt);
        this.score = new SimpleStringProperty(finalScore + " / " + totalMarks);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty examNameProperty() { return examName; }
    public StringProperty submittedAtProperty() { return submittedAt; }
    public StringProperty scoreProperty() { return score; }
    public StringProperty statusProperty() { return status; }
}