package com.example.alarmcenter.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.alarmcenter.Models.Room.NotificationEntity;
import com.example.alarmcenter.Models.NotificationRepository;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private NotificationRepository repository;
    private LiveData<List<NotificationEntity>> allNotifications;

    public NotificationViewModel(@NonNull Application application) {
        super(application);

        repository = new NotificationRepository(application);
        allNotifications = repository.getAllNotifications();
    }

    public void insert(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        Log.d("MVVM_TAG", "[ViewModel] - insert()");
        repository.insert(notification, lifecycleOwner);
    }

    public void update(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        Log.d("MVVM_TAG", "[ViewModel] - update()");
        repository.update(notification, lifecycleOwner);
    }

    public void delete(NotificationEntity notification, LifecycleOwner lifecycleOwner) {
        Log.d("MVVM_TAG", "[ViewModel] - delete()");
        repository.delete(notification, lifecycleOwner);
    }

    public void deleteAllNotes(LifecycleOwner lifecycleOwner) {
        Log.d("MVVM_TAG", "[ViewModel] - deleteAllNotes()");
        repository.deleteAllNotification(lifecycleOwner);
    }

    public LiveData<List<NotificationEntity>> getAllNotes() {
        Log.d("MVVM_TAG", "[ViewModel] - getAllNotes()");
        return allNotifications;
    }
}
