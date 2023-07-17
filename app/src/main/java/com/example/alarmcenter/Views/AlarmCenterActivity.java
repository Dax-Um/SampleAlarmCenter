package com.example.alarmcenter.Views;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmcenter.Models.Room.NotificationEntity;
import com.example.alarmcenter.ViewModels.NotificationViewModel;
import com.example.alarmcenter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Set;

public class AlarmCenterActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;
    private static final int VIEW_NOTE_REQUEST = 3;
    private NotificationViewModel notificationViewModel;
    NotificationBroadcaseReceiver receiver;
    private String channelID = "test_Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MVVM_TAG", "[MainActivity] - onCreate() ");

        //Notification 권한 check
        if (!isNotiPermissionAllowed()) {
            Intent setting_intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(setting_intent);
        }

        //RecylerView 처리
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NotificationAdapter adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);

        notificationViewModel = new ViewModelProvider
                .AndroidViewModelFactory(this.getApplication())
                .create(NotificationViewModel.class);
        notificationViewModel.getAllNotes().observe(this, new Observer<List<NotificationEntity>>() {
            @Override
            public void onChanged(List<NotificationEntity> notes) {
                Log.d("MVVM_TAG", "[MainActivity] - noteViewModel.getAllNotes().observe() - onChanged() ");
                adapter.setNotificationEntities(notes);
            }
        });

        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotificationEntity notification) {
                Log.d("MVVM_TAG", "[MainActivity] - adapter.setOnItemClickListener 인터페이스 onItemClick() ");
                Intent intent = new Intent(AlarmCenterActivity.this, NotificationViewer.class);
                intent.putExtra(NotificationViewer.EXTRA_ID, notification.getId());
                intent.putExtra(NotificationViewer.EXTRA_TITLE, notification.getTitle());
                intent.putExtra(NotificationViewer.EXTRA_CONTEXT, notification.getContext());
                intent.putExtra(NotificationViewer.EXTRA_POSTEDDATE, notification.getPosted_date());
                intent.putExtra(NotificationViewer.EXTRA_PACKAGE, notification.getPackage_name());
                startActivityForResult(intent, VIEW_NOTE_REQUEST);
            }
        });


        //Recycler view에서 좌우 drag로 삭제 처리
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                notificationViewModel.delete(adapter.getNotificationAt(viewHolder.getAdapterPosition()), AlarmCenterActivity.this);
                Toast.makeText(AlarmCenterActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerView);

        //Toolbar init
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar); //액티비티의 앱바(App Bar)로 지정

        //Floating action 버튼 구현 (test notification 생성하는 기능으로)
        testCreateNotificationChannel(channelID, "Test Notification", "Test Notification Channel");

        FloatingActionButton buttonAddNote = findViewById(R.id.floatingActionButton);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("MVVM_TAG", "[MainActivity] - FloatingActionButton() - 추가버튼 ");
//                Intent intent = new Intent(AlarmCenterActivity.this, NotificationViewer.class);
//                startActivityForResult(intent, ADD_NOTE_REQUEST);

                testCreateNotification();
            }
        });

        //Broadcast receiver처리
        receiver = new NotificationBroadcaseReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.alarmcenter");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MVVM_TAG", "[MainActivity] - onDestroy()  unregisterReceiver");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MVVM_TAG", "[MainActivity] - onCreateOptionsMenu()");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("MVVM_TAG", "[MainActivity] - onOptionsItemSelected()");
        if (item.getItemId() == R.id.delete_all) {
            notificationViewModel.deleteAllNotes(this);
            Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class NotificationBroadcaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NotificationEntity notification = (NotificationEntity) intent.getSerializableExtra("notification_listener_event");
            Log.d("MVVM_TAG", "NotificationBroadcaseReceiver () onReceive :  note.title = " + notification.getTitle());

            notificationViewModel.insert(notification, AlarmCenterActivity.this);
        }
    }

    private boolean isNotiPermissionAllowed() {
        if (ActivityCompat.checkSelfPermission(AlarmCenterActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AlarmCenterActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }

        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for (String packageName : notiListenerSet) {
            if (packageName == null) {
                continue;
            }
            if (packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

    private void testCreateNotification() {
        Intent mainIntent = new Intent(getApplicationContext(), AlarmCenterActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder newMessageNotification = new NotificationCompat.Builder(this, channelID);
        newMessageNotification.setSmallIcon(android.R.drawable.ic_dialog_info);
        newMessageNotification.setContentTitle("Notification_title");
        newMessageNotification.setContentText("This is a test message");
        newMessageNotification.setAutoCancel(true);
        newMessageNotification.setContentIntent(mainPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(101, newMessageNotification.build());
    }
    protected void testCreateNotificationChannel(String id, String name, String description) {

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, name, importance);

        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400,
                500, 400, 300, 200, 400});

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}