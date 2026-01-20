package com.arssh.notesmanager.controllers;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.Reminder;
import com.arssh.notesmanager.services.NotesManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NoteEditorController {
    @FXML private TextField titleField;
    @FXML private TextArea textArea;
    @FXML private CheckBox reminderCheckBox;
    @FXML private DatePicker reminderDatePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private Spinner<Integer> prioritySpinner;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label priorityLabel;

    private Note editingNote;
    private Stage dialogStage;
    private boolean saved = false;

    @FXML
    public void initialize() {
        // Setup spinners
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        prioritySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3));

        // Bind reminder controls to checkbox
        reminderDatePicker.setDisable(true);
        hourSpinner.setDisable(true);
        minuteSpinner.setDisable(true);
        prioritySpinner.setDisable(true);
        dateLabel.setDisable(true);
        timeLabel.setDisable(true);
        priorityLabel.setDisable(true);

        reminderCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            reminderDatePicker.setDisable(!newVal);
            hourSpinner.setDisable(!newVal);
            minuteSpinner.setDisable(!newVal);
            prioritySpinner.setDisable(!newVal);
            dateLabel.setDisable(!newVal);
            timeLabel.setDisable(!newVal);
            priorityLabel.setDisable(!newVal);
        });
    }

    public void setNote(Note note) {
        this.editingNote = note;

        // Populate fields
        titleField.setText(note.getTitle());
        textArea.setText(note.getMainText());

        // Populate reminder fields if note has a reminder
        if (note.getReminder() != null && note.getReminder().isActive()) {
            reminderCheckBox.setSelected(true);

            LocalDateTime reminderTime = note.getReminder().getTime();
            reminderDatePicker.setValue(reminderTime.toLocalDate());
            hourSpinner.getValueFactory().setValue(reminderTime.getHour());
            minuteSpinner.getValueFactory().setValue(reminderTime.getMinute());
            prioritySpinner.getValueFactory().setValue(note.getReminder().getPriority());
        }
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    private void onSave() {
        if (!validateInput()) {
            return;
        }

        String title = titleField.getText().trim();
        Note newNote = buildNoteFromForm();

        if (editingNote == null) {
            // Creating new note
            NotesManager.getInstance().addNote(newNote);
        } else {
            // Updating existing note
            NotesManager.getInstance().updateNote(editingNote.getTitle(), newNote);
        }

        saved = true;
        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        saved = false;
        dialogStage.close();
    }

    private boolean validateInput() {
        String title = titleField.getText().trim();

        if (title.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Title cannot be empty.");
            alert.showAndWait();
            return false;
        }

        if (reminderCheckBox.isSelected()) {
            if (reminderDatePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please select a date for the reminder.");
                alert.showAndWait();
                return false;
            }
        }

        return true;
    }

    private Note buildNoteFromForm() {
        String title = titleField.getText().trim();
        String content = textArea.getText();

        Note note = new Note(title, content);

        if (reminderCheckBox.isSelected()) {
            Reminder reminder = buildReminderFromForm();
            note.setReminder(reminder);
        }

        return note;
    }

    private Reminder buildReminderFromForm() {
        LocalDate date = reminderDatePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        int priority = prioritySpinner.getValue();

        LocalDateTime reminderTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

        return new Reminder(reminderTime, priority);
    }
}
