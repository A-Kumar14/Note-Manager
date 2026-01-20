package com.arssh.notesmanager;

import java.time.LocalDateTime;

//priority queues
/*
    higher priority - retrieved/executed first

    binary heap :
        max heap = largest value
        min heap = min value

        min heap implementation :
            if reminder time (due date - current date) is the lowest for a task, show that task

*/

public class Reminder {
    private LocalDateTime time;
    private int priority;
    private boolean active;

    public Reminder(LocalDateTime time, int priority){
        this.time = time;
        this.priority = priority;
        this.active = true;
    }

    public LocalDateTime getTime(){
        return time;
    }

    public int getPriority(){
        return priority;
    }

    public boolean isActive(){
        return active;
    }

    public void deactivate(){
        this.active = false;
    }

    public void activate(){
        this.active = true;
    }

    public boolean isPast(){
        return time.isBefore(LocalDateTime.now());
    }

    public void setTime(LocalDateTime time){
        this.time = time;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

}
