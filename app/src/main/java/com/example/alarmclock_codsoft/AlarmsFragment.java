package com.example.alarmclock_codsoft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private AlarmAdapter alarmAdapter;
    private AlarmDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        emptyView = rootView.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dbHelper = new AlarmDatabaseHelper(requireContext());
        loadAlarmsFromDB();

        return rootView;
    }

    private void loadAlarmsFromDB() {
        List<Alarm> alarms = dbHelper.getAllAlarms();

        if (alarms.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            alarmAdapter = new AlarmAdapter(requireContext(), alarms);
            recyclerView.setAdapter(alarmAdapter);
        }
    }
}
