package com.kdkvit.wherewasi.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.anychart.graphics.vector.Fill;
import com.google.android.material.navigation.NavigationView;
import com.kdkvit.wherewasi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utils.DatabaseHandler;
import utils.InteractionDatabaseHandler;

import static com.kdkvit.wherewasi.MainActivity.user;

public class MainFragment extends Fragment {

    public static final long MILLIS_IN_DAY = 86400000;

    private View rootView;

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
        nameTV.setText(getResources().getString(R.string.hello_name)+user.getName());

        initInteractionChart();
        initLocationChart();

        return rootView;
    }


    public void initInteractionChart() {

        AnyChartView anyChartView = rootView.findViewById(R.id.chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(getContext());
        // Getting interactions from db and adding to arrayList
        for (int i = 6; i>=0; i--){
            long currentTime = System.currentTimeMillis();
            long dateInMillis = currentTime - (i * MILLIS_IN_DAY);
            int num = db.getNumOfInteractionsOnDay(dateInMillis);

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
        cartesian.title("Weekly Interactions");
        

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("{%Value}{numDecimals:0}");


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Date");
        //cartesian.yAxis(0).title("Interactions");


        anyChartView.setChart(cartesian);

    }

    public void initLocationChart() {

        AnyChartView anyChartView = rootView.findViewById(R.id.chart_view2);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(getContext());
        // Getting interactions from db and adding to arrayList
        for (int i = 6; i>=0; i--){
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
        cartesian.title("Weekly Locations");

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("{%Value}{numDecimals:0}");


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Date");
        //cartesian.yAxis(0).title("Interactions");

        anyChartView.setChart(cartesian);

    }

}