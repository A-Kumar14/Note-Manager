package com.arssh.notesmanager.controllers;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.Reminder;
import com.arssh.notesmanager.ai.AIConfig;
import com.arssh.notesmanager.ai.AIService;
import com.arssh.notesmanager.services.NotesManager;
import javafx.application.Platform;
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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    @FXML private Button summarizeButton;
    @FXML private Button generateTagsButton;
    @FXML private Button extractTasksButton;
    @FXML private TextField tagsField;
    @FXML private TextArea summaryArea;

    private Note editingNote;
    private Stage dialogStage;
    private boolean saved = false;
    private AIService aiService;

    @FXML
    public void initialize() {
        // Setup AI service
        aiService = AIConfig.getInstance().getAIService();

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

        // Check if AI service is available
        if (!AIConfig.getInstance().isConfigured()) {
            if (summarizeButton != null) summarizeButton.setDisable(true);
            if (generateTagsButton != null) generateTagsButton.setDisable(true);
            if (extractTasksButton != null) extractTasksButton.setDisable(true);
        }
    }

    public void setNote(Note note) {
        this.editingNote = note;

        // Populate fields
        titleField.setText(note.getTitle());
        textArea.setText(note.getMainText());

        // Populate tags
        if (note.getTags() != null && !note.getTags().isEmpty() && tagsField != null) {
            tagsField.setText(String.join(", ", note.getTags()));
        }

        // Populate summary
        if (note.getAiSummary() != null && !note.getAiSummary().isEmpty() && summaryArea != null) {
            summaryArea.setText(note.getAiSummary());
        }

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

        // Add tags
        if (tagsField != null && !tagsField.getText().trim().isEmpty()) {
            String[] tags = tagsField.getText().split(",");
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty()) {
                    note.addTag(trimmedTag);
                }
            }
        }

        // Add summary
        if (summaryArea != null && !summaryArea.getText().trim().isEmpty()) {
            note.setAiSummary(summaryArea.getText().trim());
        }

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

    @FXML
    private void onSummarize() {
        if (!checkAIAvailable()) return;

        String content = textArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showInfo("No Content", "Please enter some content to summarize.");
            return;
        }

        // Disable button during processing
        summarizeButton.setDisable(true);
        summarizeButton.setText("Generating...");

        // Run AI operation in background thread
        new Thread(() -> {
            String summary = aiService.generateSummary(content);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                if (summaryArea != null) {
                    summaryArea.setText(summary);
                }
                summarizeButton.setDisable(false);
                summarizeButton.setText("Summarize");
            });
        }).start();
    }

    @FXML
    private void onGenerateTags() {
        if (!checkAIAvailable()) return;

        String content = textArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showInfo("No Content", "Please enter some content to generate tags.");
            return;
        }

        // Disable button during processing
        generateTagsButton.setDisable(true);
        generateTagsButton.setText("Generating...");

        // Run AI operation in background thread
        new Thread(() -> {
            List<String> tags = aiService.generateTags(content);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                if (tagsField != null && !tags.isEmpty()) {
                    tagsField.setText(String.join(", ", tags));
                }
                generateTagsButton.setDisable(false);
                generateTagsButton.setText("Generate Tags");
            });
        }).start();
    }

    @FXML
    private void onExtractTasks() {
        if (!checkAIAvailable()) return;

        String content = textArea.getText();
        if (content == null || content.trim().isEmpty()) {
            showInfo("No Content", "Please enter some content to extract tasks.");
            return;
        }

        // Disable button during processing
        extractTasksButton.setDisable(true);
        extractTasksButton.setText("Extracting...");

        // Run AI operation in background thread
        new Thread(() -> {
            List<String> tasks = aiService.extractTasks(content);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                if (tasks.isEmpty()) {
                    showInfo("No Tasks Found", "No action items were detected in the content.");
                } else {
                    StringBuilder taskList = new StringBuilder("Extracted Tasks:\n\n");
                    for (int i = 0; i < tasks.size(); i++) {
                        taskList.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
                    }
                    showInfo("Tasks Extracted", taskList.toString());
                }
                extractTasksButton.setDisable(false);
                extractTasksButton.setText("Extract Tasks");
            });
        }).start();
    }

    @FXML
    private void onConfigureAI() {
        TextInputDialog dialog = new TextInputDialog(AIConfig.getInstance().getApiKey());
        dialog.setTitle("Configure AI");
        dialog.setHeaderText("OpenAI API Configuration");
        dialog.setContentText("Enter your OpenAI API Key:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(apiKey -> {
            if (!apiKey.trim().isEmpty()) {
                AIConfig.getInstance().setApiKey(apiKey.trim());
                aiService = AIConfig.getInstance().getAIService();

                if (aiService.isAvailable()) {
                    if (summarizeButton != null) summarizeButton.setDisable(false);
                    if (generateTagsButton != null) generateTagsButton.setDisable(false);
                    if (extractTasksButton != null) extractTasksButton.setDisable(false);
                    showInfo("Success", "AI service configured successfully!");
                } else {
                    showError("Configuration Failed", "Failed to initialize AI service. Please check your API key.");
                }
            }
        });
    }

    private boolean checkAIAvailable() {
        if (!AIConfig.getInstance().isConfigured() || !aiService.isAvailable()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("AI Not Configured");
            alert.setHeaderText("OpenAI API key not configured");
            alert.setContentText("Would you like to configure your API key now?");

            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                onConfigureAI();
                return aiService.isAvailable();
            }
            return false;
        }
        return true;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
