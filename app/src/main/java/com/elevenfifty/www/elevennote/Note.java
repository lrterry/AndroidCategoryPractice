package com.elevenfifty.www.elevennote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by bkeck on 3/9/15.
 */
public class Note implements Comparable<Note> {
    @SerializedName("title")
    private String title;
    @SerializedName("text")
    private String text;
    @SerializedName("date")
    private Date date;
    @SerializedName("categoryID")
    private int categoryID;

    public Note(String title, String text, Date date, int categoryID) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.categoryID = categoryID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Note another) {
//        return this.date.compareTo(another.getDate()); // newest at bottom
        return another.getDate().compareTo(this.date); // newest at top
    }
}
