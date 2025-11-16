package com.example.nyanam;

import javafx.beans.property.*;

public class Resource {
    private IntegerProperty resourceId = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty topic = new SimpleStringProperty();
    private StringProperty subtopic = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private StringProperty filePath = new SimpleStringProperty();

    public IntegerProperty getResourceIdProperty() { return resourceId; }
    public StringProperty getTitleProperty() { return title; }
    public StringProperty getTopicProperty() { return topic; }
    public StringProperty getSubtopicProperty() { return subtopic; }
    public StringProperty getTypeProperty() { return type; }
    public StringProperty getFilePathProperty() { return filePath; }


    public void setResourceId(int id) { this.resourceId.set(id); }
    public void setTitle(String t) { this.title.set(t); }
    public void setTopic(String t) { this.topic.set(t); }
    public void setSubtopic(String s) { this.subtopic.set(s); }
    public void setType(String t) { this.type.set(t); }
    public void setFilePath(String f) { this.filePath.set(f); }
}
