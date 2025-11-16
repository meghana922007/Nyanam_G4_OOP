package com.example.nyanam;


public class Teacher extends User {

    private String department;

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
