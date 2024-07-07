package com.example.alarmclock_codsoft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView digitalClock;
    private Handler handler;
    private ImageButton addAlarmButton;
    private Calendar selectedTime;
    private Uri selectedRingtoneUri;
    private AlarmDatabaseHelper dbHelper;

    private final ActivityResultLauncher<Intent> ringtonePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedRingtoneUri = result.getData().getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Log.d("HomeFragment", "Ringtone selected: " + selectedRingtoneUri);
                    showTimePickerDialog();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        digitalClock = rootView.findViewById(R.id.digitalClock);
        addAlarmButton = rootView.findViewById(R.id.btnAddAlarm);
        handler = new Handler(Looper.getMainLooper());

        startDigitalClockUpdate();

        addAlarmButton.setOnClickListener(v -> showRingtonePickerDialog());

        return rootView;
    }

    private void startDigitalClockUpdate() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                digitalClock.setText(getCurrentTimeWithAmPm());
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    private String getCurrentTimeWithAmPm() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showRingtonePickerDialog() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        ringtonePickerLauncher.launch(intent);
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute1) -> {
                    selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute1);

                    Log.d("HomeFragment", "Selected Time: " + hourOfDay + ":" + minute1);
                    Log.d("HomeFragment", "Selected Ringtone URI: " + selectedRingtoneUri);

                    setAlarm(selectedTime, selectedRingtoneUri);
                },
                hour,
                minute,
                false);

        timePickerDialog.show();
    }

    private void setAlarm(Calendar calendar, Uri ringtoneUri) {
        // Schedule the alarm using AlarmScheduler
        AlarmScheduler.scheduleExactAlarm(requireContext(), calendar, ringtoneUri);

        // Create an Alarm object with isEnabled set to true
        Alarm alarm = new Alarm(0, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), ringtoneUri.toString(), true);

        // Insert the alarm into the database using AlarmDatabaseHelper
        long alarmId = dbHelper.insertAlarm(alarm);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Toast.makeText(requireContext(), "Alarm set for " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
    public void snoozeAlarm(View view) {
        AlarmReceiver.stopAlarm(requireContext());
        AlarmScheduler.scheduleSnoozeAlarm(requireContext(), selectedRingtoneUri);
        Toast.makeText(requireContext(), "Alarm snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
    }

    public void dismissAlarm(View view) {
        AlarmReceiver.stopAlarm(requireContext());
        Toast.makeText(requireContext(), "Alarm dismissed", Toast.LENGTH_SHORT).show();
    }

}
