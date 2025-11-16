package com.example.nyanam;


public class Student extends User {

    private String branch;


    public Student(int userId, String username, String fullName, String branch) {
        super(userId, username, fullName);
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
