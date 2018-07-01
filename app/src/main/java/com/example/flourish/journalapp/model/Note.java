package com.example.flourish.journalapp.model;

/**
 * Created by Flourish on 28/06/2018.
 */

public class Note {
    private int id;
    private String title;
    private String note;
    private long timestamp;
    private long rTimestamp;

    public Note() {
    }

    public Note(String title, String note, long timestamp) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.timestamp = timestamp;
        this.rTimestamp = -1 * timestamp;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getNote() {
        return note;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public long getrTimestamp() { return rTimestamp; }

    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setrTimestamp(long rTimestamp) { this.rTimestamp = rTimestamp; }
}
