package com.kdkvit.wherewasi.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.adapters.LocationsTabsAdapter;

import java.util.ArrayList;
import java.util.List;

import models.MyLocation;
import utils.DatabaseHandler;

import static com.kdkvit.wherewasi.services.LocationService.BROADCAST_CHANNEL;


public class ActivityFragment extends Fragment {
    BroadcastReceiver receiver;
    DatabaseHandler db;

    TimeLineFragment timeLineFragment = new TimeLineFragment(new TimeLineFragment.TimeLineLocationListener() {
        @Override
        public void onClick(int position) {
            onTLLocationClick(position);
        }
    });

    MapsFragment mapsFragment = new MapsFragment();

    public static List<MyLocation> locations = new ArrayList<>();

    boolean dbInit = false;
    Handler handler;
    private TabLayout tabLayout;

    private View rootView;


    public ActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_activity, container, false);
        handler = new Handler();
        db = new DatabaseHandler(rootView.getContext());

        assert getFragmentManager() != null;
        LocationsTabsAdapter locationsTabsAdapter = new LocationsTabsAdapter(getFragmentManager(),1);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.locations_view_pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.locations_tab_layout);

        locationsTabsAdapter.addFragment(timeLineFragment,"TimeLine");
        locationsTabsAdapter.addFragment(mapsFragment,"Map");

        viewPager.setAdapter(locationsTabsAdapter);
        tabLayout.setupWithViewPager(viewPager);

        initReceiver();

        getLocationsHistory();

        return rootView;
    }

    private  void getLocationsHistory(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                locations = db.getAllLocations(DatabaseHandler.SORTING_PARAM.LastUpdated);

                dbInit = true;
                handler.post(()-> {
                    timeLineFragment.updateTimeLineAdapter();
                    mapsFragment.setMapPointers();
                });
            }
        }.start();
    }

    private void onTLLocationClick(int position) {
        tabLayout.getTabAt(1).select();
        mapsFragment.focus(locations.get(position),false);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(BROADCAST_CHANNEL);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                if (command != null) {
                    switch (command) {
                        case "new_location":
                            if(dbInit){
                                MyLocation location = (MyLocation) intent.getSerializableExtra("location");
                                locations.add(0,location);
                                timeLineFragment.updateTimeLineAdapter();
                                mapsFragment.setMapPointers();
                            }
                        case "location_changed":
                            if(dbInit) {
                                MyLocation location = (MyLocation)intent.getSerializableExtra("location");
                                Log.i("changed","location");
                                locations.remove(0);
                                locations.add(0,location);
                                timeLineFragment.updateTimeLineAdapter();
                                mapsFragment.setMapPointers();
                            }
                            break;
                        case "close":
                            break;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(receiver);
        super.onDestroyView();
    }
}