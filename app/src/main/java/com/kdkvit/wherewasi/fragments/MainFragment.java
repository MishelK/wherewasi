package com.kdkvit.wherewasi.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.material.button.MaterialButton;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.adapters.NotificationsAdapter;
import com.kdkvit.wherewasi.services.BtAdvertiserService;
import com.kdkvit.wherewasi.services.SoniTalkService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import models.MyNotification;
import utils.DatabaseHandler;
import utils.NotificationCenter;

import static com.kdkvit.wherewasi.MainActivity.user;
import static utils.NotificationCenter.NOTIFICATIONS_RECEIVER;

public class MainFragment extends Fragment {

    public static final long MILLIS_IN_DAY = 86400000;
    public static List<MyNotification> notifications = new ArrayList<>();
    BroadcastReceiver receiver;
    private boolean listening = false;

    private View rootView;
    NotificationsAdapter adapter = new NotificationsAdapter(getContext());
    private BroadcastReceiver soniTalkReceiver;
    private MaterialButton listenBtn;


    public MainFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView nameTV = rootView.findViewById(R.id.name_tv);
        nameTV.setText(getResources().getString(R.string.hello_name)+ " " +user.getName());

        initNotifications();
        initInteractionChart();
        initLocationChart();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.notifications_recycler);
        NotificationsAdapter adapter = new NotificationsAdapter(getContext());

        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);

        MaterialButton button = rootView.findViewById(R.id.send_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), SoniTalkService.class);
                intent.putExtra("command", "start");
                //view.getContext().startService(intent);
                rootView.getContext().startService(intent);
            }
        });
        listenBtn = rootView.findViewById(R.id.listen);
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listening = !listening;
                if(listening){
                    listenBtn.setText("Stop");
                }else{
                    listenBtn.setText("Listen");
                }
                Intent intent = new Intent(rootView.getContext(), SoniTalkService.class);
                intent.putExtra("command", "start_listening");
                //view.getContext().startService(intent);
                rootView.getContext().startService(intent);

            }
        });
        initReceiver();

        return rootView;
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(NOTIFICATIONS_RECEIVER);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean command = intent.getBooleanExtra("new_notification",false);
                if(command){
                    initNotifications();
                    adapter.notifyDataSetChanged();
                }
            }
        };

        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(receiver, filter);

        IntentFilter soniFilter = new IntentFilter(SoniTalkService.SONITALK_RECEIVER);
        soniTalkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                if(command.equals("stop_listening")){
                    listening = !listening;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            listenBtn.setText("Listen");
                        }
                    });
                }
            }
        };
        LocalBroadcastManager.getInstance(rootView.getContext()).registerReceiver(soniTalkReceiver,soniFilter);
    }

    private void initNotifications() {
        notifications =  NotificationCenter.generateDailyNotifications(getContext());
        if(notifications.size() == 0){
            notifications.add(new MyNotification(0, getString(R.string.no_notifications)));
        }
    }


    public void initInteractionChart() {

        AnyChartView anyChartView = rootView.findViewById(R.id.chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(getContext());
        // Getting interactions from db and adding to arrayList
        for (int i = 13; i>=0; i--){
            long currentTime = System.currentTimeMillis();
            long dateInMillis = currentTime - (i * MILLIS_IN_DAY);
            int num = db.getNumOfInteractionsOnDay(dateInMillis,false);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMillis);

            data.add(new ValueDataEntry(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH), num));
        }

        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("Interactions: {%Value}{groupsSeparator: }");
        column.color("#f95700");

        cartesian.animation(true);
        cartesian.title("14 day Interactions");
        

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("{%Value}{numDecimals:0}");


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Date");

        anyChartView.setChart(cartesian);

    }

    public void initLocationChart() {

        AnyChartView anyChartView = rootView.findViewById(R.id.chart_view2);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(getContext());
        // Getting interactions from db and adding to arrayList
        for (int i = 13; i>=0; i--){
            long currentTime = (new Date()).getTime();
            long dateInMillis = currentTime - (i * MILLIS_IN_DAY);
            int num = db.getLocationsOnDay(dateInMillis);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMillis);

            data.add(new ValueDataEntry(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH), num));
        }

        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("Locations: {%Value}{groupsSeparator: }");
        column.color("#f95700");

        cartesian.animation(true);
        cartesian.title("14 day Locations");

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("{%Value}{numDecimals:0}");


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Date");

        anyChartView.setChart(cartesian);

    }


    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(rootView.getContext()).unregisterReceiver(soniTalkReceiver);
        super.onDestroyView();
    }
}