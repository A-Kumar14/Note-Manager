module com.arssh.notesmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.theokanning.openai.gpt3.java.service;

    opens com.arssh.notesmanager to javafx.fxml, com.google.gson;
    opens com.arssh.notesmanager.persistence to com.google.gson;
    opens com.arssh.notesmanager.controllers to javafx.fxml;
    opens com.arssh.notesmanager.ai to com.google.gson;

    exports com.arssh.notesmanager;
    exports com.arssh.notesmanager.controllers;
    exports com.arssh.notesmanager.services;
    exports com.arssh.notesmanager.ai;
}
