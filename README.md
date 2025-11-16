# NYANAM – Nextgen Youth Academics Navigation Assesment Module
A complete **Java + JavaFX + MySQL (JDBC)** based learning, quiz, and contest management system. 

Nyanam is a JavaFX-based desktop application designed to make learning and assessment more interactive for both teachers and students. The project focuses on applying Object-Oriented Programming (OOP) concepts while building a functional and user-friendly learning management system. 

**GitHub Repo Link: https://github.com/meghana922007/Nyanam_G4_OOP.git**

## Overview
**NYANAM** is an educational assessment platform built using **Java OOP**, **JavaFX (FXML)**, and **MySQL**.

### Teachers can:
- Upload learning resources
- Add quiz & contest questions
- Create quizzes and contests
- View student attempts

### Students can:
- Access uploaded resources
- Take quizzes and contests
- View results and feedback

### Admins can:
- Add teachers & students
- Monitor system activity

##  Key Features

### Teacher Module
- Add quiz questions *(AddQuestionsController)*
- Add contest questions *(AddContestQuestionsController)*
- Upload resources *(ResourceController)*
- View reports *(TeacherReportSummary)*
- Manually grade descriptive questions to get final score *(GradeAttemptController)*
- Manage quizzes 

### Student Module
- Dashboard *(StudentDashboardController)*
- Take quizzes *(TakeExamController)*
- Enter contest lobby *(ContestLobbyController)*
- Participate in contests *(TakeContestController)*
- View resources *(StudentResourcesController)*
- Detailed attempt summaries *(StudentAttemptSummary)*

### Admin Module
- Add teachers *(AddTeacherController)*
- Add students *(AddStudentController)*

## Tech Stack

| Component | Technology |
|---|---|
| **Language** | Java |
| **UI Framework** | JavaFX + FXML |
| **Architecture** | OOP |
| **Database** | MySQL |
| **DB Connector** | JDBC |
| **Models & Controllers** | Custom Java classes |

## Project Structure 

```bash
src/
 └── main/
      ├── java/com/example/nyanam/
      └── resources/com/example/nyanam
```

## Prerequisites
- Java 11 or newer
- JavaFX SDK
- MySQL Server
- MySQL JDBC Connector
- IntelliJ IDEA (Community or Ultimate Edition)

## Installation Guide

