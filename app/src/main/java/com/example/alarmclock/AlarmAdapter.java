package com.example.alarmclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private final ArrayList<AlarmModel> alarmList;
    private final Context context;
    private final OnItemEditListener editListener;

    public interface OnItemEditListener {
        void onEdit(int position, AlarmModel alarm);
    }

    public AlarmAdapter(ArrayList<AlarmModel> alarmList, Context context, OnItemEditListener editListener) {
        this.alarmList = alarmList;
        this.context = context;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmModel alarm = alarmList.get(position);
        holder.textViewAlarmTime.setText(alarm.getFormattedTime() + "\n" + alarm.getFormattedDate());

        holder.switchEnable.setOnCheckedChangeListener(null);
        holder.switchEnable.setChecked(alarm.isEnabled());

        holder.switchEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);
            if (isChecked) {
                AlarmUtils.scheduleAlarm(context, alarm);
            } else {
                AlarmUtils.cancelAlarm(context, alarm);
            }
        });

        holder.buttonEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(holder.getAdapterPosition(), alarm);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            AlarmUtils.cancelAlarm(context, alarm);
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition >= 0 && adapterPosition < alarmList.size()) {
                alarmList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, alarmList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAlarmTime;
        Switch switchEnable;
        ImageButton buttonEdit, buttonDelete;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAlarmTime = itemView.findViewById(R.id.textViewAlarmTime);
            switchEnable = itemView.findViewById(R.id.switchEnable);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
