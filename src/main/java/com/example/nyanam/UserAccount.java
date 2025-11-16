package com.example.nyanam;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;


public class UserAccount {

    private final IntegerProperty id;
    private final StringProperty fullName;
    private final StringProperty username;
    private final StringProperty status;

    public UserAccount(int id, String fullName, String username, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.username = new SimpleStringProperty(username);
        this.status = new SimpleStringProperty(status);
    }


    public int getId() { return id.get(); }
    public String getStatus() { return status.get(); }

    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty statusProperty() { return status; }
}