package com.example.nyanam;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A data model class to hold a student's past exam attempt summary.
 * This is used for the "View Results" TableView.
 */
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

    // --- JavaFX Property Getters ---
    // These are required by the PropertyValueFactory

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
