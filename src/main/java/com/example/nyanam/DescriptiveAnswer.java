package com.example.nyanam;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;


public class DescriptiveAnswer {

    private final IntegerProperty studentAnswerId;
    private final StringProperty questionText;
    private final StringProperty answerText;
    private final IntegerProperty maxMarks;
    private final IntegerProperty marksAwarded;

    public DescriptiveAnswer(int studentAnswerId, String questionText, String answerText, int maxMarks,int intitialMarks) {
        this.studentAnswerId = new SimpleIntegerProperty(studentAnswerId);
        this.questionText = new SimpleStringProperty(questionText);
        this.answerText = new SimpleStringProperty(answerText);
        this.maxMarks = new SimpleIntegerProperty(maxMarks);
        this.marksAwarded = new SimpleIntegerProperty( intitialMarks);
    }


    public int getMarksAwarded() { return marksAwarded.get(); }
    public void setMarksAwarded(int marks) { this.marksAwarded.set(marks); }
    public IntegerProperty marksAwardedProperty() { return marksAwarded; }

    public int getStudentAnswerId() { return studentAnswerId.get(); }

    public StringProperty questionTextProperty() { return questionText; }
    public StringProperty answerTextProperty() { return answerText; }
    public IntegerProperty maxMarksProperty() { return maxMarks; }
}