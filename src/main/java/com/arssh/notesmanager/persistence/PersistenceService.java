package com.arssh.notesmanager.persistence;

import com.arssh.notesmanager.structures.NoteLL;
import java.io.IOException;

public interface PersistenceService {
    void saveNotes(NoteLL notes) throws IOException;
    NoteLL loadNotes() throws IOException;
    boolean dataFileExists();
}
