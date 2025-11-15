package com.example.nyanam;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class LeaderboardEntry {

    private final StringProperty rank;
    private final StringProperty anonymousName;
    private final IntegerProperty score;

    public LeaderboardEntry(int rank, String anonymousName, int score) {
        this.rank = new SimpleStringProperty("#" + rank);
        this.anonymousName = new SimpleStringProperty(anonymousName);
        this.score = new SimpleIntegerProperty(score);
    }


    public StringProperty rankProperty() { return rank; }
    public StringProperty anonymousNameProperty() { return anonymousName; }
    public IntegerProperty scoreProperty() { return score; }
}