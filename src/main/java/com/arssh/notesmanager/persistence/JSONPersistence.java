package com.arssh.notesmanager.persistence;

import com.arssh.notesmanager.Note;
import com.arssh.notesmanager.structures.NoteLL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JSONPersistence implements PersistenceService {
    private final String dataFilePath;
    private final Gson gson;

    public JSONPersistence() {
        // Save location: user home directory/.notesmanager/notes.json
        String userHome = System.getProperty("user.home");
        this.dataFilePath = userHome + File.separator + ".notesmanager" + File.separator + "notes.json";

        // Configure Gson with LocalDateTime adapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void saveNotes(NoteLL notes) throws IOException {
        // Create parent directories if they don't exist
        File dataFile = new File(dataFilePath);
        File parentDir = dataFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Convert NoteLL to List<Note>
        List<Note> noteList = new ArrayList<>();
        notes.forEach(noteList::add);

        // Write to file
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(noteList, writer);
        }
    }

    @Override
    public NoteLL loadNotes() throws IOException {
        File dataFile = new File(dataFilePath);

        // If file doesn't exist, return empty NoteLL
        if (!dataFile.exists()) {
            return new NoteLL();
        }

        // Read from file
        try (FileReader reader = new FileReader(dataFile)) {
            Type listType = new TypeToken<ArrayList<Note>>(){}.getType();
            List<Note> noteList = gson.fromJson(reader, listType);

            // Create NoteLL and add each note
            NoteLL noteLL = new NoteLL();
            if (noteList != null) {
                for (Note note : noteList) {
                    noteLL.add(note);
                }
            }

            return noteLL;
        }
    }

    @Override
    public boolean dataFileExists() {
        File dataFile = new File(dataFilePath);
        return dataFile.exists();
    }
}
