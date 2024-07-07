package com.example.alarmclock_codsoft;

public class Alarm {
    private int id;  // Add this field
    private int hour;
    private int minute;
    private String ringtoneUri;
    private boolean isEnabled;

    public Alarm(int id, int hour, int minute, String ringtoneUri, boolean isEnabled) {
        this.id = id;  // Initialize the id
        this.hour = hour;
        this.minute = minute;
        this.ringtoneUri = ringtoneUri;
        this.isEnabled = isEnabled;
    }

    public int getId() {  // Getter for id
        return id;
    }

    public void setId(int id) {  // Setter for id
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
