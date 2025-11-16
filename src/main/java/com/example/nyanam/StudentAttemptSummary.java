package com.example.nyanam;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class StudentAttemptSummary {

    private final StringProperty examName;
    private final StringProperty submittedAt;
    private final StringProperty score;
    private final StringProperty status;

    public StudentAttemptSummary(String examName, String submittedAt, int finalScore, int totalMarks, String status) {
        this.examName = new SimpleStringProperty(examName);
        this.submittedAt = new SimpleStringProperty(submittedAt);
        this.score = new SimpleStringProperty(finalScore + " / " + totalMarks);
        this.status = new SimpleStringProperty(status);
    }


    public StringProperty examNameProperty() {
        return examName;
    }

    public StringProperty submittedAtProperty() {
        return submittedAt;
    }

    public StringProperty scoreProperty() {
        return score;
    }

    public StringProperty statusProperty() {
        return status;
    }
}
