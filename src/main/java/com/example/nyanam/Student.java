package com.example.nyanam;

/**
 * Student model, extending the base User.
 */
public class Student extends User {

    private String branch;
    // You can add more fields here, like a list of courses or results
    // List<Result> pastResults;

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
