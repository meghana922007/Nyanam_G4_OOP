package com.example.nyanam;

public class Admin extends User {

    private int accessLevel;

    public Admin(int userId, String username, String fullName, int accessLevel) {
        super(userId, username, fullName);
        this.accessLevel = accessLevel;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }
}
