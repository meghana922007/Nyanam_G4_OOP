package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContestLobbyController implements Initializable {

    @FXML private TableView<ContestSummary> contestTableView;
    @FXML private TableColumn<ContestSummary, String> contestNameCol;
    @FXML private TableColumn<ContestSummary, String> startTimeCol;
    @FXML private TableColumn<ContestSummary, String> endTimeCol;
    @FXML private TableColumn<ContestSummary, Integer> questionCountCol;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        contestNameCol.setCellValueFactory(new PropertyValueFactory<>("contestName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        questionCountCol.setCellValueFactory(new PropertyValueFactory<>("questionCount"));


        loadActiveContests();
    }

    private void loadActiveContests() {
        try {

            List<ContestSummary> summaryList = DatabaseConnector.getActiveContests();
            ObservableList<ContestSummary> observableList = FXCollections.observableArrayList(summaryList);
            contestTableView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading active contests.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 800, 600);
    }

    @FXML

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleJoinContest() {
        ContestSummary selectedContest = contestTableView.getSelectionModel().getSelectedItem();

        if (selectedContest == null) {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Please select a contest to join.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("CodeNinja");
        dialog.setTitle("Anonymous Login");
        dialog.setHeaderText("You are joining: " + selectedContest.contestNameProperty().get());
        dialog.setContentText("Please enter your anonymous display name:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String anonymousName = result.get().trim();

            try {

                int studentId = Main.getActiveSession().getUserId();

                int attemptId = DatabaseConnector.startContestAttempt(
                        selectedContest.getContestId(), studentId, anonymousName);

                if (attemptId == -1) {
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setText("Error creating contest attempt in DB.");
                    return;
                }


                FXMLLoader loader = new FXMLLoader(Main.class.getResource("TakeContest.fxml"));
                Parent root = loader.load();

                TakeContestController controller = loader.getController();
                controller.initData(selectedContest.getContestId(), attemptId, anonymousName);


                Scene scene = new Scene(root);
                Main.getPrimaryStage().setScene(scene);
                Main.getPrimaryStage().setTitle(selectedContest.contestNameProperty().get());
                Main.getPrimaryStage().setMaximized(true);

            } catch (NullPointerException e) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("CRITICAL ERROR: No active student session found. Please fix login system.");
                e.printStackTrace();
            } catch (Exception e) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            statusLabel.setTextFill(Color.ORANGE);
            statusLabel.setText("Contest join cancelled. You must provide an anonymous name.");
        }
    }
}