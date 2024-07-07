package com.example.alarmclock_codsoft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_alarms) {
                selectedFragment = new AlarmsFragment();
            }
            if (selectedFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, selectedFragment);
                transaction.commit();
            }
            return true;
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
    public void onSnoozeClick(View view) {
        // Handle snooze action here
        Intent snoozeIntent = new Intent(this, AlarmReceiver.class);
        snoozeIntent.setAction("com.example.alarmclock_codsoft.ALARM_SNOOZE");
        // Add necessary extras if any
        sendBroadcast(snoozeIntent);
    }

    public void onDismissClick(View view) {
        // Handle dismiss action here
        Intent dismissIntent = new Intent(this, AlarmReceiver.class);
        dismissIntent.setAction("com.example.alarmclock_codsoft.ALARM_DISMISS");
        // Add necessary extras if any
        sendBroadcast(dismissIntent);
    }
    public void pushNOT(){
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channelID);
        builder.setSmallIcon(R.drawable.ic_nt);
        builder.setContentTitle("Alarm");
        builder.setContentText("this is ur alarms");
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

}
