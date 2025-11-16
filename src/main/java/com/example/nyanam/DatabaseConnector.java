package com.example.nyanam;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/NYANAM_DB_G4";
    private static final String DB_USER = "G4";
    private static final String DB_PASSWORD = "g4";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static UserSession validateLogin(String username, String password, String role) {
        String sql;
        String idColumn;
        String table;

        switch (role) {
            case "student":
                table = "Students";
                idColumn = "student_id";
                break;
            case "teacher":
                table = "Teachers";
                idColumn = "teacher_id";
                break;
            case "admin":
                table = "Admins";
                idColumn = "admin_id";
                break;
            default:
                return null;
        }

        sql = "SELECT * FROM " + table + " WHERE username = ? AND password = ? AND status = 'Active'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt(idColumn);
                    String userFullName = rs.getString("full_name");
                    return new UserSession(userId, userFullName, role);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void createTeacher(String fullName, String username, String password) throws SQLException {
        String sql = "INSERT INTO Teachers (full_name, username, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }

    public static void createStudent(String fullName, String username, String password) throws SQLException {
        String sql = "INSERT INTO Students (full_name, username, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }

    public static List<UserAccount> getAllStudents() {
        List<UserAccount> students = new ArrayList<>();
        String sql = "SELECT student_id, full_name, username, status FROM Students";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new UserAccount(
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static List<UserAccount> getAllTeachers() {
        List<UserAccount> teachers = new ArrayList<>();
        String sql = "SELECT teacher_id, full_name, username, status FROM Teachers";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(new UserAccount(
                        rs.getInt("teacher_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    public static void updateUserStatus(String role, int userId, String status) throws SQLException {
        String table = role.equals("student") ? "Students" : "Teachers";
        String idColumn = role.equals("student") ? "student_id" : "teacher_id";
        String sql = "UPDATE " + table + " SET status = ? WHERE " + idColumn + " = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteUser(String role, int userId) throws SQLException {
        String table = role.equals("student") ? "Students" : "Teachers";
        String idColumn = role.equals("student") ? "student_id" : "teacher_id";
        String sql = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static int createExam(String examName, int duration, int teacherId) {
        String sql = "INSERT INTO Exams (exam_name, duration_minutes, created_by_teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, examName);
            pstmt.setInt(2, duration);
            pstmt.setInt(3, teacherId);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int addQuestion(int examId, String questionText, String questionType, int marks) {
        String sql = "INSERT INTO Questions (exam_id, question_text, question_type, marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, examId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, questionType);
            pstmt.setInt(4, marks);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addMcqOption(int questionId, String optionText, boolean isCorrect) {
        String sql = "INSERT INTO MCQ_Options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, optionText);
            pstmt.setBoolean(3, isCorrect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<ExamSummary> getAllExamSummaries() {
        List<ExamSummary> examSummaries = new ArrayList<>();
        String sql = "SELECT e.exam_id, e.exam_name, e.duration_minutes, COUNT(q.question_id) AS question_count, COALESCE(SUM(q.marks), 0) AS total_marks " +
                "FROM Exams e LEFT JOIN Questions q ON e.exam_id = q.exam_id " +
                "WHERE e.exam_type = 'Standard' " +
                "GROUP BY e.exam_id, e.exam_name, e.duration_minutes " +
                "ORDER BY e.exam_name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                examSummaries.add(new ExamSummary(rs.getInt("exam_id"), rs.getString("exam_name"), rs.getInt("duration_minutes"), rs.getInt("question_count"), rs.getInt("total_marks")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return examSummaries;
    }

    public static boolean deleteExamById(int examId) {
        String sql = "DELETE FROM Exams WHERE exam_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int startExamAttempt(int examId, int studentId) {
        String sql = "INSERT INTO Student_Attempts (exam_id, student_id, started_at) VALUES (?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, examId);
            pstmt.setInt(2, studentId);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<FullQuestion> getFullExamQuestions(int examId) {
        List<FullQuestion> fullQuestions = new ArrayList<>();
        Map<Integer, Question> questionMap = new HashMap<>();
        Map<Integer, List<McqOption>> optionsMap = new HashMap<>();
        String sql = "SELECT q.question_id, q.exam_id, q.question_text, q.question_type, q.marks, o.option_id, o.option_text, o.is_correct " +
                "FROM Questions q LEFT JOIN MCQ_Options o ON q.question_id = o.question_id " +
                "WHERE q.exam_id = ? ORDER BY q.question_id, o.option_id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int qId = rs.getInt("question_id");
                    if (!questionMap.containsKey(qId)) {
                        questionMap.put(qId, new Question(qId, rs.getInt("exam_id"), rs.getString("question_text"), rs.getString("question_type"), rs.getInt("marks")));
                        optionsMap.put(qId, new ArrayList<>());
                    }
                    int optionId = rs.getInt("option_id");
                    if (!rs.wasNull()) {
                        optionsMap.get(qId).add(new McqOption(optionId, qId, rs.getString("option_text"), rs.getBoolean("is_correct")));
                    }
                }
            }
            for (int qId : questionMap.keySet()) {
                fullQuestions.add(new FullQuestion(questionMap.get(qId), optionsMap.get(qId)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullQuestions;
    }

    public static void saveStudentAnswer(int attemptId, int questionId, Integer selectedOptionId, String answerText) {
        String sql = "REPLACE INTO Student_Answers (attempt_id, question_id, selected_option_id, answer_text) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attemptId);
            pstmt.setInt(2, questionId);
            if (selectedOptionId != null) pstmt.setInt(3, selectedOptionId);
            else pstmt.setNull(3, Types.INTEGER);
            if (answerText != null && !answerText.isEmpty()) pstmt.setString(4, answerText);
            else pstmt.setNull(4, Types.VARCHAR);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int submitAndGradeExam(int attemptId) {
        int totalScore = 0;
        String gradingSql = "UPDATE Student_Answers sa JOIN MCQ_Options mo ON sa.selected_option_id = mo.option_id JOIN Questions q ON sa.question_id = q.question_id " +
                "SET sa.is_correct = mo.is_correct, sa.marks_awarded = CASE WHEN mo.is_correct = 1 THEN q.marks ELSE 0 END " +
                "WHERE sa.attempt_id = ? AND q.question_type = 'MCQ'";
        String sumSql = "SELECT SUM(marks_awarded) AS total_score FROM Student_Answers WHERE attempt_id = ?";
        String finalUpdateSql = "UPDATE Student_Attempts SET submitted_at = NOW(), final_score = ?, is_graded = ? WHERE attempt_id = ?";
        try (Connection conn = getConnection()) {
            try (PreparedStatement gradeStmt = conn.prepareStatement(gradingSql)) {
                gradeStmt.setInt(1, attemptId);
                gradeStmt.executeUpdate();
            }
            try (PreparedStatement sumStmt = conn.prepareStatement(sumSql)) {
                sumStmt.setInt(1, attemptId);
                try (ResultSet rs = sumStmt.executeQuery()) {
                    if (rs.next()) totalScore = rs.getInt("total_score");
                }
            }
            boolean hasDescriptive = hasDescriptiveQuestions(conn, attemptId);
            try (PreparedStatement finalStmt = conn.prepareStatement(finalUpdateSql)) {
                finalStmt.setInt(1, totalScore);
                finalStmt.setBoolean(2, !hasDescriptive);
                finalStmt.setInt(3, attemptId);
                finalStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalScore;
    }

    private static boolean hasDescriptiveQuestions(Connection conn, int attemptId) throws SQLException {
        String sql = "SELECT 1 FROM Student_Answers sa JOIN Questions q ON sa.question_id = q.question_id WHERE sa.attempt_id = ? AND q.question_type = 'Descriptive' LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attemptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static List<StudentAttemptSummary> getAttemptsForStudent(int studentId) {
        List<StudentAttemptSummary> attempts = new ArrayList<>();
        String sql = "SELECT e.exam_name, sa.submitted_at, sa.final_score, sa.is_graded, (SELECT COALESCE(SUM(q.marks), 0) FROM Questions q WHERE q.exam_id = e.exam_id) AS total_marks " +
                "FROM Student_Attempts sa JOIN Exams e ON sa.exam_id = e.exam_id " +
                "WHERE sa.student_id = ? AND sa.submitted_at IS NOT NULL ORDER BY sa.submitted_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("submitted_at");
                    String submittedAt = (ts != null) ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                    String status = rs.getBoolean("is_graded") ? "Graded" : "Pending Review";
                    attempts.add(new StudentAttemptSummary(rs.getString("exam_name"), submittedAt, rs.getInt("final_score"), rs.getInt("total_marks"), status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attempts;
    }


    public static List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM tbl_resources ORDER BY resource_id DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Resource res = new Resource();
                res.setResourceId(rs.getInt("resource_id"));
                res.setTitle(rs.getString("title"));
                res.setTopic(rs.getString("topic"));
                res.setSubtopic(rs.getString("subtopic"));
                res.setType(rs.getString("file_type"));
                res.setFilePath(rs.getString("file_path"));
                resources.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resources;
    }

    public static void addResource(String title, String topic, String subtopic, String type, String path) throws SQLException {
        String sql = "INSERT INTO tbl_resources (title, topic, subtopic, file_type, file_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, topic);
            pstmt.setString(3, subtopic);
            pstmt.setString(4, type);
            pstmt.setString(5, path);
            pstmt.executeUpdate();
        }
    }

    public static void deleteResource(int resourceId) throws SQLException {
        String sql = "DELETE FROM tbl_resources WHERE resource_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resourceId);
            pstmt.executeUpdate();
        }
    }


    public static List<PendingAttempt> getAttemptsPendingReview() {
        List<PendingAttempt> attempts = new ArrayList<>();
        String sql = "SELECT sa.attempt_id, COALESCE(s.full_name, 'Unknown Student') AS student_name, e.exam_name, sa.submitted_at " +
                "FROM Student_Attempts sa JOIN Exams e ON sa.exam_id = e.exam_id LEFT JOIN Students s ON sa.student_id = s.student_id " +
                "WHERE sa.is_graded = 0 AND sa.submitted_at IS NOT NULL ORDER BY sa.submitted_at ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("submitted_at");
                String submittedAt = (ts != null) ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                attempts.add(new PendingAttempt(rs.getInt("attempt_id"), rs.getString("student_name"), rs.getString("exam_name"), submittedAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attempts;
    }

    public static List<DescriptiveAnswer> getDescriptiveAnswersForAttempt(int attemptId) {
        List<DescriptiveAnswer> answers = new ArrayList<>();
        String sql = "SELECT sa.student_answer_id, q.question_text, sa.answer_text, q.marks, sa.marks_awarded " + // <--- ADDED COLUMN HERE
                "FROM Student_Answers sa JOIN Questions q ON sa.question_id = q.question_id " +
                "WHERE sa.attempt_id = ? AND q.question_type = 'Descriptive'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attemptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int initialMarks = rs.getInt("marks_awarded");
                    if (rs.wasNull()) {
                        initialMarks = 0; // Treat NULL marks (ungraded) as 0
                    }

                    answers.add(new DescriptiveAnswer(
                            rs.getInt("student_answer_id"),
                            rs.getString("question_text"),
                            rs.getString("answer_text"),
                            rs.getInt("marks"),
                            initialMarks // Pass the value here
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    public static int saveAndFinalizeGrades(int attemptId, Map<Integer, Integer> marksMap) throws SQLException {
        String updateAnswerSql = "UPDATE Student_Answers SET marks_awarded = ? WHERE student_answer_id = ?";
        String sumTotalSql = "SELECT SUM(COALESCE(marks_awarded, 0)) FROM Student_Answers WHERE attempt_id = ?";
        String finalizeAttemptSql = "UPDATE Student_Attempts SET final_score = ?, is_graded = 1 WHERE attempt_id = ?";
        Connection conn = null;
        int finalScore = 0;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(updateAnswerSql)) {
                for (Map.Entry<Integer, Integer> entry : marksMap.entrySet()) {
                    pstmt.setInt(1, entry.getValue());
                    pstmt.setInt(2, entry.getKey());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sumTotalSql)) {
                pstmt.setInt(1, attemptId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) finalScore = rs.getInt(1);
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(finalizeAttemptSql)) {
                pstmt.setInt(1, finalScore);
                pstmt.setInt(2, attemptId);
                pstmt.executeUpdate();
            }
            conn.commit();
            return finalScore;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static List<TeacherReportSummary> getAllStudentAttemptsSummary() {
        List<TeacherReportSummary> attempts = new ArrayList<>();
        String sql = "SELECT COALESCE(s.full_name, 'Unknown Student') AS student_name, e.exam_name, sa.submitted_at, sa.final_score, sa.is_graded, (SELECT COALESCE(SUM(q.marks), 0) FROM Questions q WHERE q.exam_id = e.exam_id) AS total_marks " +
                "FROM Student_Attempts sa JOIN Exams e ON sa.exam_id = e.exam_id LEFT JOIN Students s ON sa.student_id = s.student_id " +
                "WHERE sa.submitted_at IS NOT NULL ORDER BY sa.submitted_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("submitted_at");
                String submittedAt = (ts != null) ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
                String status = rs.getBoolean("is_graded") ? "Graded" : "Pending Review";
                attempts.add(new TeacherReportSummary(rs.getString("student_name"), rs.getString("exam_name"), submittedAt, rs.getInt("final_score"), rs.getInt("total_marks"), status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attempts;
    }


    public static int createContest(String contestName, Timestamp startTime, Timestamp endTime, int adminId) {
        String sql = "INSERT INTO Contests (contest_name, start_time, end_time, created_by_admin_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, contestName);
            pstmt.setTimestamp(2, startTime);
            pstmt.setTimestamp(3, endTime);
            pstmt.setInt(4, adminId);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<ContestSummary> getAllContestSummaries() {
        List<ContestSummary> contestSummaries = new ArrayList<>();
        String sql = "SELECT c.contest_id, c.contest_name, c.start_time, c.end_time, COUNT(cq.question_id) AS question_count " +
                "FROM Contests c LEFT JOIN Contest_Questions cq ON c.contest_id = cq.contest_id " +
                "GROUP BY c.contest_id, c.contest_name, c.start_time, c.end_time " +
                "ORDER BY c.start_time DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                contestSummaries.add(new ContestSummary(rs.getInt("contest_id"), rs.getString("contest_name"), rs.getTimestamp("start_time").toLocalDateTime().format(dtf), rs.getTimestamp("end_time").toLocalDateTime().format(dtf), rs.getInt("question_count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contestSummaries;
    }

    public static boolean deleteContestById(int contestId) {
        String sql = "DELETE FROM Contests WHERE contest_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int addContestQuestion(int contestId, String questionText, String questionType, int marks) {
        String sql = "INSERT INTO Contest_Questions (contest_id, question_text, question_type, marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, contestId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, questionType);
            pstmt.setInt(4, marks);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addContestMcqOption(int questionId, String optionText, boolean isCorrect) {
        String sql = "INSERT INTO Contest_MCQ_Options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, optionText);
            pstmt.setBoolean(3, isCorrect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static List<ContestSummary> getActiveContests() {
        List<ContestSummary> contestSummaries = new ArrayList<>();
        String sql = "SELECT c.contest_id, c.contest_name, c.start_time, c.end_time, COUNT(cq.question_id) AS question_count " +
                "FROM Contests c LEFT JOIN Contest_Questions cq ON c.contest_id = cq.contest_id " +
                "WHERE NOW() BETWEEN c.start_time AND c.end_time " +
                "GROUP BY c.contest_id, c.contest_name, c.start_time, c.end_time " +
                "ORDER BY c.end_time ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                contestSummaries.add(new ContestSummary(rs.getInt("contest_id"), rs.getString("contest_name"), rs.getTimestamp("start_time").toLocalDateTime().format(dtf), rs.getTimestamp("end_time").toLocalDateTime().format(dtf), rs.getInt("question_count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contestSummaries;
    }

    public static int startContestAttempt(int contestId, int studentId, String anonymousName) {
        String sql = "INSERT INTO Contest_Attempts (contest_id, student_id, anonymous_name, submitted_at) VALUES (?, ?, ?, NULL)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, contestId);
            pstmt.setInt(2, studentId);
            pstmt.setString(3, anonymousName);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<SystemConfig> getAllConfigs() {
        List<SystemConfig> configs = new ArrayList<>();
        String sql = "SELECT * FROM System_Config";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                configs.add(new SystemConfig(rs.getString("config_key"), rs.getString("config_value")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return configs;
    }

    public static void updateConfig(String key, String value) throws SQLException {
        String sql = "REPLACE INTO System_Config (config_key, config_value) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        }
    }

    public static String getConfigValue(String key) {
        String sql = "SELECT config_value FROM System_Config WHERE config_key = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("config_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean backupDatabase(String mysqlBinPath, String savePath) throws IOException, InterruptedException {
        String dumpExecutable = mysqlBinPath + File.separator + "mysqldump";
        ProcessBuilder pb = new ProcessBuilder(
                dumpExecutable, "-u" + DB_USER, "-p" + DB_PASSWORD,
                DB_URL.substring(DB_URL.lastIndexOf("/") + 1), "-r", savePath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        return process.waitFor() == 0;
    }

    public static boolean restoreDatabase(String mysqlBinPath, String restorePath) throws IOException, InterruptedException {
        String mysqlExecutable = mysqlBinPath + File.separator + "mysql";
        ProcessBuilder pb = new ProcessBuilder(
                mysqlExecutable, "-u" + DB_USER, "-p" + DB_PASSWORD,
                DB_URL.substring(DB_URL.lastIndexOf("/") + 1), "-e", "source " + restorePath
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        return process.waitFor() == 0;
    }



    public static Map<String, Object> getContestDetails(int contestId) {
        Map<String, Object> details = new HashMap<>();
        String sql = "SELECT contest_name, end_time FROM Contests WHERE contest_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    details.put("contest_name", rs.getString("contest_name"));
                    details.put("end_time", rs.getTimestamp("end_time"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static List<FullQuestion> getFullContestQuestions(int contestId) {
        List<FullQuestion> fullQuestions = new ArrayList<>();
        Map<Integer, Question> questionMap = new HashMap<>();
        Map<Integer, List<McqOption>> optionsMap = new HashMap<>();


        String sql = "SELECT q.question_id, q.contest_id, q.question_text, q.question_type, q.marks, " +
                "o.option_id, o.option_text, o.is_correct " +
                "FROM Contest_Questions q " +
                "LEFT JOIN Contest_MCQ_Options o ON q.question_id = o.question_id " +
                "WHERE q.contest_id = ? " +
                "ORDER BY q.question_id, o.option_id";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int qId = rs.getInt("question_id");
                    if (!questionMap.containsKey(qId)) {

                        questionMap.put(qId, new Question(
                                qId,
                                rs.getInt("contest_id"), // Store contest_id in the model's examId field
                                rs.getString("question_text"),
                                rs.getString("question_type"),
                                rs.getInt("marks")
                        ));
                        optionsMap.put(qId, new ArrayList<>());
                    }
                    int optionId = rs.getInt("option_id");
                    if (!rs.wasNull()) {
                        // Populate the generic McqOption model
                        optionsMap.get(qId).add(new McqOption(
                                optionId,
                                qId,
                                rs.getString("option_text"),
                                rs.getBoolean("is_correct")
                        ));
                    }
                }
            }

            for (int qId : questionMap.keySet()) {
                fullQuestions.add(new FullQuestion(questionMap.get(qId), optionsMap.get(qId)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullQuestions;
    }


    public static void saveContestAnswer(int contestAttemptId, int questionId, Integer selectedOptionId, String answerText) {

        String sql = "REPLACE INTO Contest_Answers (contest_attempt_id, question_id, selected_option_id, answer_text) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contestAttemptId);
            pstmt.setInt(2, questionId);

            if (selectedOptionId != null) pstmt.setInt(3, selectedOptionId);
            else pstmt.setNull(3, Types.INTEGER);

            if (answerText != null && !answerText.isEmpty()) pstmt.setString(4, answerText);
            else pstmt.setNull(4, Types.VARCHAR);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int submitAndGradeContest(int contestAttemptId) {
        int totalScore = 0;


        String gradingSql = "UPDATE Contest_Answers ca " +
                "JOIN Contest_MCQ_Options cmo ON ca.selected_option_id = cmo.option_id " +
                "JOIN Contest_Questions cq ON ca.question_id = cq.question_id " +
                "SET ca.is_correct = cmo.is_correct, " +
                "ca.marks_awarded = CASE WHEN cmo.is_correct = 1 THEN cq.marks ELSE 0 END " +
                "WHERE ca.contest_attempt_id = ? AND cq.question_type = 'MCQ'";

        String sumSql = "SELECT SUM(marks_awarded) AS total_score FROM Contest_Answers WHERE contest_attempt_id = ?";

        String finalUpdateSql = "UPDATE Contest_Attempts SET submitted_at = NOW(), final_score = ? WHERE contest_attempt_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Use transaction

            try (PreparedStatement gradeStmt = conn.prepareStatement(gradingSql)) {
                gradeStmt.setInt(1, contestAttemptId);
                gradeStmt.executeUpdate();
            }



            try (PreparedStatement sumStmt = conn.prepareStatement(sumSql)) {
                sumStmt.setInt(1, contestAttemptId);
                try (ResultSet rs = sumStmt.executeQuery()) {
                    if (rs.next()) {
                        totalScore = rs.getInt("total_score");
                    }
                }
            }

            try (PreparedStatement finalStmt = conn.prepareStatement(finalUpdateSql)) {
                finalStmt.setInt(1, totalScore);
                finalStmt.setInt(2, contestAttemptId);
                finalStmt.executeUpdate();
            }

            conn.commit(); // Commit all changes

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalScore;
    }

    public static List<LeaderboardEntry> getContestLeaderboard(int contestId) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        String sql = "SELECT anonymous_name, final_score " +
                "FROM Contest_Attempts " +
                "WHERE contest_id = ? AND submitted_at IS NOT NULL " +
                "ORDER BY final_score DESC, submitted_at ASC " +
                "LIMIT 10";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    leaderboard.add(new LeaderboardEntry(
                            rank++,
                            rs.getString("anonymous_name"),
                            rs.getInt("final_score")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
}