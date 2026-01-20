module com.arssh.notesmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.arssh.notesmanager to javafx.fxml, com.google.gson;
    opens com.arssh.notesmanager.persistence to com.google.gson;
    opens com.arssh.notesmanager.controllers to javafx.fxml;

    exports com.arssh.notesmanager;
    exports com.arssh.notesmanager.controllers;
    exports com.arssh.notesmanager.services;
}
