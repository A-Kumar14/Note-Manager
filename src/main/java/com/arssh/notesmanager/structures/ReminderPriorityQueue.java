package com.arssh.notesmanager.structures;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.Reminder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class ReminderPriorityQueue {
    private PriorityQueue<Note> queue;

    public ReminderPriorityQueue() {
        // Custom comparator: sort by reminder time (earliest first), then by priority (higher first)
        this.queue = new PriorityQueue<>(new Comparator<Note>() {
            @Override
            public int compare(Note n1, Note n2) {
                Reminder r1 = n1.getReminder();
                Reminder r2 = n2.getReminder();

                // Compare by time first (earlier time has higher priority)
                int timeComparison = r1.getTime().compareTo(r2.getTime());
                if (timeComparison != 0) {
                    return timeComparison;
                }

                // If times are equal, compare by priority (higher priority first)
                return Integer.compare(r2.getPriority(), r1.getPriority());
            }
        });
    }

    // Add note if it has an active reminder
    public void insert(Note note) {
        if (note.getReminder() != null && note.getReminder().isActive()) {
            queue.offer(note);
        }
    }

    // View next reminder without removing
    public Note peek() {
        return queue.peek();
    }

    // Remove and return next reminder
    public Note poll() {
        return queue.poll();
    }

    // Return queue size
    public int size() {
        return queue.size();
    }

    // Check if empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Get top N reminders (non-destructive)
    public List<Note> getUpcoming(int n) {
        List<Note> upcoming = new ArrayList<>();
        List<Note> temp = new ArrayList<>();

        // Extract up to n notes
        for (int i = 0; i < n && !queue.isEmpty(); i++) {
            Note note = queue.poll();
            upcoming.add(note);
            temp.add(note);
        }

        // Re-insert the notes back into the queue
        for (Note note : temp) {
            queue.offer(note);
        }

        return upcoming;
    }

    // Reconstruct queue from note list
    public void rebuild(NoteLL notes) {
        queue.clear();
        notes.forEach(note -> {
            if (note.getReminder() != null && note.getReminder().isActive()) {
                queue.offer(note);
            }
        });
    }

    // Remove specific note from queue
    public void remove(Note note) {
        queue.remove(note);
    }
}
