package com.example.alarmcenter.Services;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.alarmcenter.Models.Room.NotificationEntity;

public class NotificationListenerService extends android.service.notification.NotificationListenerService {
    public NotificationListenerService() {
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        super.onNotificationPosted(sbn);

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        int sbn_id = sbn.getId();
        String sbn_key = sbn.getKey();
        Long sbn_postTime = sbn.getPostTime();
        String package_name = sbn.getPackageName();

        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getString(Notification.EXTRA_TEXT);
        String big_text = extras.getString(Notification.EXTRA_BIG_TEXT);
        String info_text = extras.getString(Notification.EXTRA_INFO_TEXT);
        String subText = extras.getString(Notification.EXTRA_SUB_TEXT);
        String summaryText = extras.getString(Notification.EXTRA_SUMMARY_TEXT);

        //[Noti_Listener][3]  모든 app의 StatusBarNotification(sbn) 값 받아서 출력
        Log.d(
                "MVVM_TAG", "onNotificationPosted:\n" +
                        "PackageName : " + package_name + "\n" +
                        "ID : " + sbn_id + "\n" +
                        "Key : " + sbn_key + "\n" +
                        "postTime : " + sbn_postTime + "\n" +
                        "Title : " + title + "\n" +
                        "Text : " + text + "\n" +
                        "InfoText : " + info_text + "\n" +
                        "BigText : " + big_text + "\n" +
                        "SubText : " + subText + "\n" +
                        "SummaryText : " + summaryText + "\n"
        );

        //[Noti_Listener][4]  sbn중 title이 존재하는 정보만 object 생성해서 전달하도록 intent 구현
        if(title != null) {
            NotificationEntity notificationEntity = new NotificationEntity(title, text, package_name, sbn_postTime);
            Intent intent1 = new Intent("com.example.alarmcenter");
            intent1.putExtra("notification_listener_event", notificationEntity);
            sendBroadcast(intent1);
        }
    }
}