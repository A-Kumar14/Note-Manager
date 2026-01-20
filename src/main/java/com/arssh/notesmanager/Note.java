package com.arssh.notesmanager;

public class Note {
    private String title;
    private String mainText;
    private Reminder reminder;

    public Note(String title , String mainText){
        this.title = title;
        this.mainText = mainText;
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
}