### Install IntelliJ IDEA
- Go to [official Jetbrains Download page](https://jetbrains.com/idea/download)
- Choose Community edition (free) or Ultimate edition (paid)
- Run the installer and follow the setup wizard

### Install MySQL
- Download MySQL Installer
     - Go to [MySQL Installer Page](https://dev.mysql.com/downloads/installer/)
     - Select the version and operating system
     - Download MySQL Installer

- Run MySQL Installer
     - Execute the downloaded .msi file
     - Choose "Developer Default" setup type
     - Follow the installation wizard

- Configure MySQL Server
     - Set root password (remember this for later)
     - Choose authentication method (recommended: "Use Strong Password Encryption")
     - Complete the installation

- Verify Installation
     - Open Command Prompt
     - Run: mysql -u root -p
     - Enter your root password

### Download MySQL JDBC Connector
- Go to official downloads [link](https://dev.mysql.com/downloads/connector/j/) for MySQL JDBC Connector
- From the **Select Operating System** dropdown, select **Platform Independent**
- Download the ZIP file
- Extract the ZIP file

## Project Setup

### Clone or download the project
```bash
git clone https://github.com/meghana922007/Nyanam_G4_OOP.git
```
Or download the source files

### Database Setup
Run the following in MySQL Workbench:
```sql
CREATE DATABASE NYANAM_DB_G4;
CREATE USER 'G4'@'localhost' IDENTIFIED BY 'g4';
GRANT ALL PRIVILEGES ON NYANAM_DB_G4.* TO 'G4'@'localhost';
FLUSH PRIVILEGES;

USE NYANAM_DB_G4;

CREATE TABLE Students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE Teachers (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE Admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE tbl_resources (
    resource_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    topic VARCHAR(100),
    subtopic VARCHAR(100),
    file_type VARCHAR(20),
    file_path TEXT NOT NULL
);

CREATE TABLE Exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    exam_type VARCHAR(50) NOT NULL DEFAULT 'Standard',
    duration_minutes INT NOT NULL,
    created_by_teacher_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    FOREIGN KEY (created_by_teacher_id) REFERENCES Teachers(teacher_id) ON DELETE SET NULL
);

CREATE TABLE Contests (
    contest_id INT PRIMARY KEY AUTO_INCREMENT,
    contest_name VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    created_by_admin_id INT,
    FOREIGN KEY (created_by_admin_id) REFERENCES Admins(admin_id) ON DELETE SET NULL
);

CREATE TABLE Questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MCQ', 'Descriptive') NOT NULL,
    marks INT NOT NULL,
    FOREIGN KEY (exam_id) REFERENCES Exams(exam_id) ON DELETE CASCADE
);

CREATE TABLE Contest_Questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    contest_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    marks INT NOT NULL,
    FOREIGN KEY (contest_id) REFERENCES Contests(contest_id) ON DELETE CASCADE
);

CREATE TABLE MCQ_Options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

CREATE TABLE Contest_MCQ_Options (
    option_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Contest_Questions(question_id) ON DELETE CASCADE
);

CREATE TABLE Student_Attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    final_score INT NULL,
    is_graded BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES Exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE
);

CREATE TABLE Contest_Attempts (
    contest_attempt_id INT PRIMARY KEY AUTO_INCREMENT,
    contest_id INT NOT NULL,
    student_id INT NOT NULL,
    anonymous_name VARCHAR(100) NOT NULL,
    final_score INT DEFAULT 0,
    submitted_at TIMESTAMP NULL,
    FOREIGN KEY (contest_id) REFERENCES Contests(contest_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE
);

CREATE TABLE Student_Answers (
    student_answer_id INT AUTO_INCREMENT UNIQUE NOT NULL,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option_id INT NULL,
    answer_text TEXT NULL,
    marks_awarded INT NULL,
    is_correct BOOLEAN NULL,
    PRIMARY KEY (attempt_id, question_id),
    FOREIGN KEY (attempt_id) REFERENCES Student_Attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES MCQ_Options(option_id) ON DELETE SET NULL
);

CREATE TABLE Contest_Answers (
    contest_answer_id INT PRIMARY KEY AUTO_INCREMENT,
    contest_attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option_id INT,
    answer_text TEXT,
    E_CORRECT BOOLEAN,
    marks_awarded INT DEFAULT 0,
    FOREIGN KEY (contest_attempt_id) REFERENCES Contest_Attempts(contest_attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Contest_Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES Contest_MCQ_Options(option_id),
    UNIQUE KEY unq_attempt_question (contest_attempt_id, question_id)
);

INSERT INTO Admins (full_name, username, password) 
VALUES ('Admin User', 'admin', 'admin');

```

### Java SDK Setup  
- Open the project folder in IntelliJ IDEA
- Go to **File** > **Project Structure** or press **Ctrl+Alt+Shift+S**  
- Under **Platform Settings** , click on **SDKs** tab
- Click on the **+** icon and select **Download JDK...**
- In the **Download JDK** dialog box, select the **latest version** of **BellSoft Liberica JDK (Full)**

### MySQL JDBC Connector Setup
- Open the project folder in IntelliJ IDEA
- Go to **File** > **Project Structure** or press **Ctrl+Alt+Shift+S**
- Under **Project Settings** , click on **Libraries** tab
- Click on the **+** icon and select **Java**
- Navigate to the folder where you extracted the **MySQL JDBC Connetor**
- Select the **mysql-connector-9.4.0.jar** file and click **Add**

## How to Run

1. Open the project folder in IntelliJ IDEA
2. **MAKE SURE YOU HAVE FOLLOWED ALL THE STEPS IN THE PROJECT SETUP SECTION**
3. Add VM options:
    - Go to **Run** > **Edit Confidurations...**
    - In the VM options field, add the following line. **If there already is a path, do NOT change it.**
    ```
    --module-path "C:\path\to\your\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml
    ```
    - Click Apply and OK
4. Click on the Green Run Button on the top of the window
5. Login with the default admin ID with    
        **username: "admin"**   
        **password: "admin"**  
6. Add student and teacher users to login as students/teachers and access all their functionalities  
    ```
    Sample login credentials:  
        Teacher: 
        ramesh01  
        rk01  

        Student:  
        priya_cse_01  
        py01
    ```

## Functional Flow

### Student Flow
```
Login → Dashboard → Acess Uploaded Resources → Select Quiz/Contest → Attempt → Submit → Results → Feedback
```

### Teacher Flow
```
Login → Dashboard → Add Questions → Upload Resources → Publish Quiz/Contest → Review Responses
```

### Admin Flow
```
Login → Dashboard → Add Users → Remove Users → Add contests
```

## Notable Classes

### Models
- User, Teacher, Student, Admin
- Resource, UserAccount, UserSession
- ContestSummary, StudentAttemptSummary, TeacherReportSummary

### Controllers
- JavaFX FXML controllers for every screen
- Clean MVC routing
- Handles JDBC CRUD operations

## Database & Persistence
- MySQL tables for users, questions, contests, attempts, resources
- JDBC CRUD operations
- Session-based login handling
- Attempt history tracking

## Future Improvements
- Support for multiple users across different devices
- Online website as alternative
- Real-time leaderboards for contests
- AI-based summary showing weak topics

## Authors
- **Meghana Reddy**  
- **Rishikesh R. Mahato**    
- **Krishna Bansal**  
- **Himanshu Sharma**  
- **Yash Sanghi**  
- **Ritesh Reddy**  
