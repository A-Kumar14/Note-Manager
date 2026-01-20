package com.arssh.notesmanager.structures;

import com.arssh.notesmanager.Note;

public class NoteNode {
    public Note note;
    public NoteNode next;

    public NoteNode(Note note){
        this.note = note;
        this.next = null;
    }

    public Note getNote(NoteNode curr){
        return this.note;
    }
}
