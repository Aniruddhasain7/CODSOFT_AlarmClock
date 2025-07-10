package com.example.alarmclock;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {

    private Button buttonPickTime, buttonPickDate;
    private int selectedHour = -1, selectedMinute = -1;
    private int selectedYear, selectedMonth, selectedDay;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        buttonPickTime = findViewById(R.id.buttonPickTime);
        buttonPickDate = findViewById(R.id.buttonPickDate);

        Calendar today = Calendar.getInstance();
        selectedYear = today.get(Calendar.YEAR);
        selectedMonth = today.get(Calendar.MONTH);
        selectedDay = today.get(Calendar.DAY_OF_MONTH);

        // Check if editing
        Intent intent = getIntent();
        boolean isEditing = intent.getBooleanExtra("isEditing", false);
        if (isEditing) {
            selectedHour = intent.getIntExtra("hour", today.get(Calendar.HOUR_OF_DAY));
            selectedMinute = intent.getIntExtra("minute", today.get(Calendar.MINUTE));
            selectedYear = intent.getIntExtra("year", today.get(Calendar.YEAR));
            selectedMonth = intent.getIntExtra("month", today.get(Calendar.MONTH));
            selectedDay = intent.getIntExtra("day", today.get(Calendar.DAY_OF_MONTH));

            buttonPickDate.setText("Date: " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
            buttonPickTime.setText(String.format("Time: %02d:%02d", selectedHour, selectedMinute));
        } else {
            buttonPickDate.setText("Pick Date");
            buttonPickTime.setText("Pick Time");
        }

        buttonPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SetAlarmActivity.this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = dayOfMonth;
                        buttonPickDate.setText("Date: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    selectedYear, selectedMonth, selectedDay
            );
            datePickerDialog.show();
        });

        buttonPickTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            if (selectedHour != -1) {
                hour = selectedHour;
                minute = selectedMinute;
            }

            TimePickerDialog dialog = new TimePickerDialog(
                    SetAlarmActivity.this,
                    (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                        selectedHour = hourOfDay;
                        selectedMinute = minuteOfHour;

                        buttonPickTime.setText(String.format("Time: %02d:%02d", selectedHour, selectedMinute));

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("hour", selectedHour);
                        resultIntent.putExtra("minute", selectedMinute);
                        resultIntent.putExtra("year", selectedYear);
                        resultIntent.putExtra("month", selectedMonth);
                        resultIntent.putExtra("day", selectedDay);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    },
                    hour, minute, false
            );

            dialog.show();
        });
    }
}
