package com.example.nyanam;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;


public class ContestSummary {

    private final IntegerProperty contestId;
    private final StringProperty contestName;
    private final StringProperty startTime;
    private final StringProperty endTime;
    private final IntegerProperty questionCount;

    public ContestSummary(int contestId, String contestName, String startTime, String endTime, int questionCount) {
        this.contestId = new SimpleIntegerProperty(contestId);
        this.contestName = new SimpleStringProperty(contestName);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
        this.questionCount = new SimpleIntegerProperty(questionCount);
    }


    public int getContestId() { return contestId.get(); }
    public StringProperty contestNameProperty() { return contestName; }
    public StringProperty startTimeProperty() { return startTime; }
    public StringProperty endTimeProperty() { return endTime; }
    public IntegerProperty questionCountProperty() { return questionCount; }
}