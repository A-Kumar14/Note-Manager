package com.arssh.notesmanager.controllers;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.Reminder;
import com.arssh.notesmanager.services.NotesManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML private ListView<Note> notesListView;
    @FXML private ListView<String> remindersListView;
    @FXML private Button newNoteButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ObservableList<Note> notesList;
    private ObservableList<String> remindersList;

    @FXML
    public void initialize() {
        // Initialize observable lists
        notesList = FXCollections.observableArrayList();
        remindersList = FXCollections.observableArrayList();

        notesListView.setItems(notesList);
        remindersListView.setItems(remindersList);

        // Setup custom cell factory for notes
        notesListView.setCellFactory(lv -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Format: Bold title, 2-line content preview, clock icon if has reminder
                    String title = note.getTitle();
                    String content = note.getMainText();
                    String preview = content.length() > 100 ? content.substring(0, 100) + "..." : content;

                    String hasReminder = (note.getReminder() != null && note.getReminder().isActive()) ? " \uD83D\uDD54" : "";

                    Text titleText = new Text(title + hasReminder + "\n");
                    titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                    Text contentText = new Text(preview);
                    contentText.setStyle("-fx-fill: gray; -fx-font-size: 12;");

                    TextFlow textFlow = new TextFlow(titleText, contentText);
                    setGraphic(textFlow);
                }
            }
        });

        // Enable/disable buttons based on selection
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        // Load notes
        refreshNotesList();
        refreshRemindersList();
    }

    @FXML
    private void onNewNote() {
        openNoteEditor(null);
    }

    @FXML
    private void onEditNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            openNoteEditor(selectedNote);
        }
    }

    @FXML
    private void onDeleteNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Note");
            alert.setHeaderText("Delete \"" + selectedNote.getTitle() + "\"?");
            alert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                NotesManager.getInstance().deleteNote(selectedNote.getTitle());
                refreshNotesList();
                refreshRemindersList();
            }
        }
    }

    public void refreshNotesList() {
        notesList.clear();
        List<Note> notes = NotesManager.getInstance().getAllNotes();
        notesList.addAll(notes);
    }

    public void refreshRemindersList() {
        remindersList.clear();
        List<Note> reminders = NotesManager.getInstance().getUpcomingReminders(50);
        for (Note note : reminders) {
            remindersList.add(formatReminder(note));
        }
    }

    private String formatReminder(Note note) {
        if (note.getReminder() == null) {
            return note.getTitle();
        }

        Reminder reminder = note.getReminder();
        LocalDateTime reminderTime = reminder.getTime();

        // Format absolute time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a");
        String absoluteTime = reminderTime.format(formatter);

        // Calculate relative time
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, reminderTime);

        String relativeTime;
        if (reminder.isPast()) {
            // Past reminder
            duration = Duration.between(reminderTime, now);
            long hours = duration.toHours();
            long days = duration.toDays();

            if (days > 0) {
                relativeTime = days + " day" + (days > 1 ? "s" : "") + " ago";
            } else if (hours > 0) {
                relativeTime = hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else {
                long minutes = duration.toMinutes();
                relativeTime = minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            }

            return "OVERDUE: " + note.getTitle() + " - Was " + absoluteTime;
        } else {
            // Future reminder
            long hours = duration.toHours();
            long days = duration.toDays();

            if (days > 0) {
                relativeTime = "In " + days + " day" + (days > 1 ? "s" : "");
            } else if (hours > 0) {
                relativeTime = "In " + hours + " hour" + (hours > 1 ? "s" : "");
            } else {
                long minutes = duration.toMinutes();
                relativeTime = "In " + minutes + " minute" + (minutes > 1 ? "s" : "");
            }

            return note.getTitle() + " - " + relativeTime + " (" + absoluteTime + ")";
        }
    }

    private void openNoteEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/arssh/notesmanager/noteEditor.fxml"));
            Parent root = loader.load();

            NoteEditorController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(note == null ? "New Note" : "Edit Note");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(notesListView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            controller.setDialogStage(dialogStage);
            if (note != null) {
                controller.setNote(note);
            }

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                refreshNotesList();
                refreshRemindersList();
            }
        } catch (IOException e) {
            showError("Could not open note editor", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
