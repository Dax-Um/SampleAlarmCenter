package com.example.alarmcenter.Models;

//[alarm_center][Repository][1]  class 파일 추가하고 Database Room제어하는 method들 추가
//room control은 thread를 이용해야함 여기선 AsyncTask로 동작하도록 구현

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.alarmcenter.Models.Room.NotificationDao;
import com.example.alarmcenter.Models.Room.NotificationDatabase;
import com.example.alarmcenter.Models.Room.NotificationEntity;
import com.example.alarmcenter.Services.NotificationDBWork;

import java.util.List;

public class NotificationRepository {
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String NOTI_ID = "NOTI_ID";
    public static final String NOTI_TITLE = "NOTI_TITLE";
    public static final String NOTI_CONTEXT = "NOTI_CONTEXT";
    public static final String NOTI_PACKAGE = "NOTI_PACKAGE";
    public static final String NOTI_POSTEDDATE = "NOTI_POSTEDDATE";

    private NotificationDao notificationDao;
    private LiveData<List<NotificationEntity>> allNotifications;

    Application mApplication;

    public NotificationRepository(Application application) {

        mApplication = application;
        NotificationDatabase database = NotificationDatabase.getInstance(application);
        notificationDao = database.notificationDao();
        allNotifications = notificationDao.getAllNotifications();
    }

    public void insert(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        OneTimeWorkRequest insertRequest = new OneTimeWorkRequest.Builder(NotificationDBWork.class)
                .setInputData(createInputData("insert", notification))
                .build();
        WorkManager workManager = WorkManager.getInstance(mApplication);
        workManager.getWorkInfoByIdLiveData(insertRequest.getId()).observe(lifecycleOwner, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState().isFinished()){
                    Toast.makeText(mApplication, "Work insert OK!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        workManager.enqueue(insertRequest);

//        new InsertNotificationAsyncTask(notificationDao).execute(notification);
    }

    public void update(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        new UpdateNotificationAsyncTask(notificationDao).execute(notification);
    }

    public void delete(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        OneTimeWorkRequest deleteRequest = new OneTimeWorkRequest.Builder(NotificationDBWork.class)
                .setInputData(createInputData("delete", notification))
                .build();
        WorkManager workManager = WorkManager.getInstance(mApplication);
        workManager.getWorkInfoByIdLiveData(deleteRequest.getId()).observe(lifecycleOwner, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState().isFinished()){
                    Toast.makeText(mApplication, "Work delete OK!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        workManager.enqueue(deleteRequest);
//        new DeleteNotificationAsyncTask(notificationDao).execute(notification);
    }

    public void deleteAllNotification(LifecycleOwner lifecycleOwner) {
        new DeleteAllNotificationAsyncTask(notificationDao).execute();
    }

    public LiveData<List<NotificationEntity>> getAllNotifications() {
        return allNotifications;
    }


    private static class InsertNotificationAsyncTask extends AsyncTask<NotificationEntity, Void, Void> {
        private NotificationDao notificationDao;

        public InsertNotificationAsyncTask(NotificationDao notificationDao) {
            this.notificationDao = notificationDao;
        }

        @Override
        protected Void doInBackground(NotificationEntity... notificationEntities) {
            notificationDao.insert(notificationEntities[0]);
            return null;
        }
    }

    private static class UpdateNotificationAsyncTask extends AsyncTask<NotificationEntity, Void, Void> {
        private NotificationDao notificationDao;

        public UpdateNotificationAsyncTask(NotificationDao notificationDao) {
            this.notificationDao = notificationDao;
        }

        @Override
        protected Void doInBackground(NotificationEntity... notificationEntities) {
            notificationDao.update(notificationEntities[0]);
            return null;
        }
    }

    private static class DeleteNotificationAsyncTask extends AsyncTask<NotificationEntity, Void, Void> {
        private NotificationDao notificationDao;

        public DeleteNotificationAsyncTask(NotificationDao notificationDao) {
            this.notificationDao = notificationDao;
        }

        @Override
        protected Void doInBackground(NotificationEntity... notificationEntities) {
            notificationDao.delete(notificationEntities[0]);
            return null;
        }
    }

    private static class DeleteAllNotificationAsyncTask extends AsyncTask<Void, Void, Void> {
        private NotificationDao notificationDao;

        public DeleteAllNotificationAsyncTask(NotificationDao notificationDao) {
            this.notificationDao = notificationDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notificationDao.deleteAllNotifications();
            return null;
        }
    }

    private Data createInputData(String action_type, NotificationEntity notification){

        Data.Builder builder = new Data.Builder();
        builder.putString(ACTION_TYPE, action_type);
        builder.putInt(NOTI_ID, notification.getId());
        builder.putString(NOTI_TITLE, notification.getTitle());
        builder.putString(NOTI_CONTEXT, notification.getContext());
        builder.putString(NOTI_PACKAGE, notification.getPackage_name());
        builder.putLong(NOTI_POSTEDDATE, notification.getPosted_date());

        return builder.build();
    }
}
