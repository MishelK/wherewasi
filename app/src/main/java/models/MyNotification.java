package models;

import java.util.Date;

public class MyNotification {

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    int days;
    String text;

    public MyNotification(int days, String text) {
        this.days = days;
        this.text = text;
    }
}
