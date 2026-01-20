package com.arssh.notesmanager.structures;

import java.util.function.Consumer;

import com.arssh.notesmanager.Note;

public class NoteLL {
    private NoteNode head;

    public NoteLL(){
        this.head = null;
    }

    public void add(Note note){
        NoteNode newNoteNode = new NoteNode(note);
        newNoteNode.next = head;
        head = newNoteNode; 
    }

    public boolean remove(String title){
        NoteNode prev = null;
        NoteNode curr = head;

        while (curr != null){
            if (((Note)curr.note).getTitle().equalsIgnoreCase(title)) {
                if (prev == null) {
                    head = curr.next;
                }
                else {prev.next = curr.next;}
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    public Note findByTitle(String title){
        NoteNode curr = head;
        while (curr != null){
            if (((Note)curr.note).getTitle().equalsIgnoreCase(title)){
                return (Note) curr.note;
            }
            curr = curr.next;
        }
        return null;
    }

    public void forEach(Consumer<Note> action) {
        NoteNode curr = head;
        while (curr != null) {
            action.accept((Note) curr.note);
            curr = curr.next;
        }
    }

}
