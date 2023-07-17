package com.example.alarmcenter.Models.Room;

//[alarm_center][entity][1] Entity class 파일 추가하고 item define

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notification_table")
public class NotificationEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String context;

    private String package_name;

    private Long posted_date;

    public NotificationEntity(String title, String context, String package_name, Long posted_date) {
        this.title = title;
        this.context = context;
        this.package_name = package_name;
        this.posted_date = posted_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public Long getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(Long posted_date) {
        this.posted_date = posted_date;
    }
}
