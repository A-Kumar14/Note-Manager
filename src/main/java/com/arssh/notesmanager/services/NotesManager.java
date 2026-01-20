package com.arssh.notesmanager.services;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.Reminder;
import com.arssh.notesmanager.persistence.JSONPersistence;
import com.arssh.notesmanager.persistence.PersistenceService;
import com.arssh.notesmanager.structures.NoteLL;
import com.arssh.notesmanager.structures.ReminderPriorityQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private static NotesManager instance;
    private NoteLL allNotes;
    private ReminderPriorityQueue activeReminders;
    private PersistenceService persistence;

    private NotesManager() {
        this.allNotes = new NoteLL();
        this.activeReminders = new ReminderPriorityQueue();
        this.persistence = new JSONPersistence();
    }

    public static NotesManager getInstance() {
        if (instance == null) {
            instance = new NotesManager();
        }
        return instance;
    }

    public void initialize() {
        loadNotes();
    }

    public void addNote(Note note) {
        allNotes.add(note);
        if (note.getReminder() != null && note.getReminder().isActive()) {
            activeReminders.insert(note);
        }
        saveNotes();
    }

    public void updateNote(String oldTitle, Note updated) {
        allNotes.remove(oldTitle);
        allNotes.add(updated);
        activeReminders.rebuild(allNotes);
        saveNotes();
    }

    public void deleteNote(String title) {
        Note note = allNotes.findByTitle(title);
        if (note != null) {
            allNotes.remove(title);
            activeReminders.remove(note);
        }
        saveNotes();
    }

    public Note getNoteByTitle(String title) {
        return allNotes.findByTitle(title);
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        allNotes.forEach(noteList::add);
        return noteList;
    }

    public List<Note> getUpcomingReminders(int count) {
        return activeReminders.getUpcoming(count);
    }

    public void setReminder(String noteTitle, Reminder reminder) {
        Note note = allNotes.findByTitle(noteTitle);
        if (note != null) {
            note.setReminder(reminder);
            activeReminders.rebuild(allNotes);
            saveNotes();
        }
    }

    public void removeReminder(String noteTitle) {
        Note note = allNotes.findByTitle(noteTitle);
        if (note != null) {
            if (note.getReminder() != null) {
                note.getReminder().deactivate();
            }
            activeReminders.rebuild(allNotes);
            saveNotes();
        }
    }

    private void saveNotes() {
        try {
            persistence.saveNotes(allNotes);
        } catch (IOException e) {
            System.err.println("Error saving notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNotes() {
        try {
            allNotes = persistence.loadNotes();
            activeReminders.rebuild(allNotes);
        } catch (IOException e) {
            System.err.println("Error loading notes: " + e.getMessage());
            e.printStackTrace();
            allNotes = new NoteLL();
        }
    }

    public void shutdown() {
        saveNotes();
    }
}
