package com.example.alarmcenter.Models.Room;

//[alarm_center][Dao][1] interface class 파일 추가하고 Database query interface define

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    void insert(NotificationEntity notification);

    @Update
    void update(NotificationEntity notification);

    @Delete
    void delete(NotificationEntity notification);

    @Query("DELETE FROM notification_table")
    void deleteAllNotifications();

    @Query("SELECT * FROM notification_table ORDER BY posted_date DESC")
    LiveData<List<NotificationEntity>> getAllNotifications();
}
