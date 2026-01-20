package com.arssh.notesmanager;

import java.util.ArrayList;
import java.util.List;

public class Note {
    private String title;
    private String mainText;
    private Reminder reminder;
    private String aiSummary;
    private List<String> tags;

    public Note(String title , String mainText){
        this.title = title;
        this.mainText = mainText;
        this.tags = new ArrayList<>();
    }

    public String getTitle(){
        return title;
    }

    public String getMainText(){
        return mainText;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setMainText(String mainText){
        this.mainText = mainText;
    }

    public void setReminder(Reminder reminder){
        this.reminder = reminder;
    }

    public Reminder getReminder(){
        return reminder;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
        }
    }
}