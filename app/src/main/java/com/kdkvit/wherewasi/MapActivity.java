package com.kdkvit.wherewasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import models.LocationsGroup;
import models.MyLocation;

import static com.kdkvit.wherewasi.fragments.ActivityFragment.locations;

public class MapActivity extends AppCompatActivity {

    private View infoContainer;
    private GoogleMap googleMap;
    LocationsGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        int groupNum = intent.getIntExtra("locations_group",0);

        ImageButton backBtn = ((ImageButton) findViewById(R.id.menu_btn));
        backBtn.setImageResource(R.drawable.ic_arrow_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        group = locations.get(groupNum);

        infoContainer = findViewById(R.id.info_container);
        infoContainer.setVisibility(View.GONE);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap gMap) {
            googleMap = gMap;

            setMapPointers();
        }
    };

    public void setMapPointers() {
        if (googleMap != null) {
            googleMap.clear();

            if (group.locationsSize() > 0) {
                PolylineOptions polylineOptions = new PolylineOptions();
                googleMap.addMarker(new MarkerOptions().position(group.getLastLocation().getLatLng()).title(group.getLastLocation().getAddressLine()));
                MyLocation firstLocation = null;

                for(MyLocation location : group.getLocations()){
                    polylineOptions.add(location.getLatLng());
                    firstLocation = location;
                    CircleOptions circleOptions = new CircleOptions()
                            .center(location.getLatLng())
                            .radius(0.2) // radius in meters
                            .fillColor(Color.BLACK); //this is a half transparent blue, change "88" for the transparency
                    googleMap.addCircle(circleOptions);
                }
                if(firstLocation !=null) {
                    IconGenerator iconGenerator = new IconGenerator(this);
                    iconGenerator.setBackground(this.getDrawable(R.drawable.map_dot));

                    googleMap.addMarker(new MarkerOptions().position(firstLocation.getLatLng()).title(firstLocation.getAddressLine()));
                }
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(5);
                polylineOptions.geodesic(true);
                googleMap.addPolyline(polylineOptions);
                focus(group.getLastLocation());
            }
        }
    }

    public void focus(MyLocation myLocation) {
        LatLng mapPoint = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        float zoom = 15.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoint,zoom));
    }

}