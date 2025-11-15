package com.example.nyanam;

/**
 * Teacher model, extending the base User.
 */
public class Teacher extends User {

    private String department;
    // You can add a list of courses they teach
    // List<Course> coursesTaught;

    public Teacher(int userId, String username, String fullName, String department) {
        super(userId, username, fullName);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
