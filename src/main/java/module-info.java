module com.arssh.notesmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires service; // OpenAI library (automatic module name)
    requires okhttp3; // OkHttp dependency

    opens com.arssh.notesmanager to javafx.fxml, com.google.gson;
    opens com.arssh.notesmanager.persistence to com.google.gson;
    opens com.arssh.notesmanager.controllers to javafx.fxml;
    opens com.arssh.notesmanager.ai to com.google.gson;

    exports com.arssh.notesmanager;
    exports com.arssh.notesmanager.controllers;
    exports com.arssh.notesmanager.services;
    exports com.arssh.notesmanager.ai;
}
