package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GradeExamsController implements Initializable {

    @FXML private TableView<PendingAttempt> pendingTableView;
    @FXML private TableColumn<PendingAttempt, String> studentNameCol;
    @FXML private TableColumn<PendingAttempt, String> examNameCol;
    @FXML private TableColumn<PendingAttempt, String> submittedAtCol;
    @FXML private Button backButton;
    @FXML private Button gradeButton;
    @FXML private Label statusLabel;

    private ObservableList<PendingAttempt> pendingList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        examNameCol.setCellValueFactory(new PropertyValueFactory<>("examName"));
        submittedAtCol.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));

        pendingTableView.setItems(pendingList);
        loadPendingAttempts();
    }

    private void loadPendingAttempts() {
        try {
            List<PendingAttempt> attempts = DatabaseConnector.getAttemptsPendingReview();
            pendingList.setAll(attempts);
            statusLabel.setText(attempts.size() + " attempts pending review.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading attempts.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("TeachersDashboard.fxml", "Teacher Dashboard", 800, 600);
    }

    @FXML
    private void handleGrade() {
        PendingAttempt selected = pendingTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an attempt to grade.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("GradeAttempt.fxml"));
            Parent root = loader.load();

            GradeAttemptController controller = loader.getController();
            controller.initData(selected.getAttemptId(),selected.studentNameProperty().get(),selected.examNameProperty().get());

            Stage stage = new Stage();
            stage.setTitle("Grade: " + selected.studentNameProperty().get() + " - " + selected.examNameProperty().get());
            stage.setScene(new Scene(root, 900, 700));
            stage.initModality(Modality.APPLICATION_MODAL);


            stage.setOnHidden(e -> loadPendingAttempts());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not open grading window.");
        }
    }
}