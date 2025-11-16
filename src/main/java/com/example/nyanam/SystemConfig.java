package com.example.nyanam;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class SystemConfig {

    private final StringProperty configKey;
    private final StringProperty configValue;

    public SystemConfig(String key, String value) {
        this.configKey = new SimpleStringProperty(key);
        this.configValue = new SimpleStringProperty(value);
    }


    public String getConfigKey() { return configKey.get(); }
    public void setConfigValue(String value) { this.configValue.set(value); }


    public StringProperty configKeyProperty() { return configKey; }
    public StringProperty configValueProperty() { return configValue; }
}