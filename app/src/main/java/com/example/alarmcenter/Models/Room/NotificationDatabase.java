package com.example.alarmcenter.Models.Room;

//[alarm_center][Database][1] Database class 파일 추가하고 Database create instance

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {NotificationEntity.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {


    private static NotificationDatabase db_instance;
    public abstract NotificationDao notificationDao();

    public static synchronized NotificationDatabase getInstance(Context context) {
        if (db_instance == null){
            Log.d("MVVM_TAG", "[Database] - NotificationDatabase() - db_instance == null - Room.databaseBuilder");
            db_instance = Room.databaseBuilder(context.getApplicationContext(),
                            NotificationDatabase.class, "notification_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return db_instance;
    }

    //[alarm_center][Database][2] Database 최초 생성될때 수행되는 callback을 등록하여 DB 초기값들 처리한다 여기선 기본3개 item 추가한다
    //Callback에서 item 3개 추가로 room사용이므로 thread로 처리해야함 여기선 AsyncTask로 처리함.

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Log.d("MVVM_TAG", "[Database] - RoomDatabase.Callback");
            super.onCreate(db);

            new CreateDbAsyncTask(db_instance).execute();
        }
    };

    private static class CreateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NotificationDao notificationDao;

        public CreateDbAsyncTask(NotificationDatabase db) {
            this.notificationDao = db.notificationDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notificationDao.insert(new NotificationEntity("Title 1", "Context 1", "Package 1", 1L));
            notificationDao.insert(new NotificationEntity("Title 2", "Context 2", "Package 2", 2L));
            notificationDao.insert(new NotificationEntity("Title 3", "Context 3", "Package 3", 3L));
            return null;
        }
    }

}
