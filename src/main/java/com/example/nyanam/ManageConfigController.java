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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageConfigController implements Initializable {

    @FXML private TableView<SystemConfig> configTable;
    @FXML private TableColumn<SystemConfig, String> configKeyCol;
    @FXML private TableColumn<SystemConfig, String> configValueCol;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private ObservableList<SystemConfig> configList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        configKeyCol.setCellValueFactory(new PropertyValueFactory<>("configKey"));
        configValueCol.setCellValueFactory(new PropertyValueFactory<>("configValue"));
        configTable.setItems(configList);

        configValueCol.setCellFactory(TextFieldTableCell.forTableColumn());


        configValueCol.setOnEditCommit(event -> {
            SystemConfig config = event.getRowValue();
            String newValue = event.getNewValue().trim();

            try {

                DatabaseConnector.updateConfig(config.getConfigKey(), newValue);


                config.setConfigValue(newValue);

                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Setting '" + config.getConfigKey() + "' updated successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Error updating setting: " + e.getMessage());

                configTable.refresh();
            }
        });


        loadConfigs();
    }

    private void loadConfigs() {
        try {
            List<SystemConfig> configs = DatabaseConnector.getAllConfigs();
            configList.setAll(configs);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading system settings.");
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Main.changeScene("AdminDashboard.fxml", "Admin Dashboard", 800, 600);
    }
}