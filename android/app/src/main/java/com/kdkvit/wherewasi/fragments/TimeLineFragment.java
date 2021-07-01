package com.kdkvit.wherewasi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.adapters.LocationsAdapter;

public class TimeLineFragment extends Fragment {

    private View rootView;
    private LocationsAdapter locationsAdapter;

    public interface TimeLineLocationListener{
        void onClick(int position);
    }

    TimeLineLocationListener listener;

    public TimeLineFragment(TimeLineLocationListener listener) {
        locationsAdapter = new LocationsAdapter();
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_time_line, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.timeline_recycler);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        locationsAdapter.setListener(new LocationsAdapter.LocationListener() {
            @Override
            public void onClick(int position) {
                if(listener!=null){
                    listener.onClick(position);
                }
            }
        });
        recyclerView.setAdapter(locationsAdapter);

        return rootView;
    }

    public void updateTimeLineAdapter() {
        if (locationsAdapter != null) {
            locationsAdapter.notifyDataSetChanged();
        }
    }
}