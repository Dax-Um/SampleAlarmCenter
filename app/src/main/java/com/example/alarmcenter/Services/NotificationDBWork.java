package com.example.alarmcenter.Services;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.alarmcenter.Models.Room.NotificationDao;
import com.example.alarmcenter.Models.Room.NotificationDatabase;
import com.example.alarmcenter.Models.Room.NotificationEntity;

public class NotificationDBWork extends Worker {
    private String action_type = "select";
    private int noti_id;
    private String noti_title, noti_context, noti_package;
    private Long noti_posted_date;

    private NotificationDao notificationDao;
    private NotificationEntity notificationEntity;

    public NotificationDBWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        NotificationDatabase database = NotificationDatabase.getInstance(context);
        notificationDao = database.notificationDao();

        action_type = getInputData().getString("ACTION_TYPE");
        noti_id = getInputData().getInt("NOTI_ID", 0);
        noti_title = getInputData().getString("NOTI_TITLE");
        noti_context = getInputData().getString("NOTI_CONTEXT");
        noti_package = getInputData().getString("NOTI_PACKAGE");
        noti_posted_date = getInputData().getLong("NOTI_POSTEDDATE", 0L);

        Log.d("MVVM_TAG", "Work db action type : " + action_type);
    }

    @NonNull
    @Override
    public Result doWork() {
        switch (action_type) {
            case "insert":
                notificationEntity = new NotificationEntity(noti_title, noti_context, noti_package, noti_posted_date);
                notificationEntity.setId(noti_id);
                notificationDao.insert(notificationEntity);
            case "delete":
                notificationEntity = new NotificationEntity(noti_title, noti_context, noti_package, noti_posted_date);
                notificationEntity.setId(noti_id);
                notificationDao.delete(notificationEntity);
            default:
                break;
        }
        return Result.success();
    }
}
