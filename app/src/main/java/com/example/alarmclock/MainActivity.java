package com.example.alarmclock;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SET_ALARM = 1;

    private TextView textViewTime, textViewDate;
    private Button buttonAddAlarm;
    private RecyclerView recyclerViewAlarms;

    private ArrayList<AlarmModel> alarmList;
    private AlarmAdapter alarmAdapter;

    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }


        textViewTime = findViewById(R.id.textViewTime);
        textViewDate = findViewById(R.id.textViewDate);
        buttonAddAlarm = findViewById(R.id.buttonAddAlarm);
        recyclerViewAlarms = findViewById(R.id.recyclerViewAlarms);

        updateTimeAndDate();


        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList, this, (position, alarm) -> {

            editingPosition = position;
            Intent intent = new Intent(MainActivity.this, SetAlarmActivity.class);
            intent.putExtra("hour", alarm.getHour());
            intent.putExtra("minute", alarm.getMinute());
            intent.putExtra("year", alarm.getYear());
            intent.putExtra("month", alarm.getMonth());
            intent.putExtra("day", alarm.getDay());
            intent.putExtra("isEditing", true);
            startActivityForResult(intent, REQUEST_CODE_SET_ALARM);
        });

        recyclerViewAlarms.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlarms.setAdapter(alarmAdapter);


        buttonAddAlarm.setOnClickListener(view -> {
            editingPosition = -1; // New alarm
            Intent intent = new Intent(MainActivity.this, SetAlarmActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SET_ALARM);
        });
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());

        textViewTime.setText(timeFormat.format(calendar.getTime()));
        textViewDate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SET_ALARM && resultCode == RESULT_OK && data != null) {
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            int year = data.getIntExtra("year", 0);
            int month = data.getIntExtra("month", 0);
            int day = data.getIntExtra("day", 0);

            AlarmModel alarmModel = new AlarmModel(hour, minute, year, month, day, true);

            if (editingPosition >= 0) {

                alarmList.set(editingPosition, alarmModel);
                alarmAdapter.notifyItemChanged(editingPosition);
                editingPosition = -1;
            } else {

                alarmList.add(alarmModel);
                alarmAdapter.notifyItemInserted(alarmList.size() - 1);
            }

            AlarmUtils.scheduleAlarm(this, alarmModel);
        }
    }
}
