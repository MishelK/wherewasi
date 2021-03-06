package com.kdkvit.wherewasi.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.kdkvit.wherewasi.R;

import java.util.ArrayList;
import java.util.List;

import models.MyLocation;

import static com.kdkvit.wherewasi.LocationsActivity.locations;

public class MapsFragment extends Fragment {

    GoogleMap googleMap;
    private ClusterManager<MyLocation> mClusterManager;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void setMapPointers() {
        if (googleMap != null && getContext()!=null) {
            googleMap.clear();
            mClusterManager = new ClusterManager<MyLocation>(getContext(), googleMap);
            googleMap.setOnCameraIdleListener(mClusterManager);
            googleMap.setOnMarkerClickListener(mClusterManager);
            googleMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    final LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.custom_info_window, null);
                    final TextView textView = view.findViewById(R.id.textViewTitle);
                    String text = (marker.getTitle() != null) ? marker.getTitle() : "Cluster Item";
                    textView.setText(text);
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyLocation>() {
                @Override
                public boolean onClusterClick(Cluster<MyLocation> cluster) {
                    List<MyLocation> cLocations = new ArrayList<>(cluster.getItems());

                    return true;
                }
            });

            initCluster();
//            if (locations.size() > 0) {
//                LatLng mapPoint = null;
//                for (MyLocation location : locations) {
//                    mapPoint = new LatLng(location.getLatitude(), location.getLongitude());
//                    googleMap.addMarker(new MarkerOptions().position(mapPoint).title(location.getAddressLine()));
//                }
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoint,16.0f));
//            }
        }
    }

    private void initCluster() {
        mClusterManager.clearItems();
        mClusterManager.addItems(locations);
        mClusterManager.cluster();
    }
}