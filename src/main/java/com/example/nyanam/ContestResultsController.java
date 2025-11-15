package com.example.nyanam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ContestResultsController implements Initializable {

    @FXML private Label yourScoreLabel;
    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, String> rankCol;
    @FXML private TableColumn<LeaderboardEntry, String> nameCol;
    @FXML private TableColumn<LeaderboardEntry, Integer> scoreCol;
    @FXML private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("anonymousName"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
    }

    public void initData(int contestId, int finalScore, String anonymousName) {
        yourScoreLabel.setText("Your Score (" + anonymousName + "): " + finalScore);

        loadLeaderboard(contestId);
    }

    private void loadLeaderboard(int contestId) {
        try {
            List<LeaderboardEntry> entries = DatabaseConnector.getContestLeaderboard(contestId);
            ObservableList<LeaderboardEntry> observableList = FXCollections.observableArrayList(entries);
            leaderboardTable.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("StudentDashboard.fxml", "Student Dashboard", 800, 600);
    }
}