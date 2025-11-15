package com.example.nyanam;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A data model for the ManageConfig.fxml TableView.
 * Holds one key-value pair.
 */
public class SystemConfig {

    private final StringProperty configKey;
    private final StringProperty configValue;

    public SystemConfig(String key, String value) {
        this.configKey = new SimpleStringProperty(key);
        this.configValue = new SimpleStringProperty(value);
    }

    // --- Getters and Setters ---
    public String getConfigKey() { return configKey.get(); }
    public void setConfigValue(String value) { this.configValue.set(value); }

    // --- JavaFX Property Getters for TableView ---
    public StringProperty configKeyProperty() { return configKey; }
    public StringProperty configValueProperty() { return configValue; }
}