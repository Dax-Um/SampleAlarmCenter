package com.example.alarmcenter.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarmcenter.Models.Room.NotificationEntity;
import com.example.alarmcenter.R;
import com.example.alarmcenter.ViewModels.NotificationViewModel;

import java.text.SimpleDateFormat;

public class NotificationViewer  extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.alarmcenter.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.alarmcenter.EXTRA_TITLE";
    public static final String EXTRA_CONTEXT = "com.example.alarmcenter.EXTRA_CONTEXT";
    public static final String EXTRA_PACKAGE = "com.example.alarmcenter.EXTRA_PACKAGE";
    public static final String EXTRA_POSTEDDATE = "com.example.alarmcenter.EXTRA_POSTEDDATE";
    private TextView tv_title, tv_context, tv_package, tv_date;
    Intent view_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MVVM_TAG", "[AddEditNoteActivity] - onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        tv_title = findViewById(R.id.tv_title);
        tv_context = findViewById(R.id.tv_context);
        tv_package = findViewById(R.id.tv_package);
        tv_date = findViewById(R.id.tv_date);

        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar); //액티비티의 앱바(App Bar)로 지정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);

        view_intent = getIntent();
        Long lowDate = view_intent.getLongExtra(EXTRA_POSTEDDATE, 0);
        actionBar.setTitle("View Notification");
        tv_title.setText(view_intent.getStringExtra(EXTRA_TITLE));
        tv_context.setText(view_intent.getStringExtra(EXTRA_CONTEXT));
        tv_package.setText(view_intent.getStringExtra(EXTRA_PACKAGE));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String postedDate = simpleDateFormat.format(lowDate);
        tv_date.setText(String.valueOf(postedDate));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MVVM_TAG", "[AddEditNoteActivity] - onCreateOptionsMenu()");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("MVVM_TAG", "[AddEditNoteActivity] - onOptionsItemSelected()");
        if(item.getItemId() == R.id.delete_notification) {
            delete_Notification();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete_Notification() {
        NotificationViewModel notificationViewModel = new ViewModelProvider
                .AndroidViewModelFactory(this.getApplication())
                .create(NotificationViewModel.class);

        NotificationEntity notification = new NotificationEntity(
                view_intent.getStringExtra(EXTRA_TITLE),
                view_intent.getStringExtra(EXTRA_CONTEXT),
                view_intent.getStringExtra(EXTRA_PACKAGE),
                view_intent.getLongExtra(EXTRA_POSTEDDATE, 0));
        notification.setId(view_intent.getIntExtra(EXTRA_ID, 0));

        notificationViewModel.delete(notification, this);
        finish();
    }

//    private void saveNote() {
//        Log.d("MVVM_TAG", "[AddEditNoteActivity] - saveNote()");
//        String title = editTextTitle.getText().toString();
//        String description = editTextDescription.getText().toString();
//        int priority = numberPickerPriority.getValue();
//
//        if(title.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Please insert data", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Intent data = new Intent();
//        data.putExtra(EXTRA_TITLE, title);
//        data.putExtra(EXTRA_CONTEXT, description);
//        data.putExtra(EXTRA_PRIORITY, priority);
//
//        int id = getIntent().getIntExtra(EXTRA_ID, -1);
//        if (id != -1) {
//            data.putExtra(EXTRA_ID, id);
//        }
//        setResult(RESULT_OK, data);
//        finish();
//    }
}
