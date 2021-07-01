package com.kdkvit.wherewasi.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kdkvit.wherewasi.MapActivity;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.adapters.LocationsTabsAdapter;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.LocationsGroup;
import models.MyLocation;
import utils.DatabaseHandler;

import static com.kdkvit.wherewasi.services.LocationService.BROADCAST_CHANNEL;
import static com.kdkvit.wherewasi.utils.General.checkIfLocationInGroup;


public class ActivityFragment extends Fragment {
    BroadcastReceiver receiver;
    DatabaseHandler db;

    TimeLineFragment timeLineFragment = new TimeLineFragment(new TimeLineFragment.TimeLineLocationListener() {
        @Override
        public void onClick(int position) {
            onTLLocationClick(position);
        }
    });

    public static List<LocationsGroup> locations = new ArrayList<>();

    boolean dbInit = false;
    Handler handler;
    private TabLayout tabLayout;

    private View rootView;
    private DrawerLayout drawerLayout;
    ProgressBar progressBar;
    private FrameLayout loaderLayout;


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

        getFragmentManager().beginTransaction().replace(R.id.activity_fragment_container, timeLineFragment).commit();

        final FiltersFragment filtersFragment = new FiltersFragment(new FiltersFragment.FiltersCallback() {
            @Override
            public void onClear() {

            }

            @Override
            public void onFilter(Date start, Date end, int minTime, boolean onlyInteractions) {
                drawerLayout.closeDrawer(GravityCompat.END);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        if(end!=null){
                            end.setTime(end.getTime() + 24 * 60 * 60 * 1000); //Add 24 hours to include the day
                        }
                        locations = db.getAllLocations(start,end,minTime,onlyInteractions);

                        dbInit = true;
                        handler.post(()-> {
                            timeLineFragment.updateTimeLineAdapter();
                            //mapsFragment.setMapPointers();
                        });
                    }
                }.start();
            }

        });

        getFragmentManager().beginTransaction().replace(R.id.activity_fragment_container, timeLineFragment).commit();
        getFragmentManager().beginTransaction().replace(R.id.activity_filters_fragment_container, filtersFragment).commit();

        initReceiver();

        drawerLayout = rootView.findViewById(R.id.activity_filters_drawer);

        loaderLayout = rootView.findViewById(R.id.spinning_loader);

        getLocationsHistory();

        return rootView;
    }

    public void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private  void getLocationsHistory(){
        loaderLayout.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                locations = db.getAllLocations(null,null,0,false);

                dbInit = true;
                handler.post(()-> {
                    timeLineFragment.updateTimeLineAdapter();
                    loaderLayout.setVisibility(View.GONE);
                    //mapsFragment.setMapPointers();
                });
            }
        }.start();
    }

    private void onTLLocationClick(int position) {
        Intent intent = new Intent(rootView.getContext(), MapActivity.class);
        intent.putExtra("locations_group",position);
        startActivity(intent);
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

                            }
                        case "location_changed":
                            if(dbInit) {

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