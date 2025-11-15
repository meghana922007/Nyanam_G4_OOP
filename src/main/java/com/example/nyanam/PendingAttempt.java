package com.example.nyanam;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * A data model for the GradeExams.fxml TableView.
 * Holds summary info about a student attempt that needs grading.
 */
public class PendingAttempt {

    private final IntegerProperty attemptId;
    private final StringProperty studentName;
    private final StringProperty examName;
    private final StringProperty submittedAt;

    public PendingAttempt(int attemptId, String studentName, String examName, String submittedAt) {
        this.attemptId = new SimpleIntegerProperty(attemptId);
        this.studentName = new SimpleStringProperty(studentName);
        this.examName = new SimpleStringProperty(examName);
        this.submittedAt = new SimpleStringProperty(submittedAt);
    }

    // --- JavaFX Property Getters ---
    // These methods are REQUIRED by the PropertyValueFactory

    public int getAttemptId() {
        return attemptId.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public StringProperty examNameProperty() {
        return examName;
    }

    public StringProperty submittedAtProperty() {
        return submittedAt;
    }
}